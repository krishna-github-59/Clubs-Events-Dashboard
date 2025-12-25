import { useState } from "react"
import "../styles/Form.css"

export default function StudentRegisterForm({ onSwitchToGuest, onSwitchToLogin }) {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
  })

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  return (
    <form className="form">
      <label>Full Name</label>
      <input name="fullName" onChange={handleChange} />

      <label>Email</label>
      <input name="email" type="email" onChange={handleChange} />

      <label>Password</label>
      <input name="password" type="password" onChange={handleChange} />

      <button type="submit">Register as Student</button>

      <p className="link-text" onClick={onSwitchToGuest}>
        Not a student? Register as Guest
      </p>
      <p className="link-text" onClick={onSwitchToLogin}>
        Already have an account? Login
      </p>
    </form>
  )
}
