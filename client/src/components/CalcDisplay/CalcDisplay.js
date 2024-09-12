import React from "react";
import { Col, Row } from "react-bootstrap";

import "./CalcDisplay.css";

const CalcDisplay = props => {
  return (
    <div>
      <Row className="show-grid">
        <Col xs={12} md={12}>
          <input
            style={{color: props.error ? "red" : "black" }}
            className={"displayText " + (props.className || "equation")}
            type="text" 
            readOnly 
            value={props.value}
          />
        </Col>
      </Row>
    </div>
  );
}

export default CalcDisplay;
