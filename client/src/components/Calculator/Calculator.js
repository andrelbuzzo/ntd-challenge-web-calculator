import React, { useEffect, useState } from "react";
import { Alert, Spinner } from "react-bootstrap";
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

  useEffect(() => {
    if (!message && !error) return undefined;

    const timeoutId = window.setTimeout(() => {
      setMessage("");
      setError("");
    }, 4000);

    return () => window.clearTimeout(timeoutId);
  }, [message, error]);

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

  const removeLastValue = () => {
    if (operator && !right) {
      setOperator("");
      return;
    }

    const setter = operator ? setRight : setLeft;
    const current = operator ? right : left;
    setter(current.slice(0, -1));
    setError("");
  };

  const execute = (selectedOperator = operator, selectedRight = right) => {
    if (!left || !selectedOperator || !selectedRight) { setError("Enter both numbers before calculating."); return; }
    setLoading(true); setError("");
    API.calculate(selectedOperator, left, selectedRight)
      .then(({ data }) => { setResult(data.result); setMessage(data.message); onUserChange({ ...user, balance: data.userBalance }); setLeft(String(data.result)); setRight(""); setOperator(""); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Unable to execute the operation."))
      .finally(() => setLoading(false));
  };

  const squareRoot = () => {
    if (!left) { setError("Enter a number to calculate its square root."); return; }
    setLoading(true); setError("");
    API.calculate("sqrt", left, "0")
      .then(({ data }) => { setResult(data.result); setMessage(data.message); onUserChange({ ...user, balance: data.userBalance }); setLeft(String(data.result)); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Unable to calculate the square root."))
      .finally(() => setLoading(false));
  };

  const generateString = () => {
    setLoading(true); setError("");
    API.generateString()
      .then(({ data }) => { setResult(data.message); setMessage("String generated successfully"); onUserChange({ ...user, balance: data.userBalance }); })
      .catch((requestError) => setError(requestError.response?.data?.message || "Unable to generate the string."))
      .finally(() => setLoading(false));
  };

  const handleKey = (key) => {
    if (["+", "-", "*", "/"].includes(key)) { if (!left) return; setOperator(key); setError(""); return; }
    if (key === "±") return toggleSign();
    appendValue(key);
  };

  useEffect(() => {
    const handleShortcut = (event) => {
      if (event.target instanceof HTMLElement && ["INPUT", "TEXTAREA", "SELECT"].includes(event.target.tagName)) return;

      const key = event.key;
      if (/^[0-9.]$/.test(key) || ["+", "-", "*", "/"].includes(key)) {
        event.preventDefault();
        if (!loading) handleKey(key);
        return;
      }

      if (["Enter", "="].includes(key)) {
        event.preventDefault();
        if (!loading) execute();
        return;
      }

      if (key === "Escape") {
        event.preventDefault();
        if (!loading) reset();
        return;
      }

      if (key === "Backspace") {
        event.preventDefault();
        if (!loading) removeLastValue();
        return;
      }

      if (key.toLowerCase() === "r") {
        event.preventDefault();
        if (!loading) squareRoot();
        return;
      }

      if (key.toLowerCase() === "n") {
        event.preventDefault();
        if (!loading) generateString();
      }
    };

    window.addEventListener("keydown", handleShortcut);
    return () => window.removeEventListener("keydown", handleShortcut);
  });

  return (
    <section className="calculator-card" aria-labelledby="calcTitle">
      <h1 className="calculator-title" id="calcTitle">Run a calculation</h1>
      <div className="calculator-display" aria-live="polite"><span className="expression">{left || "0"} {operator} {right}</span><strong>{result || currentValue || "0"}</strong></div>
      {error && <Alert dismissible onClose={() => setError("")} variant="danger" className="calculator-alert" role="alert">{error}</Alert>}
      {message && !error && <Alert dismissible onClose={() => setMessage("")} variant="success" className="calculator-alert" role="status">{message}</Alert>}
      <div className="calculator-actions"><button type="button" onClick={squareRoot} disabled={loading} aria-label="Calculate square root" title="Square root (R)">√</button><button type="button" onClick={generateString} disabled={loading} title="Random string (N)">Random string</button><button type="button" className="utility" onClick={reset} disabled={loading} title="Clear (Escape)">Clear</button></div>
      <div className="keypad">{keys.map((key) => <button type="button" className={`key ${["+", "-", "*", "/"].includes(key) ? "operator-key" : ""}`} aria-label={key === "*" ? "multiply" : key === "/" ? "divide" : key === "±" ? "toggle sign" : key} key={key} onClick={() => handleKey(key)} disabled={loading}>{key}</button>)}<button type="button" className="equals-key" onClick={() => execute()} disabled={loading} aria-label="=">{loading ? <Spinner size="sm" animation="border" /> : "="}</button></div>
    </section>
  );
};

export default Calculator;
