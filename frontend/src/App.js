// import React from 'react';
// import EventDashboard from './components/EventDashboard';
// import './App.css';

// function App() {
//   return (
//     <div className="App">
//       <EventDashboard />
//     </div>
//   );
// }

// export default App;

import React from "react"
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"

import AuthPage from "./components/AuthPage"
import EventDashboard from "./components/EventDashboard"

function App() {
  // Check if JWT token exists in localStorage
  const isAuthenticated = !!localStorage.getItem("token")

  return (
    <Router>
      <Routes>
        {/* Auth Page */}
        <Route
          path="/"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <AuthPage />}
        />

        {/* Dashboard (protected route) */}
        <Route
          path="/dashboard"
          element={isAuthenticated ? <EventDashboard /> : <Navigate to="/" replace />}
        />

        {/* Catch-all route */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  )
}

export default App
