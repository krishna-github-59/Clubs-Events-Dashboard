import React, { useEffect, useState, useCallback } from "react";
import EventCard from "../components/EventCard";
import EventService from "../services/EventService";
import EventDetails from "../components/EventDetails";
import EventMediaModal from "../components/EventMediaModal";
import useEventDashboard from "./useEventDashboard";

const SuperAdminEventsView = () => {
  const {
    loadData,
  } = useEventDashboard("CLUB_ADMIN");

  const [clubs, setClubs] = useState([]);
  const [selectedClub, setSelectedClub] = useState(null);
  const [events, setEvents] = useState([]);
  const [activeTab, setActiveTab] = useState("UPCOMING");
  const [selectedEventId, setSelectedEventId] = useState(null);
  const [editEventId, setEditEventId] = useState(null);
  const [showMediaModal, setShowMediaModal] = useState(false);
  const [mediaEventId, setMediaEventId] = useState(null);
  const [canUpload, setCanUpload] = useState(false);
  
  const loadClubs = async () => {
    const res = await EventService.getAllClubs();
    console.log("get all clubs result", res);
    if (res.success && res.data.length > 0){
      setClubs(res.data);
      setSelectedClub(res.data[0]);
    }
  };
  
  const loadEvents = useCallback(async () => {
    if (!selectedClub?.id) return;

    const res = await EventService.getEventsByClub(selectedClub.id);
    if (res.success) setEvents(res.data);
  },[selectedClub]);
  
  useEffect(() => {
    loadClubs();
  }, []);

  useEffect(() => {
    loadEvents();
  }, [loadEvents, activeTab]);

  console.log("events", events);

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
                {/* <option value="">Select Club</option> */}
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

      {/* {showCreateEvent && (
        <CreateEvent
          onClose={() => setShowCreateEvent(false)}
          onSuccess={() => {
            setShowCreateEvent(false);
            loadData();
          }}
        />
      )} */}

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

export default SuperAdminEventsView;
