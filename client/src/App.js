import React, { Component } from "react";
import { BrowserRouter as Router, Link, Route, Switch } from "react-router-dom";

import Login from "./pages/Login/Login";
import Home from "./pages/Home";
import Records from "./pages/Records";
import NoMatch from "./components/NoMatch"

import "./App.css";

export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loggedIn: localStorage.getItem('user') ? true : false
    };
    this.handleLogoutClick = this.handleLogoutClick.bind(this);
    this.handleAuthChange = this.handleAuthChange.bind(this);
  }

  componentDidMount() {
    window.addEventListener('auth-changed', this.handleAuthChange);
  }

  componentWillUnmount() {
    window.removeEventListener('auth-changed', this.handleAuthChange);
  }

  handleAuthChange() {
    this.setState({ loggedIn: Boolean(localStorage.getItem('user')) });
  }

  handleLogoutClick(event) {
    event.preventDefault();
    localStorage.removeItem('user');
    this.setState({ loggedIn: false });
    window.location = '/';
  }

  render() {
    const isLoggedIn = this.state.loggedIn;
    let btnLogout;
    // console.log("isLoggedIn: " + isLoggedIn)
    if (isLoggedIn) {
      btnLogout = <div className="btn-logout">
                    <input className={'inputButton'} type="button" onClick={this.handleLogoutClick} value={'Log out'} />
                  </div>
    }

    return (
      <Router>
        <div id="application">
          <header>
            <h1>NTD Challenge - Arithmetic Calculator REST API</h1>
            {isLoggedIn && <nav className="app-nav"><Link to="/home">Calculadora</Link><Link to="/records">Histórico</Link></nav>}
            {btnLogout}
          </header>

          <Switch>
            <Route path="/" exact component={Login} />
            <Route path="/home" render={(routeProps) => isLoggedIn ? <Home {...routeProps} /> : <Login {...routeProps} />} />
            <Route path="/records" render={(routeProps) => isLoggedIn ? <Records {...routeProps} /> : <Login {...routeProps} />} />
            <Route component={NoMatch} />
          </Switch>

          <footer>
            <h3>Developed by: André Buzzo</h3>
            <h4>
              <a href="https://www.linkedin.com/in/andr%C3%A9-buzzo-140b5617/" target="_blank" rel="noreferrer"><i className="fa-brands fa-linkedin"></i></a>
              <a href="https://github.com/andrelbuzzo/ntd-challenge-web-calculator" target="_blank" rel="noreferrer"><i className="fa-brands fa-github"></i></a>
            </h4>
          </footer>
        </div>
      </Router>
    )
  }
}
