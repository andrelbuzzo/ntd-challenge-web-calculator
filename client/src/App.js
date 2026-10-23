import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Link, Navigate, Route, Routes, useLocation } from "react-router-dom";

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
          <Link className="brand" to={loggedIn ? "/home" : "/"}><span className="brand-mark">∑</span><span>Ledger / Calculate</span></Link>
          {loggedIn && <nav className="app-nav"><Link to="/home">Calculator</Link><Link to="/records">History</Link></nav>}
          {loggedIn && <div className="btn-logout"><button className="inputButton" type="button" onClick={handleLogout}>Sign out</button></div>}
        </header>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/home" element={<ProtectedRoute authenticated={loggedIn}><Home /></ProtectedRoute>} />
          <Route path="/records" element={<ProtectedRoute authenticated={loggedIn}><Records /></ProtectedRoute>} />
          <Route path="*" element={<NoMatch />} />
        </Routes>
        <footer><span>Ledger / Calculate</span><span>Focused arithmetic, recorded clearly.</span></footer>
      </div>
    </Router>
  );
};

export default App;
