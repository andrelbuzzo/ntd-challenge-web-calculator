import React from "react";
import { ButtonGroup, Col } from "react-bootstrap";

import CalcButton from "../CalcButton";

import "./CalcButtonGroup.css";

const CalcButtonGroup = props => {
  return (
    <div>
      <ButtonGroup justified>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="7"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="8"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
            <CalcButton
              bsStyle="primary"
              label="9"
              handleOnClick={props.handleOnClick}
            />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="info"
            label="÷"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
      </ButtonGroup>

      <ButtonGroup justified>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="4"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="5"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="6"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="info"
            label="*"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
      </ButtonGroup>
      
      <ButtonGroup justified>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="1"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="2"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="primary"
            label="3"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
        <ButtonGroup>
          <CalcButton
            bsStyle="info"
            label="-"
            handleOnClick={props.handleOnClick}
          />
        </ButtonGroup>
      </ButtonGroup>
      
      <Col xs={12} md={12} className="buttonCol mb-0">
        <ButtonGroup justified>
          <ButtonGroup>
            <CalcButton
              bsStyle="warning"
              label="Clear"
              handleOnClick={props.handleOnClick}
            />
          </ButtonGroup>
          <ButtonGroup>
            <CalcButton
              bsStyle="primary"
              label="0"
              handleOnClick={props.handleOnClick}
            />
          </ButtonGroup>
          <ButtonGroup>
            <CalcButton
              bsStyle="primary"
              label="."
              handleOnClick={props.handleOnClick}
            />
          </ButtonGroup>
          <ButtonGroup>
            <CalcButton
              bsStyle="info"
              label="+"
              handleOnClick={props.handleOnClick}
            />
          </ButtonGroup>
        </ButtonGroup>
      </Col>
      
      <Col xs={12} md={12} className="buttonCol">
        <ButtonGroup justified>
          <ButtonGroup>
            <CalcButton
              bsStyle="success"
              label="="
              handleOnClick={props.handleOnClick}
            />
          </ButtonGroup>
        </ButtonGroup>
      </Col>
    </div>
  );
}

export default CalcButtonGroup;