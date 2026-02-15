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
    e.preventDefault();

    try {
      const result = await EventService.login(
        formData.email,
        formData.password
      );

      if (result.success && result.data?.token) {
        const user = JSON.parse(localStorage.getItem("user"));
        
        if (!user) {
          alert("User data not found");
          return;
        }
        const role = user.role;

        if (role === "STUDENT") {
          navigate("/student");
        } else if (role === "CLUB_ADMIN") {
          navigate("/club-admin");
        } else if (role === "SUPER_ADMIN") {
          navigate("/super-admin");
        } else {
          navigate("/login");
        }

      } else {
        alert(result.message || "Invalid credentials");
      }
    } catch (error) {
      console.error("Login error:", error);
      alert("Login failed");
    }
  };


  // const handleSubmit = async (e) => {
  //   e.preventDefault() // prevent default form submission

  //   try {
  //     const result = await EventService.login(formData.email, formData.password)
  //     if (result.success && result.data?.token) {
  //       // Token is already saved in localStorage by EventService
  //       // navigate("/dashboard") // redirect to dashboard
  //       // FORCE REMOUNT
  //       window.location.replace("/dashboard");
  //     } else {
  //       alert(result.message || "Invalid credentials")
  //     }
  //   } catch (error) {
  //     console.error("Login error:", error)
  //     alert("Login failed")
  //   }
  // }
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
