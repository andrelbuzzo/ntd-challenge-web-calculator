import "./Login.css";
import React, { useState } from 'react'
import API from "../../utils/API";

const Login = (props) => {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [emailError, setEmailError] = useState('')
  const [passwordError, setPasswordError] = useState('')
  const [loggedIn, setLoggedIn] = useState(false)

  const onButtonClick = (event) => {
    event.preventDefault()
    // Set initial error values to empty
    setEmailError('')
    setPasswordError('')
  
    // Check if the user has entered both fields correctly
    if ('' === email) {
      setEmailError('Please enter your email')
      return
    }
  
    if (!/^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email)) {
      setEmailError('Please enter a valid email')
      return
    }
  
    if ('' === password) {
      setPasswordError('Please enter a password')
      return
    }
  
    if (password.length < 5) {
      setPasswordError('The password must be 6 characters or longer')
      return
    }
  
    logIn()
  }

  // Log in a user using email and password
  const logIn = () => {
    API.login(email, password)
    .then((response) => {
      if (response.status === 200) {
        localStorage.setItem('user', JSON.stringify({ email, token: response.data.token }))
        setLoggedIn(true)
        setEmail(email)
        props.history.push('/Home')
      } else {
        window.alert('Wrong email or password')
      }
    })
  }

  return (
    <div className={"box"}>
      <form className={"login-form"}>
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
        <input className={'inputButton'} type="submit" onClick={onButtonClick} value={'Log in'} />
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