// import React, { useState, useEffect } from 'react';
// import EventCard from './EventCard';
// import EventDetails from './EventDetails';
// import EventService from '../services/EventService';
// import '../styles/EventDashboard.css';
// import { getCategoryName } from '../utils/categoryUtils';
// import { getLoggedInUser, logout } from '../utils/authUtils';
// import { FiLogOut } from "react-icons/fi";
// import CreateEvent from './CreateEvent';
// import EventMediaModal from './EventMediaModal';


// const EventDashboard = () => {
//   const [upcomingEvents, setUpcomingEvents] = useState([]);
//   const [filteredUpcomingEvents, setFilteredUpcomingEvents] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [searchQuery, setSearchQuery] = useState('');
//   const [selectedCategory, setSelectedCategory] = useState('All Categories');
//   const [selectedEventId, setSelectedEventId] = useState(null);
//   const [totalEventsCount, setTotalEventsCount] = useState(0);
//   const [editEventId, setEditEventId] = useState(null);
//   const [showCreateEvent, setShowCreateEvent] = useState(false);
//   const [pastEvents, setPastEvents] = useState([]);
//   const [filteredPastEvents, setFilteredPastEvents] = useState([]);
//   const [showMediaModal, setShowMediaModal] = useState(false);
//   const [mediaEventId, setMediaEventId] = useState(null);
//   const [mediaMode, setMediaMode] = useState("view"); // "view" | "add"
//   const [canUpload, setCanUpload] = useState(false);


//   const categories = [
//     'All Categories',
//     'Technical',
//     'Arts and Culture',
//     'Photography',
//     'Sports'
//   ];

//   useEffect(() => {
//     loadData();
//   }, []);

//   const userData = getLoggedInUser();
//   const role = userData?.role;

//   useEffect(() => {
//     if (role=="CLUB_ADMIN") {
//       loadPastEvents();
//     }
//     loadPastEvents();
//   }, [role]);


//   useEffect(() => {
//     if (role === "CLUB_ADMIN") {
//       setTotalEventsCount(upcomingEvents.length + pastEvents.length);
//     } else {
//       setTotalEventsCount(upcomingEvents.length + pastEvents.length);
//     }
//   }, [upcomingEvents, pastEvents, role]);



//   useEffect(() => {
//     if (!searchQuery && selectedCategory === 'All Categories'){
//       loadData(); 
//       return;
//     }
//     filterEvents();
//   }, [searchQuery, selectedCategory]);

//   const loadPastEvents = async () => {
//     try {
//       let res;

//       if (role === "CLUB_ADMIN") {
//         res = await EventService.getMyClubPastEvents();
//       } else {
//         res = await EventService.getAllPastEvents();
//       }

//       console.log("past events response", res);

//       setPastEvents(res.data || []);
//       setTotalEventsCount(upcomingEvents.length + pastEvents.length);
//     } catch (e) {
//       console.error(e);
//     }
//   };

//   const loadData = async () => {
//     try {
//       setLoading(true);
      
//       let upcomingEventsResponse;
//       let pastEventsResponse;

//       if (role === 'CLUB_ADMIN') {
//         upcomingEventsResponse = await EventService.getMyClubUpcomingEvents();
//         pastEventsResponse = await EventService.getMyClubPastEvents();
//       } else {
//         upcomingEventsResponse = await EventService.getAllUpcomingEvents();
//         pastEventsResponse = await EventService.getAllPastEvents();
//       }

      
//       if (upcomingEventsResponse.success) {
//         const upcomingEvents = upcomingEventsResponse.data || [];
//         setUpcomingEvents(upcomingEvents);
//         setFilteredUpcomingEvents(upcomingEvents);
//       }

//       if (pastEventsResponse.success) {
//         const pastEvents = pastEventsResponse.data || [];
//         setPastEvents(pastEvents);
//         setFilteredPastEvents(pastEvents);
//       }
//     } catch (error) {
//       console.error('Error loading data:', error);
//     } finally {
//       setLoading(false);
//     }
//   };


//   const filterEvents = () => {
//     let filteredUpcoming = [...upcomingEvents];
//     let filteredPast = [...pastEvents];

//     if (role !== 'CLUB_ADMIN' && selectedCategory !== 'All Categories') {
//       filteredUpcoming = filteredUpcoming.filter(
//         (e) => getCategoryName(e.clubName) === selectedCategory
//       );
//       filteredPast = filteredPast.filter(
//         (e) => getCategoryName(e.clubName) === selectedCategory
//       );
//     }

//     if (searchQuery) {
//       const query = searchQuery.toLowerCase().trim();
//       filteredUpcoming = filteredUpcoming.filter(e =>
//         e.name.toLowerCase().includes(query)
//       );
//       filteredPast = filteredPast.filter(e =>
//         e.name.toLowerCase().includes(query)
//       );
//     }

//     setFilteredUpcomingEvents(filteredUpcoming);
//     setFilteredPastEvents(filteredPast);
//   };


//   const handleCloseDetails = () => {
//     setSelectedEventId(null);
//   };

//   const handleViewDetails = (eventId) => {
//     setSelectedEventId(eventId);
//     setEditEventId(null); // close edit modal if any
//   };

//   const handleEditEvent = (eventId) => {
//     setEditEventId(eventId);
//     setSelectedEventId(null); // close view modal if any
//   };

//   const handleAddMedia = (event) => {
//     setMediaEventId(event.id);
//     setMediaMode("add");
//     setShowMediaModal(true);
//     setCanUpload(event.canAddMedia);
//   };

//   const handleViewMedia = (event) => {
//     setMediaEventId(event.id);
//     setMediaMode("view");
//     setShowMediaModal(true);
//     setCanUpload(false);
//   };

//   const handleRegisterSuccess = () => {
//     handleCloseDetails();
//     loadData();
//   };

//   const handleLogout = () => {
//     if (window.confirm("Are you sure you want to logout?")) {
//       logout();
//     }
//   };


//   const handleDeleteEvent = async (eventId) => {
//     const confirmed = window.confirm(
//       "Are you sure you want to delete this event?"
//     );
//     if (!confirmed) return;

//     const response = await EventService.deleteEvent(eventId);
//     if (response.success) loadData();
//     else alert(response.message || 'Failed to delete event');
//   };

//   let subtitle = '';
//   if(role === 'SUPER_ADMIN') subtitle = "Can Manage Clubs as well as Events";
//   else if(role === 'CLUB_ADMIN') subtitle = `Managing events for ${userData.clubName}`;
//   else subtitle = 'Discover and Join Events'

//   return (
//     <div className="dashboard-container">
//       <header className="dashboard-header">
//         <div className="header-content">
//           <div className="header-title-section">
//             <span className="calendar-icon">📅</span>
//             <div>
//               <h1 className="dashboard-title">College Events Dashboard</h1>
//               <p className="dashboard-subtitle">
//                 {/* {role === 'CLUB_ADMIN'
//                   ? `Managing events for ${userData.clubName}`
//                   : 'Discover and join club events'} */}
//                   {subtitle}
//               </p>
//             </div>
//           </div>
//           {/* {role==="CLUB_ADMIN" && ( */}
//             <div className="header-stats">
//             <div className="stat-item">
//               <span className="stat-label">Total Events</span>
//               <span className="stat-value">{totalEventsCount}</span>
//             </div>
//             <div className="stat-item">
//               <span className="stat-label">Upcoming</span>
//               <span className="stat-value">{upcomingEvents.length}</span>
//             </div>
//           </div>
//         {/* )} */}
//           <div>
//             {role === 'CLUB_ADMIN' && (
//               <button
//                 className="btn-create-event"
//                 onClick={() => setShowCreateEvent(true)}
//               >
//                 + Create Event
//               </button>
//             )}
//             <FiLogOut
//               title="Logout"
//               onClick={handleLogout}
//               className="logout-icon"
//             />
//           </div>
//         </div>
//         <div className="search-container">
//           <input
//             type="text"
//             className="search-input"
//             placeholder="Search events, clubs, or keywords..."
//             value={searchQuery}
//             onChange={(e) => setSearchQuery(e.target.value)}
//           />
//           {/* {role === 'CLUB_ADMIN' && (
//             <button
//               className="btn-create-event"
//               onClick={() => setShowCreateEvent(true)}
//             >
//               + Create Event
//             </button>
//           )} */}
//         </div>
//       </header>

//       <div className="dashboard-content">
//         {role!=="CLUB_ADMIN" && role!=="SUPER_ADMIN" && (
//           <aside className="filters-sidebar">
//             <div className="filters-header">
//               <h2>Filters</h2>
//               <span className="filter-badge">{filteredUpcomingEvents.length || 0} Events</span>
//             </div>

//             <div className="filter-section">
//               <h3 className="filter-title">Category</h3>
//               <div className="filter-options">
//                 {categories.map(category => (
//                   <label key={category} className="filter-option">
//                     <input
//                       type="radio"
//                       name="category"
//                       value={category}
//                       checked={selectedCategory === category}
//                       onChange={(e) => setSelectedCategory(e.target.value)}
//                     />
//                     <span className="filter-label">{category}</span>
//                   </label>
//                 ))}
//               </div>
//             </div>
//           </aside>
//         )}
//         <main className="events-grid">
//           <section>
//             <h2 className="section-title">Upcoming Events</h2>
//             {loading ? (
//               <div className="loading">Loading events...</div>
//             ) : filteredUpcomingEvents.length === 0 ? (
//               <div className="no-events">No Upcoming events found</div>
//             ) : (
//               filteredUpcomingEvents.map(event => (
//                 <EventCard
//                   key={event.id}
//                   event={event}
//                   onViewDetails={handleViewDetails}
//                   onEdit={handleEditEvent}
//                   onDelete={handleDeleteEvent}
//                   onViewMedia={() => handleViewMedia(event.id)}
//                   isPast={false}
//                 />
//               ))
//             )}
//           </section>
//           <section className="past-events-section">
//             <h2 className="section-title">Past Events</h2>
//               {filteredPastEvents.length > 0 ? (
//                 <div className="events-grid">
//                   {filteredPastEvents.map(event => (
//                     <EventCard
//                       key={event.id}
//                       event={event}
//                       isPast={true}
//                       onViewDetails={handleViewDetails}
//                       onViewMedia={() => handleViewMedia(event)}
//                       onAddMedia={() => handleAddMedia(event)}
//                     />
//                   ))}
//                 </div>
//             ) : (
//               <div className="no-events">No Past Events Found</div>
//             )}
//             </section>
//         </main>
//       </div>

//       {(editEventId || selectedEventId) && (
//         <EventDetails
//           eventId={editEventId || selectedEventId}
//           viewOnly={!!selectedEventId && !editEventId}
//           onClose={() => {
//             setEditEventId(null);
//             setSelectedEventId(null);
//           }}
//           onSuccess={() => {
//             setEditEventId(null);
//             setSelectedEventId(null);
//             loadData(); // refresh events
//           }}
//           onRegisterSuccess={handleRegisterSuccess}
//           isPast={pastEvents.some(e => e.id === (editEventId || selectedEventId))}
//         />
//       )}

//       {showCreateEvent && (
//         <CreateEvent
//           onClose={() => setShowCreateEvent(false)}
//           onSuccess={() => {
//             setShowCreateEvent(false);
//             loadData(); // refresh events list
//           }}
//         />
//       )}

//       {showMediaModal && (
//         <EventMediaModal
//           eventId={mediaEventId}
//           canUpload={canUpload}
//           onClose={() => {
//             setShowMediaModal(false);
//             setMediaEventId(null);
//           }}
//         />
//       )}

//     </div>
//   );
// };

// export default EventDashboard;