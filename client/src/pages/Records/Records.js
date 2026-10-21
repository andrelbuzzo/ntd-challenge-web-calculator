import React, { useCallback, useEffect, useState } from "react";
import { Alert, Button, Form, InputGroup, Spinner, Table } from "react-bootstrap";
import API from "../../utils/API";
import { useNavigate } from "react-router-dom";
import "./Records.css";

const formatMoney = (value) => Number(value || 0).toFixed(2);

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
        setError(requestError.response?.data?.message || "Não foi possível carregar o histórico.");
      })
      .finally(() => setLoading(false));
  }, [navigate, search, sort, operationTypes]);

  useEffect(() => { loadRecords(0); }, [loadRecords]);

  const removeRecord = (id) => {
    if (!window.confirm("Excluir este registro do histórico?")) return;
    API.deleteRecord(id).then(() => loadRecords(data.page)).catch(() => setError("Não foi possível excluir o registro."));
  };

  return (
    <main className="records-page">
      <div className="records-heading">
        <div><p className="eyebrow">ATIVIDADE</p><h2>Histórico de operações</h2><p className="muted-copy">Consulte e gerencie seus últimos cálculos.</p></div>
        <Button variant="outline-dark" onClick={() => navigate("/home")}>Nova operação</Button>
      </div>
      <div className="records-toolbar">
        <InputGroup>
          <Form.Control aria-label="Buscar registros" placeholder="Buscar por operação ou resposta" value={search} onChange={(event) => setSearch(event.target.value)} />
          <Button variant="dark" onClick={() => loadRecords(0)}>Buscar</Button>
        </InputGroup>
        <Form.Control as="select" aria-label="Ordenar registros" value={sort} onChange={(event) => setSort(event.target.value)}>
          <option value="createdAt,desc">Mais recentes</option><option value="createdAt,asc">Mais antigos</option><option value="cost,desc">Maior custo</option><option value="cost,asc">Menor custo</option>
        </Form.Control>
      </div>
      <fieldset className="operation-filters"><legend>Filtrar operações</legend>{[["ADDITION","Adição"],["SUBTRACTION","Subtração"],["MULTIPLICATION","Multiplicação"],["DIVISION","Divisão"],["SQUARE_ROOT","Raiz quadrada"],["RANDOM_STRING","String aleatória"]].map(([value, label]) => <label key={value}><input type="checkbox" checked={operationTypes.includes(value)} onChange={() => setOperationTypes((current) => current.includes(value) ? current.filter((item) => item !== value) : [...current, value])} /> {label}</label>)}</fieldset>
      {error && <Alert variant="danger">{error}</Alert>}
      <div className="table-wrap">
        {loading ? <div className="loading-state"><Spinner animation="border" size="sm" /> Carregando...</div> : data.content.length === 0 ? <div className="empty-state"><strong>Nenhuma operação encontrada</strong><span>Seus próximos cálculos aparecerão aqui.</span></div> : (
          <Table responsive hover>
            <thead><tr><th>Operação</th><th>Entrada</th><th>Resposta</th><th>Custo</th><th>Saldo após</th><th>Data</th><th><span className="sr-only">Ações</span></th></tr></thead>
            <tbody>{data.content.map((record) => <tr key={record.id}><td><span className="operation-pill">{record.operationType}</span></td><td>{record.amount ?? "—"}</td><td className="response-cell">{record.operationResponse}</td><td>{formatMoney(record.cost)}</td><td>{formatMoney(record.userBalance)}</td><td>{record.createdAt ? new Date(record.createdAt).toLocaleString() : "—"}</td><td><Button variant="link" className="delete-link" onClick={() => removeRecord(record.id)}>Excluir</Button></td></tr>)}</tbody>
          </Table>
        )}
      </div>
      <div className="records-footer"><span>{data.totalElements} registro(s)</span><div><Button variant="outline-secondary" disabled={data.first || loading} onClick={() => loadRecords(data.page - 1)}>Anterior</Button><span className="page-label">Página {data.totalPages ? data.page + 1 : 0} de {data.totalPages}</span><Button variant="outline-secondary" disabled={data.last || loading} onClick={() => loadRecords(data.page + 1)}>Próxima</Button></div></div>
    </main>
  );
};

export default Records;
