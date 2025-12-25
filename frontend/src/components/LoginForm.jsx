import { useState } from "react"
import "../styles/Form.css"
import { useNavigate } from "react-router-dom"
import EventService from "../services/EventService"

export default function LoginForm({ onSwitchToRegister }) {
  const [formData, setFormData] = useState({ email: "", password: "" })
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }
  const handleSubmit = async (e) => {
    e.preventDefault() // prevent default form submission

    try {
      const result = await EventService.login(formData.email, formData.password)
      if (result.success && result.data?.token) {
        // Token is already saved in localStorage by EventService
        navigate("/dashboard") // redirect to dashboard
      } else {
        alert(result.message || "Invalid credentials")
      }
    } catch (error) {
      console.error("Login error:", error)
      alert("Something went wrong during login")
    }
  }
  return (
    <form className="form" onSubmit={handleSubmit}>
      <label>Email</label>
      <input 
        name="email" 
        type="email" 
        value={formData.email} 
        onChange={handleChange} 
        required
      />

      <label>Password</label>
      <input 
        name="password" 
        type="password" 
        value={formData.password}
        onChange={handleChange} 
        required
       />

      <button type="submit">Login</button>

      <p className="link-text">
        Don't have an account?
        <span onClick={onSwitchToRegister}> Register</span>
      </p>
    </form>
  )
}
