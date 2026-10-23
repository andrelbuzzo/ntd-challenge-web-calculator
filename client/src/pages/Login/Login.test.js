import React from "react";
import "@testing-library/jest-dom";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "./Login";
import API from "../../utils/API";

jest.mock("../../utils/API", () => ({
  login: jest.fn()
}));

describe("Login", () => {
  const history = { replace: jest.fn() };

  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
    window.dispatchEvent = jest.fn();
  });

  it("validates credentials before calling the API", () => {
    render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><Login history={history} /></MemoryRouter>);

    fireEvent.submit(screen.getByRole("button", { name: "Log in" }));

    expect(screen.getByText("Please enter your email")).toBeInTheDocument();
    expect(API.login).not.toHaveBeenCalled();
  });

  it("stores the JWT and navigates after a successful login", async () => {
    API.login.mockResolvedValue({ data: { token: "test-token" } });
    render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><Login history={history} /></MemoryRouter>);

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: " user1@ntd.com " }
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "123456" }
    });
    fireEvent.submit(screen.getByRole("button", { name: "Log in" }));

    await waitFor(() => expect(history.replace).toHaveBeenCalledWith("/home"));
    expect(API.login).toHaveBeenCalledWith("user1@ntd.com", "123456");
    expect(JSON.parse(localStorage.getItem("user"))).toEqual({
      email: "user1@ntd.com",
      token: "test-token"
    });
  });

  it("shows the API error and re-enables the form after a failed login", async () => {
    API.login.mockRejectedValue({ response: { data: { message: "Invalid credentials" } } });
    render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><Login history={history} /></MemoryRouter>);

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "user1@ntd.com" }
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "123456" }
    });
    fireEvent.submit(screen.getByRole("button", { name: "Log in" }));

    expect(screen.getByDisplayValue("Signing in...")).toBeDisabled();
    expect(await screen.findByText("Invalid credentials")).toBeInTheDocument();
    expect(screen.getByDisplayValue("Log in")).toBeEnabled();
    expect(history.replace).not.toHaveBeenCalled();
  });

  it("distinguishes an unavailable API from invalid credentials", async () => {
    API.login.mockRejectedValue(new Error("Network Error"));
    render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><Login history={history} /></MemoryRouter>);

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "user1@ntd.com" }
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
      target: { value: "123456" }
    });
    fireEvent.submit(screen.getByRole("button", { name: "Log in" }));

    expect(await screen.findByText("Unable to reach the API. Start the server on port 8090 and try again.")).toBeInTheDocument();
    expect(screen.getByDisplayValue("Log in")).toBeEnabled();
  });
});
