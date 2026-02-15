import React, { useState } from "react";
import useEventDashboard from "./useEventDashboard";
import EventCard from "../components/EventCard";
import EventDetails from "../components/EventDetails";
import CreateEvent from "../components/CreateEvent";
import EventMediaModal from "../components/EventMediaModal";
import EventService from "../services/EventService";
import "../styles/dashboard.css";
import { FiLogOut } from "react-icons/fi";
import { logout } from "../utils/authUtils";


const ClubAdminDashboard = () => {
  const {
    loading,
    filteredUpcomingEvents,
    filteredPastEvents,
    loadData,
  } = useEventDashboard("CLUB_ADMIN");

  const [selectedEventId, setSelectedEventId] = useState(null);
  const [editEventId, setEditEventId] = useState(null);
  const [showCreateEvent, setShowCreateEvent] = useState(false);
  const [showMediaModal, setShowMediaModal] = useState(false);
  const [mediaEventId, setMediaEventId] = useState(null);
  const [canUpload, setCanUpload] = useState(false);
  const [activeTab, setActiveTab] = useState("UPCOMING");

  
  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      logout();
    }
  };

  const eventsToRender =
      activeTab === "UPCOMING"
          ? filteredUpcomingEvents
          : filteredPastEvents;


  const handleAddMedia = (event) => {
  setMediaEventId(event.id);
  setCanUpload(true);
  setShowMediaModal(true);
  };

  const handleViewMedia = (event) => {
  setMediaEventId(event.id);
  setCanUpload(false);
  setShowMediaModal(true);
  };


  const handleDeleteEvent = async (eventId) => {
    if (!window.confirm("Delete this event?")) return;
    await EventService.deleteEvent(eventId);
    loadData();
  };

  return (
    <div className="dashboard-container">
        <header className="dashboard-header">
            <div className="header-left">
                <h1>Club Admin Dashboard</h1>
            </div>

            <div className="header-right">
                <button
                className="create-btn"
                onClick={() => setShowCreateEvent(true)}
                >
                + Create Event
                </button>

                <FiLogOut
                  title="Logout"
                  onClick={handleLogout}
                  className="logout-icon"
                />
            </div>
        </header>

      {/* Tabs */}
    <div className="event-tabs">
    <button
        className={activeTab === "UPCOMING" ? "active" : ""}
        onClick={() => setActiveTab("UPCOMING")}
    >
        Upcoming Events
    </button>

    <button
        className={activeTab === "PAST" ? "active" : ""}
        onClick={() => setActiveTab("PAST")}
    >
        Past Events
    </button>
    </div>

    {/* Events Grid */}
    <div className="events-grid">
    {loading ? (
        <p>Loading...</p>
    ) : eventsToRender.length === 0 ? (
        <div className="empty-state">
        No {activeTab.toLowerCase()} events found.
        </div>
    ) : (
        eventsToRender.map((event) => (
        <EventCard
            key={event.id}
            event={event}
            isPast={activeTab === "PAST"}
            onViewDetails={setSelectedEventId}
            onEdit={activeTab === "UPCOMING" ? setEditEventId : undefined}
            onDelete={activeTab === "UPCOMING" ? handleDeleteEvent : undefined}
            onAddMedia={
            activeTab === "PAST"
                ? () => handleAddMedia(event)
                : undefined
            }
            onViewMedia={
            activeTab === "PAST"
                ? () => handleViewMedia(event)
                : undefined
            }
        />
        ))
    )}
    </div>

      {(selectedEventId || editEventId) && (
        <EventDetails
          eventId={selectedEventId || editEventId}
          viewOnly={!editEventId}
          onClose={() => {
            setSelectedEventId(null);
            setEditEventId(null);
          }}
          onRegisterSuccess={loadData}
        />
      )}

      {showCreateEvent && (
        <CreateEvent
          onClose={() => setShowCreateEvent(false)}
          onSuccess={() => {
            setShowCreateEvent(false);
            loadData();
          }}
        />
      )}

      {showMediaModal && (
        <EventMediaModal
          eventId={mediaEventId}
          canUpload={canUpload}
          onClose={() => {
          setShowMediaModal(false);
          setMediaEventId(null);
        }}
        />
      )}
    </div>
  );
};

export default ClubAdminDashboard;
