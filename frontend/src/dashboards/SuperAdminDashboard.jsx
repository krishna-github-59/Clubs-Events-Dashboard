import React, { useState } from "react";
import StudentDashboard from "./StudentDashboard";
import SuperAdminEventsView from "./SuperAdminEventsView";
import "../styles/dashboard.css";
import { FiUsers, FiCalendar, FiHome, FiShield } from "react-icons/fi";
// import { logout } from "../utils/authUtils";

const SuperAdminDashboard = () => {
  const [activeMenu, setActiveMenu] = useState("DASHBOARD");

  // const handleLogout = () => {
  //   if (window.confirm("Are you sure you want to logout?")) {
  //     logout();
  //   }
  // };

  const renderContent = () => {
    switch (activeMenu) {
      case "DASHBOARD":
        return <StudentDashboard />;   // default
      case "EVENTS":
        return <SuperAdminEventsView />;
      case "CLUBS":
        return <div className="placeholder">Clubs Management Coming Soon</div>;
      case "ADMINS":
        return <div className="placeholder">Club Admin Management Coming Soon</div>;
      default:
        return <StudentDashboard />;
    }
  };

  return (
    <div className="superadmin-layout">
      
      {/* Sidebar */}
      <aside className="sidebar">
        <h2 className="sidebar-title">Super Admin</h2>
        {/* <FiLogOut
          title="Logout"
          onClick={handleLogout}
          className="logout-icon"
        /> */}

        <nav>
          <button
            className={activeMenu === "DASHBOARD" ? "active" : ""}
            onClick={() => setActiveMenu("DASHBOARD")}
          >
            <FiHome /> Dashboard
          </button>

          <button
            className={activeMenu === "EVENTS" ? "active" : ""}
            onClick={() => setActiveMenu("EVENTS")}
          >
            <FiCalendar /> Events - Admin View
          </button>

          <button
            className={activeMenu === "CLUBS" ? "active" : ""}
            onClick={() => setActiveMenu("CLUBS")}
          >
            <FiUsers /> Clubs
          </button>

          <button
            className={activeMenu === "ADMINS" ? "active" : ""}
            onClick={() => setActiveMenu("ADMINS")}
          >
            <FiShield /> Club Admins
          </button>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="superadmin-content">
        {renderContent()}
      </main>

    </div>
  );
};

export default SuperAdminDashboard;
