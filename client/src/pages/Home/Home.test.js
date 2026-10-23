import React from "react";
import "@testing-library/jest-dom";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Home from "./Home";
import API from "../../utils/API";

jest.mock("../../utils/API", () => ({
  currentUser: jest.fn()
}));

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: jest.fn()
}));

const renderHome = () => render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><Home /></MemoryRouter>);

describe("Home", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    require("react-router-dom").useNavigate.mockReturnValue(jest.fn());
    jest.spyOn(window, "dispatchEvent");
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("loads the current user and renders the balance", async () => {
    API.currentUser.mockResolvedValue({ data: { balance: 12.5 } });

    renderHome();

    expect(screen.getByText("Loading balance...")).toBeInTheDocument();
    expect(await screen.findByText("Run a calculation")).toBeInTheDocument();
    expect(screen.getByText("12.50")).toBeInTheDocument();
    expect(API.currentUser).toHaveBeenCalledTimes(1);
  });

  it("clears an expired session and redirects to sign in on a 401", async () => {
    const navigate = jest.fn();
    API.currentUser.mockRejectedValue({ response: { status: 401 } });
    localStorage.setItem("user", JSON.stringify({ token: "expired-token" }));
    require("react-router-dom").useNavigate.mockReturnValue(navigate);

    renderHome();

    await waitFor(() => expect(navigate).toHaveBeenCalledWith("/?notice=auth&from=%2Fhome", { replace: true }));
    expect(localStorage.getItem("user")).toBeNull();
    expect(window.dispatchEvent).toHaveBeenCalledWith(expect.objectContaining({ type: "auth-expired" }));
  });
});
