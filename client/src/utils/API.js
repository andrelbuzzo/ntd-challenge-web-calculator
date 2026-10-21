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

api.interceptors.request.use((config) => {
  const user = getStoredUser();
  if (user?.token) {
    config.headers.Authorization = `Bearer ${user.token}`;
  }
  return config;
});

const API = {
  login: (username, password) => api.post("/users/login", { username, password }),
  currentUser: () => api.get("/users/me"),
  calculate: (operator, leftOperand, rightOperand) => api.get("/calculator", {
    params: { operator, leftOperand, rightOperand }
  }),
  generateString: () => api.get("/generate-string"),
  records: ({ page = 0, size = 10, search = "", operationType = "", sort = "createdAt,desc" } = {}) => api.get("/records", {
    params: { page, size, search: search || undefined, operationType: operationType || undefined, sort }
  }),
  deleteRecord: (id) => api.delete("/records", { params: { id } })
};

export default API;
