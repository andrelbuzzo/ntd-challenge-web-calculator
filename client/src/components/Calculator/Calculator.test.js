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

    resolveRequest({ data: { result: 5, message: "Operação concluída", userBalance: 9.5 } });
    expect(await screen.findByText("Operação concluída")).toBeInTheDocument();
    expect(screen.getAllByText("5").length).toBeGreaterThan(0);
    expect(onUserChange).toHaveBeenCalledWith({ ...user, balance: 9.5 });
    await waitFor(() => expect(screen.getAllByRole("button").at(-1)).toBeEnabled());
  });

  it("shows the API error and re-enables the calculator", async () => {
    API.calculate.mockRejectedValue({ response: { data: { message: "Saldo insuficiente" } } });
    render(<Calculator user={user} onUserChange={onUserChange} />);

    fireEvent.click(screen.getByRole("button", { name: "2" }));
    fireEvent.click(screen.getByRole("button", { name: "+" }));
    fireEvent.click(screen.getByRole("button", { name: "3" }));
    fireEvent.click(screen.getByRole("button", { name: "=" }));

    expect(await screen.findByText("Saldo insuficiente")).toBeInTheDocument();
    expect(screen.getAllByRole("button").at(-1)).toBeEnabled();
    expect(onUserChange).not.toHaveBeenCalled();
  });
});
