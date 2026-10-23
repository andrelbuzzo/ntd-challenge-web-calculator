jest.mock("axios", () => {
  global.__apiRequest = {
    get: jest.fn(),
    post: jest.fn(),
    delete: jest.fn(),
    patch: jest.fn(),
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
    API.undeleteRecord("record-id");

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
    expect(request.patch).toHaveBeenCalledWith("/records/undelete", null, {
      params: { id: "record-id" }
    });
  });

  it("serializes multiple operation type filters as one query value", () => {
    API.records({ operationTypes: ["ADDITION", "DIVISION"] });

    expect(request.get).toHaveBeenCalledWith("/records", {
      params: {
        page: 0,
        size: 10,
        search: undefined,
        operationType: "ADDITION,DIVISION",
        sort: "createdAt,desc"
      }
    });
  });

  it("accepts comma-separated operation type input without leaking blank values", () => {
    API.records({ operationTypes: " ADDITION, ,DIVISION " });

    expect(request.get).toHaveBeenCalledWith("/records", expect.objectContaining({
      params: expect.objectContaining({ operationType: "ADDITION,DIVISION" })
    }));
  });
});
