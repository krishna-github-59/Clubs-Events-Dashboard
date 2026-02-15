import { useState } from "react"
import "../styles/Form.css"
import EventService from "../services/EventService"

export default function StudentRegisterForm({ 
                                // onSwitchToGuest, 
                                onSwitchToLogin }) {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
      e.preventDefault() // prevent default form submission
  
      try {
        const result = await EventService.registerStudent(formData.fullName, formData.email, formData.password)
        if (result.success) {
          alert("Registration is successful")
          onSwitchToLogin();  // switches to login page
        } else {
          alert(result.message || "Registration failed")
        }
      } catch (error) {
        console.error("Registartion error:", error)
        alert("Registartion failed")
      }
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <label>Full Name</label>
      <input name="fullName" onChange={handleChange} />

      <label>Email</label>
      <input name="email" type="email" onChange={handleChange} />

      <label>Password</label>
      <input name="password" type="password" onChange={handleChange} />

      <button type="submit">Register</button>

      {/* <p className="link-text" onClick={onSwitchToGuest}>
        Not a student? Register as Guest
      </p> */}
      <p className="link-text" onClick={onSwitchToLogin}>
        Already have an account? Login
      </p>
    </form>
  )
}
