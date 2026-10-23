import React, { useCallback, useEffect, useState } from "react";
import { Alert, Button, Form, InputGroup, Spinner, Table } from "react-bootstrap";
import API from "../../utils/API";
import { useNavigate } from "react-router-dom";
import "./Records.css";

const formatMoney = (value) => Number(value || 0).toFixed(2);
const formatOptionalMoney = (value) => value == null ? "—" : formatMoney(value);
const operationLabels = {
  ADDITION: "Addition",
  SUBTRACTION: "Subtraction",
  MULTIPLICATION: "Multiplication",
  DIVISION: "Division",
  SQUARE_ROOT: "Square root",
  RANDOM_STRING: "Random string"
};
const operatorLabels = { ADDITION: "+", SUBTRACTION: "−", MULTIPLICATION: "×", DIVISION: "÷", SQUARE_ROOT: "√", RANDOM_STRING: "N", sqrt: "√", random_string: "N" };
const parseLegacyEquation = (response) => {
  const match = String(response || "").match(/^Equation \[leftOperand=([^,]+), operation=([^,]+), rightOperand=([^,]+), result=([^\]]+)\]$/);
  return match ? { leftOperand: match[1], operator: match[2], rightOperand: match[3], response: match[4] } : null;
};

const getRecordDetails = (record) => {
  const legacy = parseLegacyEquation(record.operationResponse);
  const isUnaryOperation = record.operationType === "SQUARE_ROOT";
  return {
    leftOperand: record.leftOperand ?? record.amount ?? legacy?.leftOperand,
    operator: operatorLabels[record.operator] || operatorLabels[record.operationType] || legacy?.operator || record.operator,
    rightOperand: record.rightOperand ?? (isUnaryOperation ? null : legacy?.rightOperand),
    response: legacy?.response || record.operationResponse,
    balanceBefore: record.balanceBefore ?? (record.userBalance != null && record.cost != null ? Number(record.userBalance) + Number(record.cost) : null)
  };
};

const Records = () => {
  const navigate = useNavigate();
  const [data, setData] = useState({ content: [], page: 0, size: 10, totalPages: 0, totalElements: 0 });
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("createdAt,desc");
  const [operationTypes, setOperationTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadRecords = useCallback((page = 0) => {
    setLoading(true);
    setError("");
    API.records({ page, search, sort, ...(operationTypes.length ? { operationTypes } : {}) })
      .then(({ data: response }) => setData(response))
      .catch((requestError) => {
        if (requestError.response?.status === 401) {
          localStorage.removeItem("user");
          window.dispatchEvent(new Event("auth-expired"));
          navigate("/?notice=auth&from=%2Frecords", { replace: true });
          return;
        }
        setError(requestError.response?.data?.message || "Unable to load history.");
      })
      .finally(() => setLoading(false));
  }, [navigate, search, sort, operationTypes]);

  useEffect(() => { loadRecords(0); }, [loadRecords]);

  const removeRecord = (id) => {
    if (!window.confirm("Delete this history record?")) return;
    API.deleteRecord(id).then(() => loadRecords(data.page)).catch(() => setError("Unable to delete the record."));
  };

  return (
    <main className="records-page">
      <div className="records-heading">
        <div><p className="eyebrow">ACTIVITY</p><h2>Operation history</h2><p className="muted-copy">Review and manage your recent calculations.</p></div>
        <Button variant="outline-dark" onClick={() => navigate("/home")}>New operation</Button>
      </div>
      <div className="records-toolbar">
        <InputGroup>
          <Form.Control aria-label="Search records" placeholder="Search by operation or response" value={search} onChange={(event) => setSearch(event.target.value)} />
          <Button variant="dark" onClick={() => loadRecords(0)}>Search</Button>
        </InputGroup>
        <Form.Control as="select" aria-label="Sort records" value={sort} onChange={(event) => setSort(event.target.value)}>
          <option value="createdAt,desc">Most recent</option><option value="createdAt,asc">Oldest</option><option value="cost,desc">Highest cost</option><option value="cost,asc">Lowest cost</option>
        </Form.Control>
      </div>
      <fieldset className="operation-filters">
          {[["ADDITION","Addition"],["SUBTRACTION","Subtraction"],["MULTIPLICATION","Multiplication"],["DIVISION","Division"],["SQUARE_ROOT","Square root"],["RANDOM_STRING","Random string"]].map(([value, label]) => <label key={value}><input type="checkbox" checked={operationTypes.includes(value)} onChange={() => setOperationTypes((current) => current.includes(value) ? current.filter((item) => item !== value) : [...current, value])} /> {label}</label>)}
      </fieldset>
      {error && <Alert variant="danger">{error}</Alert>}
      <div className="table-wrap">
        {loading ? <div className="loading-state"><Spinner animation="border" size="sm" /> Loading...</div> : data.content.length === 0 ? <div className="empty-state"><strong>No operations found</strong><span>Your next calculations will appear here.</span></div> : (
          <Table responsive hover className="records-table">
            <thead><tr><th>Date</th><th>Operation</th><th>Left operand</th><th>Operator</th><th>Right operand</th><th>Response</th><th>Balance before</th><th>Cost</th><th>Balance after</th><th><span className="sr-only">Actions</span></th></tr></thead>
            <tbody>{data.content.map((record) => {
              const details = getRecordDetails(record);
              return <tr key={record.id}>
              <td>{record.createdAt ? new Date(record.createdAt).toLocaleString() : "—"}</td>
              <td><span className="operation-pill">{operationLabels[record.operationType] || record.operationType || "—"}</span></td>
              <td className="numeric-cell">{details.leftOperand ?? "—"}</td>
              <td className="operator-cell">{details.operator || "—"}</td>
              <td className="numeric-cell">{details.rightOperand ?? "—"}</td>
              <td className="response-cell">{details.response ?? "—"}</td>
              <td className="numeric-cell">{formatOptionalMoney(details.balanceBefore)}</td>
              <td className="numeric-cell">{formatOptionalMoney(record.cost)}</td>
              <td className="numeric-cell">{formatOptionalMoney(record.userBalance)}</td>
              <td><Button variant="link" className="delete-link" onClick={() => removeRecord(record.id)} aria-label={`Delete record ${record.id}`} title="Delete record"><i className="fa-solid fa-trash" aria-hidden="true" /></Button></td>
            </tr>;
            })}</tbody>
          </Table>
        )}
      </div>
      <div className="records-footer"><span>{data.totalElements} record(s)</span><div><Button variant="outline-secondary" disabled={data.first || loading} onClick={() => loadRecords(data.page - 1)}>Previous</Button><span className="page-label">Page {data.totalPages ? data.page + 1 : 0} of {data.totalPages}</span><Button variant="outline-secondary" disabled={data.last || loading} onClick={() => loadRecords(data.page + 1)}>Next</Button></div></div>
    </main>
  );
};

export default Records;
