import React, { useEffect, useState } from "react";
import { Alert, Col, Container, Row, Spinner } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Calculator from "../../components/Calculator";
import API from "../../utils/API";

import "./Home.css";

const Home = ({ history }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    API.currentUser()
      .then(({ data }) => setUser(data))
      .catch((requestError) => {
        if (requestError.response?.status === 401) {
          localStorage.removeItem("user");
          window.dispatchEvent(new Event("auth-expired"));
          navigate("/?notice=auth&from=%2Fhome", { replace: true });
          return;
        }
        setError("Unable to load your balance. Please sign in again.");
      });
  }, [navigate]);

  if (error) return <Alert variant="danger">{error}</Alert>;
  if (!user) return <div className="loading-state"><Spinner animation="border" size="sm" /> Loading balance...</div>;

  return (
    <Container className="page-shell">
      <Row>
        <Col lg={{ span: 7, offset: 1 }}>
          <Calculator history={history} user={user} onUserChange={setUser} />
        </Col>
        <Col lg={3} className="home-aside">
          <p className="eyebrow">CONSUMPTION TRACKING</p>
          <h2>Your balance follows every calculation.</h2>
          <div className="balance-summary"><span>Current balance</span><strong>{Number(user.balance || 0).toFixed(2)}</strong></div>
          <p className="muted-copy">Every operation is validated and securely recorded on the server.</p>
          <div className="tip-row"><span>01</span><p>Choose the numbers</p></div>
          <div className="tip-row"><span>02</span><p>Run the operation</p></div>
          <div className="tip-row"><span>03</span><p>Track your balance</p></div>
        </Col>
      </Row>
    </Container>
  );
};

export default Home;
