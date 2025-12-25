import { useState } from "react"
import "../styles/AuthPage.css"
import LoginForm from "./LoginForm"
import StudentRegisterForm from "./StudentRegisterForm"
import GuestRegisterForm from "./GuestRegisterForm"

export default function AuthPage() {
  const [activeTab, setActiveTab] = useState("login")
  const [registerType, setRegisterType] = useState("student")

  return (
    <div className="auth-container">
      <div className="auth-background">
        <div className="overlay"></div>
      </div>

      <div className="auth-content">
        <div className="branding">
          <h1>EventHub</h1>
          <p>Discover, manage and attend events effortlessly</p>
        </div>

        <div className="auth-card">
          <div className="auth-header">
            <h2>
              {activeTab === "login"
                ? "Login"
                : registerType === "student"
                ? "Student Registration"
                : "Guest Registration"}
            </h2>
          </div>

          <div className="auth-tabs">
            <button
              className={activeTab === "login" ? "active" : ""}
              onClick={() => setActiveTab("login")}
            >
              Login
            </button>
            <button
              className={activeTab === "register" ? "active" : ""}
              onClick={() => {
                setActiveTab("register")
                setRegisterType("student")
              }}
            >
              Register
            </button>
          </div>

          {activeTab === "login" && (
            <LoginForm onSwitchToRegister={() => setActiveTab("register")} />
          )}

          {activeTab === "register" && registerType === "student" && (
            <StudentRegisterForm
              onSwitchToGuest={() => setRegisterType("guest")}
              onSwitchToLogin={() => setActiveTab("login")}
            />
          )}

          {activeTab === "register" && registerType === "guest" && (
            <GuestRegisterForm
              onSwitchToStudent={() => setRegisterType("student")}
              onSwitchToLogin={() => setActiveTab("login")}
            />
          )}
        </div>
      </div>
    </div>
  )
}
