import React, { useEffect, useState } from "react";
import EventCard from "../components/EventCard";
import EventService from "../services/EventService";

const SuperAdminEventsView = () => {
  const [clubs, setClubs] = useState([]);
  const [selectedClub, setSelectedClub] = useState(null);
  const [events, setEvents] = useState([]);
  const [activeTab, setActiveTab] = useState("UPCOMING");
  const [selectedEventId, setSelectedEventId] = useState(null);
  const [editEventId, setEditEventId] = useState(null);
  const [showMediaModal, setShowMediaModal] = useState(false);
  const [mediaEventId, setMediaEventId] = useState(null);
  const [canUpload, setCanUpload] = useState(false);

  useEffect(() => {
    loadClubs();
  }, []);

  useEffect(() => {
    if (selectedClub) {
      loadEvents();
    }
  }, [selectedClub, activeTab]);

  const loadClubs = async () => {
    const res = await EventService.getAllClubs();
    if (res.success) setClubs(res.data);
  };

  const loadEvents = async () => {
    const res = await EventService.getEventsByClub(selectedClub.id);
    if (res.success) setEvents(res.data);
  };

  const filteredEvents = events.filter(event =>
    activeTab === "UPCOMING"
      ? new Date(event.date) >= new Date()
      : new Date(event.date) < new Date()
  );

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
  };

  return (
    <div className="superadmin-events-container">
      <div className="top-bar">
        {/* Club Dropdown */}
        <div className="club-select-wrapper">
            <label>Select Club</label>
            <select
                className="club-select"
                value={selectedClub?.id || ""}
                onChange={(e) => {
                const club = clubs.find(c => c.id === Number(e.target.value));
                setSelectedClub(club);
                }}
            >
                <option value="">Select Club</option>
                {clubs.map(club => (
                <option key={club.id} value={club.id}>
                    {club.name}
                </option>
                ))}
            </select>
        </div>

        {/* Tabs */}
        <div className="event-tabs">
            <button
            className={activeTab === "UPCOMING" ? "active" : ""}
            onClick={() => setActiveTab("UPCOMING")}
            >
            Upcoming
            </button>

            <button
            className={activeTab === "PAST" ? "active" : ""}
            onClick={() => setActiveTab("PAST")}
            >
            Past
            </button>
        </div>
      </div>

      {/* Events */}
      <div className="events-grid">
        {filteredEvents.map(event => (
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
        ))}
      </div>
    </div>
  );
};

export default SuperAdminEventsView;
