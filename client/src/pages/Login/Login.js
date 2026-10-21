import "./Login.css";
import React, { useState } from 'react'
import API from "../../utils/API";
import { useNavigate } from "react-router-dom";

const Login = (props) => {
  const navigate = useNavigate();
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
      if (props.history?.replace) props.history.replace('/home')
      else navigate('/home', { replace: true })
    })
    .catch((error) => {
      const message = error.response?.data?.message || 'Wrong email or password'
      setFormError(message)
    })
    .finally(() => setSubmitting(false))
  }

  return (
    <div className={"box"}>
      <form className={"login-form"} onSubmit={onButtonClick}>
        <h1>Login</h1>
        <input
            type="text"
            value={email}
            placeholder="Username (email)"
            onChange={(ev) => setEmail(ev.target.value)}
          />
          <label className="errorLabel">{emailError}</label>

        <input
            type="password"
            value={password}
            placeholder="Password"
            onChange={(ev) => setPassword(ev.target.value)}
          />
          <label className="errorLabel">{passwordError}</label>
        <label className="errorLabel">{formError}</label>
        <input className={'inputButton'} type="submit" disabled={submitting} value={submitting ? 'Signing in...' : 'Log in'} />
      </form>
    </div>
    
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
