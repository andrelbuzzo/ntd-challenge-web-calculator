import React from "react";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import Records from "./Records";
import API from "../../utils/API";

jest.mock("../../utils/API", () => ({
  records: jest.fn(),
  deleteRecord: jest.fn()
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: jest.fn()
}));

const response = (content, overrides = {}) => ({
  data: { content, page: 0, size: 10, totalPages: 1, totalElements: content.length, first: true, last: true, ...overrides }
});

const record = { id: "r1", operationType: "ADDITION", leftOperand: 2, operator: "+", rightOperand: 3, operationResponse: "5", balanceBefore: 10, cost: 0.5, userBalance: 9.5, createdAt: "2026-01-01T10:00:00Z" };

const renderRecords = () => render(<MemoryRouter><Records /></MemoryRouter>);

describe("Records", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    require("react-router-dom").useNavigate.mockReturnValue(jest.fn());
    window.confirm = jest.fn(() => true);
    API.records.mockResolvedValue(response([record]));
    API.deleteRecord.mockResolvedValue({});
  });

  it("fetches and renders records, then searches with the entered text", async () => {
    renderRecords();
    expect(screen.getByText("Loading...")).toBeInTheDocument();
    expect(await screen.findByRole("cell", { name: "Addition" })).toBeInTheDocument();
    ["Date", "Operation", "Left operand", "Operator", "Right operand", "Response", "Balance before", "Cost", "Balance after", "Actions"].forEach((label) => {
      expect(screen.getByRole("columnheader", { name: label })).toBeInTheDocument();
    });
    expect(screen.getByRole("button", { name: "Delete record r1" })).toContainElement(document.querySelector(".fa-trash"));
    expect(API.records).toHaveBeenCalledWith({ page: 0, search: "", sort: "createdAt,desc" });

    fireEvent.change(screen.getByRole("textbox", { name: "Search records" }), { target: { value: "addition" } });
    fireEvent.click(screen.getByRole("button", { name: "Search" }));
    await waitFor(() => expect(API.records).toHaveBeenLastCalledWith({ page: 0, search: "addition", sort: "createdAt,desc" }));
  });

  it("deletes a confirmed record and reloads the current page", async () => {
    renderRecords();
    expect(await screen.findByRole("cell", { name: "Addition" })).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Delete record r1" }));
    await waitFor(() => expect(API.deleteRecord).toHaveBeenCalledWith("r1"));
    await waitFor(() => expect(API.records).toHaveBeenCalledWith({ page: 0, search: "", sort: "createdAt,desc" }));
    expect(window.confirm).toHaveBeenCalled();
  });

  it("loads the next page from pagination controls", async () => {
    API.records.mockResolvedValueOnce(response([record], { totalPages: 2, totalElements: 11, first: true, last: false }));
    renderRecords();
    await screen.findByRole("cell", { name: "Addition" });

    await userEvent.setup().click(screen.getByRole("button", { name: "Next" }));
    await waitFor(() => expect(API.records).toHaveBeenCalledWith({ page: 1, search: "", sort: "createdAt,desc" }));
  });

  it("shows a fetch error", async () => {
    API.records.mockRejectedValue({ response: { data: { message: "History unavailable" } } });
    renderRecords();
    expect(await screen.findByText("History unavailable")).toBeInTheDocument();
  });

  it("sends multiple selected operation types to the API", async () => {
    renderRecords();
    await screen.findByRole("cell", { name: "Addition" });
    fireEvent.click(screen.getByLabelText("Addition"));
    fireEvent.click(screen.getByLabelText("Division"));
    await waitFor(() => expect(API.records).toHaveBeenLastCalledWith({ page: 0, search: "", sort: "createdAt,desc", operationTypes: ["ADDITION", "DIVISION"] }));
  });
});
