import "./Login.css";
import React, { useState } from 'react'
import API from "../../utils/API";
import { useLocation, useNavigate } from "react-router-dom";

const Login = (props) => {
  const navigate = useNavigate();
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const destination = params.get("from") || "/home";
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [emailError, setEmailError] = useState('')
  const [passwordError, setPasswordError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState('')

  const onButtonClick = (event) => {
    event.preventDefault()
    const normalizedEmail = email.trim()
    // Set initial error values to empty
    setEmailError('')
    setPasswordError('')
    setFormError('')
  
    // Check if the user has entered both fields correctly
    if ('' === normalizedEmail) {
      setEmailError('Please enter your email')
      return
    }
  
    if (!/^[\w-]+@([\w-]+\.)+[\w-]{2,4}$/.test(normalizedEmail)) {
      setEmailError('Please enter a valid email')
      return
    }
  
    if ('' === password) {
      setPasswordError('Please enter a password')
      return
    }
  
    if (password.length < 6) {
      setPasswordError('The password must be 6 characters or longer')
      return
    }
  
    logIn()
  }

  // Log in a user using email and password
  const logIn = () => {
    setSubmitting(true)
    API.login(email.trim(), password)
    .then((response) => {
      localStorage.setItem('user', JSON.stringify({
        email: email.trim(),
        token: response.data.token
      }))
      window.dispatchEvent(new Event('auth-changed'))
       if (props.history?.replace) props.history.replace(destination)
       else navigate(destination, { replace: true })
    })
    .catch((error) => {
      const message = error.response
        ? error.response.data?.message || 'Wrong email or password'
        : 'Unable to reach the API. Start the server on port 8090 and try again.'
      setFormError(message)
    })
    .finally(() => setSubmitting(false))
  }

  return (
    <main className="login-page"><div className={"box"}>
      {params.get("notice") === "auth" && <div className="auth-notice" role="status">You must sign in to access this resource.</div>}
      <form className={"login-form"} onSubmit={onButtonClick}>
        <p className="eyebrow">PRIVATE WORKSPACE</p><h1>Welcome back.</h1><p className="muted-copy">Sign in to run calculations and keep your activity in one place.</p>
        <label htmlFor="email">Email</label>
        <input
            id="email"
            type="text"
            value={email}
            placeholder="you@example.com"
            onChange={(ev) => setEmail(ev.target.value)}
          />
          <label className="errorLabel">{emailError}</label>

        <label htmlFor="password">Password</label><input
            id="password"
            type="password"
            value={password}
            placeholder="Password"
            onChange={(ev) => setPassword(ev.target.value)}
          />
          <label className="errorLabel">{passwordError}</label>
        <label className="errorLabel">{formError}</label>
        <input className={'inputButton'} type="submit" disabled={submitting} value={submitting ? 'Signing in...' : 'Log in'} />
      </form>
    </div></main>
    
    /*<div className={'loginContainer'}>
      <div className={'mainContainer'}>
        <div className={'titleContainer'}>
          <div>Login</div>
        </div>
        <br />
        <div className={'inputContainer'}>
          <input
            value={email}
            placeholder="Username (email)"
            onChange={(ev) => setEmail(ev.target.value)}
            className={'inputBox'}
          />
          <label className="errorLabel">{emailError}</label>
        </div>
        <br />
        <div className={'inputContainer'}>
          <input
            value={password}
            placeholder="Password"
            onChange={(ev) => setPassword(ev.target.value)}
            className={'inputBox'}
          />
          <label className="errorLabel">{passwordError}</label>
        </div>
        <br />
        <div className={'inputContainer'}>
          <input className={'inputButton'} type="button" onClick={onButtonClick} value={'Log in'} />
        </div>
      </div>
    </div>*/
  )
}

export default Login
