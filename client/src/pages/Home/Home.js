import React from "react";
import { Col, Grid, Row } from "react-bootstrap";
import Calculator from "../../components/Calculator";

import "./Home.css";

const Home = ({ history }) => {
  return (
    <div>
      <Grid>
        <Row className="show-grid">
          <Col xsHidden md={4}></Col>
          <Col xs={12} md={4} id="calculatorBox">
            <Calculator history={history} />
          </Col>
          <Col xsHidden md={4}></Col>
        </Row>
      </Grid>
    </div>
  );
}

export default Home;