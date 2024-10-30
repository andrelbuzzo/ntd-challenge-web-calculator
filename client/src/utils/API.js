import axios from "axios";

export default {
  login: (username, password) => 
    axios.post(`/api/v1/users/login`, {
      username: username,
      password: password
    }),
  add: (param1, param2) => 
    axios.get(`/api/v1/calculator/add?param1=${param1}&param2=${param2}`),
  subtract: (param1, param2) => 
    axios.get(`/api/v1/calculator/subtract?param1=${param1}&param2=${param2}`),
  multiply: (param1, param2) => 
    axios.get(`/api/v1/calculator/multiply?param1=${param1}&param2=${param2}`),
  divide: (param1, param2) => 
    axios.get(`/api/v1/calculator/divide?param1=${param1}&param2=${param2}`),
  calculate: (operator, leftOperand, rightOperand, token) => 
    axios.get(`/api/v1/calculator?operator=${encodeURIComponent(operator)}&leftOperand=${leftOperand}&rightOperand=${rightOperand}`, {
      headers: {'Authorization': 'Bearer ' + token}
    })
};
