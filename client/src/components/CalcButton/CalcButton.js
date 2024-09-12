import React from "react";
import { Button } from "react-bootstrap";

import "./CalcButton.css";

const CalcButton = props => {
  return (
    <Button
        bsStyle={props.bsStyle}
        value={props.label}
        onClick={props.handleOnClick}
    >   
      {props.label}
    </Button>
  );
}

export default CalcButton;