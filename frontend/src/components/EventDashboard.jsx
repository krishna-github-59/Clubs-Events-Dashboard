import React, { useState, useEffect } from 'react';
import EventCard from './EventCard';
import EventDetails from './EventDetails';
import EventService from '../services/EventService';
import '../styles/EventDashboard.css';
import { getCategoryName } from '../utils/categoryUtils';

const EventDashboard = () => {
  const [events, setEvents] = useState([]);
  const [clubs, setClubs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All Categories');
  const [selectedEventId, setSelectedEventId] = useState(null);
  const [totalEvents, setTotalEvents] = useState(0);
  const [upcomingCount, setUpcomingCount] = useState(0);

  const categories = [
    'All Categories',
    'Technical',
    'Music',
    'Photography',
    'Sports',
    'Drama'
  ];

  useEffect(() => {
    loadData();
  }, []);

  
  useEffect(() => {
    if (!searchQuery && selectedCategory === 'All Categories'){
      loadData(); 
      return;
    }
    filterEvents();
  }, [searchQuery, selectedCategory]);



  const loadData = async () => {
    try {
      setLoading(true);
      const [eventsResponse, clubsResponse] = await Promise.all([
        EventService.getAllEvents(),
        EventService.getAllClubs()
      ]);

      if (eventsResponse.success) {
        const allEvents = eventsResponse.data || [];
        setEvents(allEvents);
        setTotalEvents(allEvents.length);
        const upcoming = allEvents.filter(event => {
          const eventDate = new Date(event.date);
          return eventDate >= new Date();
        });
        setUpcomingCount(upcoming.length);
      }

      if (clubsResponse.success) {
        setClubs(clubsResponse.data || []);
      }
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };


  const filterEvents = async () => {
    try {
      setLoading(true);
      // const filters = {};

      // if (searchQuery) {
      //   filters.name = searchQuery;
      // }

      // if (selectedCategory !== 'All Categories') {
      //   filters.category = selectedCategory;
      // }

      // const response = filters.clubId || filters.category
      //   ? await EventService.filterEvents(filters)
      //   : await EventService.getAllEvents();
      const response = await EventService.getAllEvents();

      if (response.success) {
        // let filteredEvents = response.data || [];
        // console.log("filtered events:", filteredEvents);
        // setEvents(filteredEvents);
        // setTotalEvents(filteredEvents.length);
        let filteredEvents = response.data || [];

        if (selectedCategory !== 'All Categories') {
          filteredEvents = filteredEvents.filter(event => {
            const eventCategory = getCategoryName(event.clubName);
            return eventCategory === selectedCategory;
          });
          console.log("club filtered events", filterEvents);
        }

        if (searchQuery) {
          const query = searchQuery.toLowerCase().trim();
          filteredEvents = filteredEvents.filter(event =>
            event.name.toLowerCase().includes(query)
          );
          console.log("filtered events:", filteredEvents);
        }

        setEvents(filteredEvents);
        setTotalEvents(filteredEvents.length);
      }
    } catch (error) {
      console.error('Error filtering events:', error);
    } finally {
      setLoading(false);
    }
  };


  const handleViewDetails = (eventId) => {
    setSelectedEventId(eventId);
  };

  const handleCloseDetails = () => {
    setSelectedEventId(null);
  };

  const handleRegisterSuccess = () => {
    handleCloseDetails();
    loadData();
  };



  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <div className="header-title-section">
            <span className="calendar-icon">📅</span>
            <div>
              <h1 className="dashboard-title">College Events Dashboard</h1>
              <p className="dashboard-subtitle">Discover and join club events</p>
            </div>
          </div>
          <div className="header-stats">
            <div className="stat-item">
              <span className="stat-label">Total Events</span>
              <span className="stat-value">{totalEvents}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">Upcoming</span>
              <span className="stat-value">{upcomingCount}</span>
            </div>
          </div>
        </div>
        <div className="search-container">
          <input
            type="text"
            className="search-input"
            placeholder="Search events, clubs, or keywords..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </header>

      <div className="dashboard-content">
        <aside className="filters-sidebar">
          <div className="filters-header">
            <h2>Filters</h2>
            <span className="filter-badge">{events.length || 0} Events</span>
          </div>

          <div className="filter-section">
            <h3 className="filter-title">Category</h3>
            <div className="filter-options">
              {categories.map(category => (
                <label key={category} className="filter-option">
                  <input
                    type="radio"
                    name="category"
                    value={category}
                    checked={selectedCategory === category}
                    onChange={(e) => setSelectedCategory(e.target.value)}
                  />
                  <span className="filter-label">{category}</span>
                </label>
              ))}
            </div>
          </div>
        </aside>

        <main className="events-grid">
          {loading ? (
            <div className="loading">Loading events...</div>
          ) : events.length === 0 ? (
            <div className="no-events">No events found</div>
          ) : (
            events.map(event => (
              <EventCard
                key={event.id}
                event={event}
                onViewDetails={handleViewDetails}
              />
            ))
          )}
        </main>
      </div>

      {selectedEventId && (
        <EventDetails
          eventId={selectedEventId}
          onClose={handleCloseDetails}
          onRegisterSuccess={handleRegisterSuccess}
        />
      )}
    </div>
  );
};

export default EventDashboard;
