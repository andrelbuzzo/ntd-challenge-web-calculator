import React from "react";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
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

const record = { id: "r1", operationType: "ADDITION", amount: "2 + 3", operationResponse: "5", cost: 0.5, userBalance: 9.5, createdAt: "2026-01-01T10:00:00Z" };

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
    expect(screen.getByText("Carregando...")).toBeInTheDocument();
    expect(await screen.findByText("ADDITION")).toBeInTheDocument();
    expect(API.records).toHaveBeenCalledWith({ page: 0, search: "", sort: "createdAt,desc" });

    fireEvent.change(screen.getByRole("textbox", { name: "Buscar registros" }), { target: { value: "addition" } });
    fireEvent.click(screen.getByRole("button", { name: "Buscar" }));
    await waitFor(() => expect(API.records).toHaveBeenLastCalledWith({ page: 0, search: "addition", sort: "createdAt,desc" }));
  });

  it("deletes a confirmed record and reloads the current page", async () => {
    renderRecords();
    expect(await screen.findByText("ADDITION")).toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Excluir" }));
    await waitFor(() => expect(API.deleteRecord).toHaveBeenCalledWith("r1"));
    await waitFor(() => expect(API.records).toHaveBeenCalledWith({ page: 0, search: "", sort: "createdAt,desc" }));
    expect(window.confirm).toHaveBeenCalled();
  });

  it("loads the next page from pagination controls", async () => {
    API.records.mockResolvedValueOnce(response([record], { totalPages: 2, totalElements: 11, first: true, last: false }));
    renderRecords();
    await screen.findByText("ADDITION");

    fireEvent.click(screen.getByRole("button", { name: "Próxima" }));
    await waitFor(() => expect(API.records).toHaveBeenCalledWith({ page: 1, search: "", sort: "createdAt,desc" }));
  });

  it("shows a fetch error", async () => {
    API.records.mockRejectedValue({ response: { data: { message: "Histórico indisponível" } } });
    renderRecords();
    expect(await screen.findByText("Histórico indisponível")).toBeInTheDocument();
  });

  it("sends multiple selected operation types to the API", async () => {
    renderRecords();
    await screen.findByText("ADDITION");
    fireEvent.click(screen.getByLabelText("Addition"));
    fireEvent.click(screen.getByLabelText("Division"));
    await waitFor(() => expect(API.records).toHaveBeenLastCalledWith({ page: 0, search: "", sort: "createdAt,desc", operationTypes: ["ADDITION", "DIVISION"] }));
  });
});
