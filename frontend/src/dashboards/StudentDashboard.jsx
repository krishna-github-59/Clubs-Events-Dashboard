// import React, { useState } from "react";
// import useEventDashboard from "./useEventDashboard";
// import EventCard from "../components/EventCard";
// import EventDetails from "../components/EventDetails";
// import "../styles/dashboard.css";


// const StudentDashboard = () => {
//   const {
//     loading,
//     categories,
//     searchQuery,
//     setSearchQuery,
//     selectedCategory,
//     setSelectedCategory,
//     filteredUpcomingEvents,
//     filteredPastEvents,
//     loadData,
//   } = useEventDashboard("STUDENT");

//   const [selectedEventId, setSelectedEventId] = useState(null);

//   return (
//     <div className="dashboard-container">
//       <h1>Discover and Join Events</h1>

//       <input
//         className="search-input"
//         placeholder="Search events..."
//         value={searchQuery}
//         onChange={(e) => setSearchQuery(e.target.value)}
//       />

//       {/* Category Filter */}
//       <aside>
//         {categories.map((cat) => (
//           <label key={cat}>
//             <input
//               type="radio"
//               value={cat}
//               checked={selectedCategory === cat}
//               onChange={(e) => setSelectedCategory(e.target.value)}
//             />
//             {cat}
//           </label>
//         ))}
//       </aside>

//       <section>
//         <h2>Upcoming Events</h2>
//         {loading ? (
//           <p>Loading...</p>
//         ) : (
//           filteredUpcomingEvents.map((event) => (
//             <EventCard
//               key={event.id}
//               event={event}
//               onViewDetails={setSelectedEventId}
//             />
//           ))
//         )}
//       </section>

//       <section>
//         <h2>Past Events</h2>
//         {filteredPastEvents.map((event) => (
//           <EventCard
//             key={event.id}
//             event={event}
//             isPast
//             onViewDetails={setSelectedEventId}
//           />
//         ))}
//       </section>

//       {selectedEventId && (
//         <EventDetails
//           eventId={selectedEventId}
//           onClose={() => setSelectedEventId(null)}
//           onRegisterSuccess={loadData}
//         />
//       )}
//     </div>
//   );
// };

// export default StudentDashboard;

import React, { useState } from "react";
import useEventDashboard from "./useEventDashboard";
import EventCard from "../components/EventCard";
import EventDetails from "../components/EventDetails";
import EventMediaModal from "../components/EventMediaModal";
import "../styles/dashboard.css";
import { FiLogOut } from "react-icons/fi";
import { logout } from "../utils/authUtils";


const StudentDashboard = () => {
  const {
    loading,
    categories,
    searchQuery,
    setSearchQuery,
    selectedCategory,
    setSelectedCategory,
    filteredUpcomingEvents,
    filteredPastEvents,
    loadData,
  } = useEventDashboard("STUDENT");

  const [selectedEventId, setSelectedEventId] = useState(null);
  const [activeTab, setActiveTab] = useState("UPCOMING");
  const [showMediaModal, setShowMediaModal] = useState(false);
  const [mediaEventId, setMediaEventId] = useState(null);

    
  const handleLogout = () => {
    if (window.confirm("Are you sure you want to logout?")) {
      logout();
    }
  };
  
  const handleViewMedia = (event) => {
    setMediaEventId(event.id);
    setShowMediaModal(true);
  };

  const eventsToRender =
    activeTab === "UPCOMING"
      ? filteredUpcomingEvents
      : filteredPastEvents;

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-left">
          <h1>Discover and Join Events</h1>
        </div>

        <div className="header-right">
          <FiLogOut
            title="Logout"
            onClick={handleLogout}
            className="logout-icon"
          />
        </div>
      </header>

      <input
        className="search-input"
        placeholder="Search events..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
      />

      {/* Category Filter */}
      <aside className="category-filter">
        {categories.map((cat) => (
          <label key={cat}>
            <input
              type="radio"
              value={cat}
              checked={selectedCategory === cat}
              onChange={(e) => setSelectedCategory(e.target.value)}
            />
            {cat}
          </label>
        ))}
      </aside>

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
              onViewMedia={() => handleViewMedia(event)}
            />
          ))
        )}
      </div>
      
      {selectedEventId && (
        <EventDetails
          eventId={selectedEventId}
          onClose={() => setSelectedEventId(null)}
          onRegisterSuccess={loadData}
        />
      )}
      {showMediaModal && (
        <EventMediaModal
          eventId={mediaEventId}
          canUpload={false}
          onClose={() => {
            setShowMediaModal(false);
            setMediaEventId(null);
          }}
        />
      )}

    </div>
  );
};

export default StudentDashboard;

