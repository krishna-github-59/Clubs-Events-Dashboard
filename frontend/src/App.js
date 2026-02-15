import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AuthPage from './components/AuthPage';
// import EventDashboard from './components/EventDashboard';
import ProtectedRoute from './routes/ProtectedRoute';
import StudentDashboard from './dashboards/StudentDashboard';
import ClubAdminDashboard from './dashboards/ClubAdminDashboard';
import SuperAdminDashboard from './dashboards/SuperAdminDashboard';

function App() {
  return (
      <Routes>
        <Route path="/login" element={<AuthPage />} />

        {/* Protected dashboard */}
        {/* <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <EventDashboard/>
            </ProtectedRoute>
          }
        /> */}
        <Route
          path="/student"
          element={
            <ProtectedRoute
            allowedRoles={["STUDENT"]}
            >
              <StudentDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/club-admin"
          element={
            <ProtectedRoute 
            allowedRoles={["CLUB_ADMIN"]}
            >
              <ClubAdminDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/super-admin"
          element={
            <ProtectedRoute 
            allowedRoles={["SUPER_ADMIN"]}
            >
              <SuperAdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* Redirect unknown routes */}
        <Route path="*" element={<AuthPage />} />
      </Routes>
  );
}

export default App;
