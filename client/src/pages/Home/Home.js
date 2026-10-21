import React, { useEffect, useState } from "react";
import { Alert, Col, Container, Row, Spinner } from "react-bootstrap";
import Calculator from "../../components/Calculator";
import API from "../../utils/API";

import "./Home.css";

const Home = ({ history }) => {
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    API.currentUser()
      .then(({ data }) => setUser(data))
      .catch(() => setError("Não foi possível carregar seu saldo. Entre novamente."));
  }, []);

  if (error) return <Alert variant="danger">{error}</Alert>;
  if (!user) return <div className="loading-state"><Spinner animation="border" size="sm" /> Carregando saldo...</div>;

  return (
    <Container className="page-shell">
      <Row>
        <Col lg={{ span: 7, offset: 1 }}>
          <Calculator history={history} user={user} onUserChange={setUser} />
        </Col>
        <Col lg={3} className="home-aside">
          <p className="eyebrow">CONTROLE DE CONSUMO</p>
          <h2>Seu saldo acompanha cada cálculo.</h2>
          <p className="muted-copy">Cada operação é validada e registrada com segurança no servidor.</p>
          <div className="tip-row"><span>01</span><p>Escolha os números</p></div>
          <div className="tip-row"><span>02</span><p>Execute a operação</p></div>
          <div className="tip-row"><span>03</span><p>Acompanhe o saldo</p></div>
        </Col>
      </Row>
    </Container>
  );
};

export default Home;
