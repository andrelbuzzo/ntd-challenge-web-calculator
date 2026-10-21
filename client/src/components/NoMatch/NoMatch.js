import React from "react";
import "./NoMatch.css";

const NoMatch = () => <main className="not-found"><p className="eyebrow">ERROR 404</p><h1 id="noMatchError">That page is missing.</h1><p className="muted-copy">The address may be out of date or the page may have moved.</p><a href="/home">Return to calculator</a></main>;

export default NoMatch;
