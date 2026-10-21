import React from "react";
import { Alert } from "react-bootstrap";
import "./NoMatch.css";

const NoMatch = () => (
  <Alert variant="light" className="text-center">
    <h1 id="noMatchError">404 Page Not Found</h1>
  </Alert>
);

export default NoMatch;
