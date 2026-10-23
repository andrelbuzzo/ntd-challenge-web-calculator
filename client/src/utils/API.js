import axios from "axios";

const getStoredUser = () => {
  try {
    return JSON.parse(localStorage.getItem("user") || "null");
  } catch {
    localStorage.removeItem("user");
    return null;
  }
};

const api = axios.create({
  baseURL: "/api/v1"
});

const serializeOperationTypes = (operationTypes, operationType) => {
  const values = Array.isArray(operationTypes)
    ? operationTypes
    : operationTypes ? [operationTypes] : operationType ? [operationType] : [];
  const normalized = values.flatMap((value) => String(value).split(","))
    .map((value) => value.trim())
    .filter(Boolean);
  return normalized.length ? normalized.join(",") : undefined;
};

api.interceptors.request.use((config) => {
  const user = getStoredUser();
  if (user?.token) {
    config.headers.Authorization = `Bearer ${user.token}`;
  }
  return config;
});

if (api.interceptors.response) {
  api.interceptors.response.use((response) => response, (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("user");
      window.dispatchEvent(new Event("auth-expired"));
    }
    return Promise.reject(error);
  });
}

const API = {
  login: (username, password) => api.post("/users/login", { username, password }),
  currentUser: () => api.get("/users/me"),
  calculate: (operator, leftOperand, rightOperand) => api.get("/calculator", {
    params: { operator, leftOperand, rightOperand }
  }),
  generateString: () => api.get("/generate-string"),
  records: ({ page = 0, size = 10, search = "", operationTypes = [], operationType = "", sort = "createdAt,desc" } = {}) => api.get("/records", {
    params: { page, size, search: search || undefined, operationType: serializeOperationTypes(operationTypes, operationType), sort }
  }),
  deleteRecord: (id) => api.delete("/records", { params: { id } }),
  undeleteRecord: (id) => api.patch("/records/undelete", null, { params: { id } })
};

export default API;
