import React from "react";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import Calculator from "./Calculator";
import API from "../../utils/API";

jest.mock("../../utils/API", () => ({
  calculate: jest.fn(),
  generateString: jest.fn()
}));

describe("Calculator", () => {
  const user = { balance: 10 };
  const onUserChange = jest.fn();

  beforeEach(() => jest.clearAllMocks());

  it("submits an operation, shows loading, and updates the result and balance", async () => {
    let resolveRequest;
    API.calculate.mockReturnValue(new Promise((resolve) => { resolveRequest = resolve; }));
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.click(screen.getByRole("button", { name: "2" }));
    fireEvent.click(screen.getByRole("button", { name: "+" }));
    fireEvent.click(screen.getByRole("button", { name: "3" }));
    fireEvent.click(screen.getByRole("button", { name: "=" }));

    expect(screen.getAllByRole("button").at(-1)).toBeDisabled();
    expect(API.calculate).toHaveBeenCalledWith("+", "2", "3");

    resolveRequest({ data: { result: 5, message: "Operation completed", userBalance: 9.5 } });
    expect(await screen.findByText("Operation completed")).toBeInTheDocument();
    expect(screen.getAllByText("5").length).toBeGreaterThan(0);
    expect(onUserChange).toHaveBeenCalledWith({ ...user, balance: 9.5 });
    await waitFor(() => expect(screen.getAllByRole("button").at(-1)).toBeEnabled());
  });

  it("shows the API error and re-enables the calculator", async () => {
    API.calculate.mockRejectedValue({ response: { data: { message: "Insufficient balance" } } });
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.click(screen.getByRole("button", { name: "2" }));
    fireEvent.click(screen.getByRole("button", { name: "+" }));
    fireEvent.click(screen.getByRole("button", { name: "3" }));
    fireEvent.click(screen.getByRole("button", { name: "=" }));

    expect(await screen.findByText("Insufficient balance")).toBeInTheDocument();
    expect(screen.getAllByRole("button").at(-1)).toBeEnabled();
    expect(onUserChange).not.toHaveBeenCalled();
  });

  it("closes the Bootstrap alert with its in-layout close button", async () => {
    API.calculate.mockRejectedValue({ response: { data: { message: "Insufficient balance" } } });
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.click(screen.getByRole("button", { name: "2" }));
    fireEvent.click(screen.getByRole("button", { name: "+" }));
    fireEvent.click(screen.getByRole("button", { name: "3" }));
    fireEvent.click(screen.getByRole("button", { name: "=" }));

    expect(await screen.findByRole("alert")).toHaveTextContent("Insufficient balance");
    const closeButton = screen.getByRole("button", { name: "Close alert" });
    expect(closeButton).toHaveClass("btn-close");
    fireEvent.click(closeButton);

    await waitFor(() => expect(screen.queryByRole("alert")).not.toBeInTheDocument());
  });

  it("supports keyboard shortcuts for calculation and clearing", async () => {
    API.calculate.mockResolvedValue({ data: { result: 5, message: "Operation completed", userBalance: 9.5 } });
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.keyDown(window, { key: "2" });
    fireEvent.keyDown(window, { key: "+" });
    fireEvent.keyDown(window, { key: "3" });
    fireEvent.keyDown(window, { key: "Enter" });

    await waitFor(() => expect(API.calculate).toHaveBeenCalledWith("+", "2", "3"));
    expect(await screen.findByText("Operation completed")).toBeInTheDocument();

    fireEvent.keyDown(window, { key: "Escape" });
    expect(screen.getByText("0", { selector: ".calculator-display strong" })).toBeInTheDocument();
  });

  it("supports root and random string keyboard shortcuts", async () => {
    API.calculate.mockResolvedValue({ data: { result: 3, message: "Operation completed", userBalance: 9.5 } });
    API.generateString.mockResolvedValue({ data: { message: "abc123", userBalance: 9 } });
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.keyDown(window, { key: "9" });
    fireEvent.keyDown(window, { key: "r" });
    await waitFor(() => expect(API.calculate).toHaveBeenCalledWith("sqrt", "9", "0"));
    await screen.findByText("3", { selector: ".calculator-display strong" });

    fireEvent.keyDown(window, { key: "n" });
    await waitFor(() => expect(API.generateString).toHaveBeenCalledTimes(1));
  });
});
