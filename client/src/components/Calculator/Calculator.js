import React, { useState } from "react";
import { Alert, Button, Col, Row, Spinner } from "react-bootstrap";
import API from "../../utils/API";
import "./Calculator.css";

const keys = ["7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "±", "+"];

const Calculator = ({ user, onUserChange }) => {
  const [left, setLeft] = useState("");
  const [right, setRight] = useState("");
  const [operator, setOperator] = useState("");
  const [result, setResult] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const currentValue = operator ? right : left;

  const reset = () => { setLeft(""); setRight(""); setOperator(""); setResult(""); setMessage(""); setError(""); };

  const appendValue = (value) => {
    const setter = operator ? setRight : setLeft;
    const current = operator ? right : left;
    if (value === "." && current.includes(".")) return;
    setter(`${current}${value}`);
    setError("");
  };

  const toggleSign = () => {
    const setter = operator ? setRight : setLeft;
    const current = operator ? right : left;
    if (!current) return;
    setter(current.startsWith("-") ? current.slice(1) : `-${current}`);
  };

  const execute = (selectedOperator = operator, selectedRight = right) => {
    if (!left || !selectedOperator || !selectedRight) { setError("Informe os dois números antes de calcular."); return; }
    setLoading(true); setError("");
    API.calculate(selectedOperator, left, selectedRight)
      .then(({ data }) => { setResult(data.result); setMessage(data.message); onUserChange({ ...user, balance: data.userBalance }); setLeft(String(data.result)); setRight(""); setOperator(""); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Não foi possível executar a operação."))
      .finally(() => setLoading(false));
  };

  const squareRoot = () => {
    if (!left) { setError("Informe um número para calcular a raiz."); return; }
    setLoading(true); setError("");
    API.calculate("sqrt", left, "0")
      .then(({ data }) => { setResult(data.result); setMessage(data.message); onUserChange({ ...user, balance: data.userBalance }); setLeft(String(data.result)); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Não foi possível calcular a raiz."))
      .finally(() => setLoading(false));
  };

  const generateString = () => {
    setLoading(true); setError("");
    API.generateString()
      .then(({ data }) => { setResult(data.message); setMessage("String gerada com sucesso"); onUserChange({ ...user, balance: data.userBalance }); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Não foi possível gerar a string."))
      .finally(() => setLoading(false));
  };

  const handleKey = (key) => {
    if (["+", "-", "*", "/"].includes(key)) { if (!left) return; setOperator(key); setError(""); return; }
    if (key === "±") return toggleSign();
    appendValue(key);
  };

  return (
    <div className="calculator-card">
      <div className="calculator-topline"><div><p className="eyebrow">CALCULADORA</p><h1 id="calcTitle">Faça seu cálculo</h1></div><div className="balance-chip"><span>Saldo</span><strong>{Number(user.balance || 0).toFixed(2)}</strong></div></div>
      <div className="calculator-display"><span className="expression">{left || "0"} {operator} {right}</span><strong>{result || currentValue || "0"}</strong></div>
      {error && <Alert variant="danger" className="calculator-alert">{error}</Alert>}
      {message && !error && <Alert variant="success" className="calculator-alert">{message}</Alert>}
      <div className="calculator-actions"><Button variant="outline-dark" onClick={squareRoot} disabled={loading}>√ Raiz</Button><Button variant="outline-dark" onClick={generateString} disabled={loading}>Gerar string</Button><Button variant="outline-danger" onClick={reset} disabled={loading}>Limpar</Button></div>
      <Row className="keypad">{keys.map((key) => <Col xs={3} key={key}><Button className="key" variant={["+", "-", "*", "/", "±"].includes(key) ? "light" : "outline-light"} onClick={() => handleKey(key)}>{key}</Button></Col>)}<Col xs={12}><Button className="equals-key" variant="dark" onClick={() => execute()} disabled={loading}>{loading ? <Spinner size="sm" animation="border" /> : "="}</Button></Col></Row>
    </div>
  );
};

export default Calculator;
