import React, { Component } from "react";
import { Col, Row } from "react-bootstrap";

import CalcDisplay from "../CalcDisplay";
import CalcButtonGroup from "../CalcButtonGroup";

import API from "../../utils/API";

import "./Calculator.css";

export default class Calculator extends Component {
  constructor(props) {
    super(props);

    this.state = {
      input: "",
      results: "",
      operator: "",
      param1: "",
      param2: "",
      APIFunction: null,
      onParam1: true,
      error: false,
      loggedIn: localStorage.getItem('user') ? true : false
    };

    this.resetValues = this.resetValues.bind(this);
    this.getAPIFunction = this.getAPIFunction.bind(this);
    this.performCalculation = this.performCalculation.bind(this);
    this.handleOnClick = this.handleOnClick.bind(this);
    
    this.checkIfLoggedIn(props)
  }

  checkIfLoggedIn(props) {
    if (!this.state.loggedIn)
      props.history.push('/')
  }

  resetValues(error) {
    this.setState({ 
      input: "",
      results: error ? "Error Occurred - Please Clear!" : "",
      operator: "",
      param1: "",
      param2: "",
      APIFunction: null,
      onParam1: true,
      error: error ? true : false
    });  
  }

  getAPIFunction(value) {
    console.log("getAPIFunction")
    console.log(value);
    let APIFunction = null;

    this.setState({ operator: value });
    
    APIFunction = API.calculate;

    return APIFunction;
  }

  performCalculation(usedEqualSign, value) {
    API.calculate(this.state.operator, this.state.param1, this.state.param2, JSON.parse(localStorage.getItem('user')).token)
    .then(response => {
      console.log("response")
      console.log(response)
      this.setState({
        input: usedEqualSign ? this.state.input : this.state.input + " " + value + " ",
        results: response.data.result,
        param1: usedEqualSign ? "" : response.data.result,
        operator: "",
        param2: "",
        APIFunction: usedEqualSign ? null : this.getAPIFunction(value),
        onParam1: usedEqualSign ? true : false,
        error: false
      })
    })
    .catch(err => {
      console.log(err);
      this.resetValues(true);
    })
  }

  handleOnClick(event) {
    console.log("handleOnClick")
    const value = event.target.value;
    console.log(value)

    switch (value) {
      case '=': {
        console.log("case =")
        console.log("operator: " + this.state.operator)
        if (this.state.param1 === "" || 
            this.state.param2 === "" || 
            this.state.operator === "") {
          this.resetValues(true);
        } else {
          console.log("performCalculation before")
          this.performCalculation(true, null);
        }
        break;
      }
      case "Clear": {
        this.resetValues(false);
        break;
      }
      case "+":
      case "-":
      case "*":
      case "/": {
        if (this.state.param1 === "") {
          this.resetValues(true);
        }
        else if (!this.state.onParam1) {
          console.log("----")
          this.performCalculation(false, value);
        }
        else {
          this.setState({
            input: this.state.input + " " + value + " ",
            //APIFunction: this.getAPIFunction(value),
            operator: value,
            onParam1: false
          })
          console.log("setou operator: " + this.state.operator)
        }
        break;
      }
      default: {
        console.log("handleOnClick - default")
        this.setState({
          input: this.state.input + value,
          param1: 
            this.state.onParam1 ? this.state.param1 + value : this.state.param1,
          param2:
            this.state.onParam1 ? this.state.param2 : this.state.param2 + value
        })
      }
    }
  }

  render() {
    return (
      <div>
        <Row className="show-grid">
          <Col>
            <h1 id="calcTitle">Simple Calculator</h1>
          </Col>
        </Row>
        <Row className="show-grid">
          <Col xs={12} md={12}>
            <CalcDisplay value={this.state.input} />
            <CalcDisplay className="result"
              value={this.state.results}
              error={this.state.error}
            />
          </Col>
        </Row>
        <Row className="show-grid mt-10 mb-10">
          <Col xs={12} md={12}>
            <CalcButtonGroup
              handleOnClick={this.handleOnClick}
            />
          </Col>
        </Row>

      </div>
    );
  }
}