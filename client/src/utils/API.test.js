jest.mock("axios", () => {
  global.__apiRequest = {
    get: jest.fn(),
    post: jest.fn(),
    delete: jest.fn(),
    interceptors: { request: { use: jest.fn() } }
  };
  return { create: jest.fn(() => global.__apiRequest) };
});

import API from "./API";

const request = global.__apiRequest;

describe("API client", () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  it("sends the login payload to the versioned endpoint", () => {
    API.login("user@example.com", "123456");

    expect(request.post).toHaveBeenCalledWith("/users/login", {
      username: "user@example.com",
      password: "123456"
    });
  });

  it("builds calculator and record requests with the API contract", () => {
    API.calculate("+", "2", "3");
    API.records({ page: 2, size: 10, search: "addition", sort: "cost,desc" });
    API.deleteRecord("record-id");

    expect(request.get).toHaveBeenNthCalledWith(1, "/calculator", {
      params: { operator: "+", leftOperand: "2", rightOperand: "3" }
    });
    expect(request.get).toHaveBeenNthCalledWith(2, "/records", {
      params: {
        page: 2,
        size: 10,
        search: "addition",
        operationType: undefined,
        sort: "cost,desc"
      }
    });
    expect(request.delete).toHaveBeenCalledWith("/records", {
      params: { id: "record-id" }
    });
  });
});
