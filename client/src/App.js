import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Link, Navigate, Route, Routes } from "react-router-dom";

import Login from "./pages/Login/Login";
import Home from "./pages/Home";
import Records from "./pages/Records";
import NoMatch from "./components/NoMatch";

import "./App.css";

const hasValidSession = () => {
  try {
    const user = JSON.parse(localStorage.getItem("user") || "null");
    return Boolean(user?.token);
  } catch {
    localStorage.removeItem("user");
    return false;
  }
};

const ProtectedRoute = ({ authenticated, children }) => (
  authenticated ? children : <Navigate to="/" replace />
);

const App = () => {
  const [loggedIn, setLoggedIn] = useState(hasValidSession);

  useEffect(() => {
    const handleAuthChange = () => setLoggedIn(hasValidSession());
    window.addEventListener("auth-changed", handleAuthChange);
    return () => window.removeEventListener("auth-changed", handleAuthChange);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setLoggedIn(false);
    window.location.href = "/";
  };

  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <div id="application">
        <header>
          <h1>NTD Challenge - Arithmetic Calculator REST API</h1>
          {loggedIn && <nav className="app-nav"><Link to="/home">Calculadora</Link><Link to="/records">Histórico</Link></nav>}
          {loggedIn && <div className="btn-logout"><button className="inputButton" type="button" onClick={handleLogout}>Log out</button></div>}
        </header>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<ProtectedRoute authenticated={loggedIn}><Home /></ProtectedRoute>} />
          <Route path="/records" element={<ProtectedRoute authenticated={loggedIn}><Records /></ProtectedRoute>} />
          <Route path="*" element={<NoMatch />} />
        </Routes>
        <footer>
          <h3>Developed by: André Buzzo</h3>
          <h4>
            <a href="https://www.linkedin.com/in/andr%C3%A9-buzzo-140b5617/" target="_blank" rel="noreferrer"><i className="fa-brands fa-linkedin"></i></a>
            <a href="https://github.com/andrelbuzzo/ntd-challenge-web-calculator" target="_blank" rel="noreferrer"><i className="fa-brands fa-github"></i></a>
          </h4>
        </footer>
      </div>
    </Router>
  );
};

export default App;
