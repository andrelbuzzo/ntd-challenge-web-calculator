import React, { Component } from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";

import NoMatch from "./components/NoMatch"

import Home from "./pages/Home";

import "./App.css";
import Login from "./pages/Login/Login";

export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loggedIn: localStorage.getItem('user') ? true : false
    };
    this.handleLogoutClick = this.handleLogoutClick.bind(props);
  }

  handleLogoutClick(event) {
    event.preventDefault();
    localStorage.clear();
    this.history.push('/');
  }

  render() {
    const isLoggedIn = this.state.loggedIn;
    let btnLogout;
    if (isLoggedIn) {
      btnLogout = <div className="btn-logout">
                    <input className={'inputButton'} type="button" onClick={this.handleLogoutClick} value={'Log out'} />
                  </div>
    }

    return (
      <Router >
        <div id="application">
          <header>
            <h1>NTD Challenge - Arithmetic Calculator REST API</h1>
            {btnLogout}
          </header>

          <Switch>
            <Route path="/" exact component={Login} />
            <Route component={Home} />
            <Route component={NoMatch} />
          </Switch>

          <footer>
            <h3>Developed by: André Buzzo</h3>
            <h4>
              <a href="https://www.linkedin.com/in/andr%C3%A9-buzzo-140b5617/" target="_blank"><i className="fa-brands fa-linkedin"></i></a>
              <a href="https://github.com/andrelbuzzo/ntd-challenge-web-calculator" target="_blank"><i className="fa-brands fa-github"></i></a>
            </h4>
          </footer>
        </div>
      </Router>
    )
  }
}