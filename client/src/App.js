import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Link, Navigate, NavLink, Route, Routes, useLocation } from "react-router-dom";

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

const ProtectedRoute = ({ authenticated, children }) => {
  const location = useLocation();
  return authenticated ? children : <Navigate to={`/?notice=auth&from=${encodeURIComponent(location.pathname)}`} replace />;
};

const App = () => {
  const [loggedIn, setLoggedIn] = useState(hasValidSession);

  useEffect(() => {
    const handleAuthChange = () => setLoggedIn(hasValidSession());
    window.addEventListener("auth-changed", handleAuthChange);
    window.addEventListener("auth-expired", handleAuthChange);
    return () => { window.removeEventListener("auth-changed", handleAuthChange); window.removeEventListener("auth-expired", handleAuthChange); };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("user");
    setLoggedIn(false);
    window.location.href = "/";
  };

  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <div id="application">
        <header className="app-header">
          <Link className="brand" to={loggedIn ? "/home" : "/"}><span className="brand-mark" aria-hidden="true"><i className="fa-solid fa-calculator" /></span><span>NTD Software - Web Calculator</span></Link>
          {loggedIn && <nav className="app-nav"><NavLink to="/home" className={({ isActive }) => isActive ? "active" : undefined}><i className="fa-solid fa-calculator header-icon" aria-hidden="true" />Calculator</NavLink><NavLink to="/records" className={({ isActive }) => isActive ? "active" : undefined}><i className="fa-solid fa-clock-rotate-left header-icon" aria-hidden="true" />History</NavLink></nav>}
          {loggedIn && <div className="btn-logout"><button className="header-logout" type="button" onClick={handleLogout}><i className="fa-solid fa-right-from-bracket header-icon" aria-hidden="true" />Sign out</button></div>}
        </header>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<ProtectedRoute authenticated={loggedIn}><Home /></ProtectedRoute>} />
          <Route path="/records" element={<ProtectedRoute authenticated={loggedIn}><Records /></ProtectedRoute>} />
          <Route path="*" element={<NoMatch />} />
        </Routes>
        <footer>
          <span>NTD Software - Web Calculator</span>
          <nav className="footer-social" aria-label="Social links">
            <a href="https://www.linkedin.com/in/andre-buzzo" target="_blank" rel="noreferrer" aria-label="LinkedIn"><i className="fa-brands fa-linkedin" aria-hidden="true" /></a>
            <a href="https://github.com/andrelbuzzo" target="_blank" rel="noreferrer" aria-label="GitHub"><i className="fa-brands fa-github" aria-hidden="true" /></a>
          </nav>
        </footer>
      </div>
    </Router>
  );
};

export default App;
