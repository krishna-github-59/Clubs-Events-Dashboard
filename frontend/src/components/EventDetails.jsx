import React, { useState, useEffect } from 'react';
import EventService from '../services/EventService';
import PaymentModal from './PaymentModal';
import '../styles/EventDetails.css';
import { getClubName } from '../utils/categoryUtils';

const EventDetails = ({ eventId, onClose, onRegisterSuccess }) => {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    loadEvent();
    const userData = localStorage.getItem('user');
    if (userData) {
      setUser(JSON.parse(userData));
    }
  }, [eventId]);

  const loadEvent = async () => {
    try {
      setLoading(true);
      const response = await EventService.getEventById(eventId);
      if (response.success) {
        setEvent(response.data);
      }
    } catch (error) {
      console.error('Error loading event:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('en-US', options);
  };

  const formatTime = (timeString) => {
    if (!timeString) return '';
    const [hours, minutes] = timeString.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
  };

  // const getCategoryName = (category) => {
  //   const categoryMap = {
  //     'TECHNICAL': 'Technical',
  //     'MUSIC': 'Music',
  //     'PHOTOGRAPHY': 'Photography',
  //     'SPORTS': 'Sports',
  //     'MANCHATHANTRA': 'Manchathantra'
  //   };
  //   return categoryMap[category] || category || 'General';
  // };

  const handleRegister = async () => {
    if (!user) {
      alert('Please login to register for events');
      return;
    }

    // If event is free, register directly
    if (!event.entryFee || event.entryFee === 0) {
      try {
        const userData = JSON.parse(localStorage.getItem('user'));
        const userId = userData?.id || user.id;
        const response = await EventService.registerForEvent(eventId, userId);
        if (response.success) {
          alert('Successfully registered for the event!');
          if (onRegisterSuccess) onRegisterSuccess();
        } else {
          alert(response.message || 'Registration failed');
        }
      } catch (error) {
        console.error('Registration error:', error);
        alert('Error registering for event');
      }
    } else {
      // Show payment modal for paid events
      setShowPaymentModal(true);
    }
  };

  const handlePaymentSuccess = async () => {
    setShowPaymentModal(false);
    if (onRegisterSuccess) onRegisterSuccess();
  };

  const handleShare = () => {
    if (navigator.share) {
      navigator.share({
        title: event?.name,
        text: event?.description,
        url: window.location.href
      }).catch(err => console.log('Error sharing', err));
    } else {
      // Fallback: Copy to clipboard
      navigator.clipboard.writeText(window.location.href);
      alert('Event link copied to clipboard!');
    }
  };

  if (loading) {
    return (
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={(e) => e.stopPropagation()}>
          <div className="loading">Loading...</div>
        </div>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={(e) => e.stopPropagation()}>
          <button className="close-button" onClick={onClose}>×</button>
          <div className="error">Event not found</div>
        </div>
      </div>
    );
  }

  return (
    <>
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={(e) => e.stopPropagation()}>
          <button className="close-button" onClick={onClose}>×</button>
          
          <div className="event-details-banner">
            <img 
              src={event.imageUrl || 'https://via.placeholder.com/800x400?text=Event+Banner'} 
              alt={event.name}
            />
          </div>

          <div className="event-details-content">
            <div className="event-details-header">
              <h1 className="event-details-title">{event.name}</h1>
              <div className="event-details-meta">
                <span className="event-org">{event.clubName || 'Club'}</span>
                <span className="event-category-tag">{getClubName(event.category) || 'General'}</span>
              </div>
            </div>

            <div className="event-details-info">
              <div className="info-item">
                <span className="info-label">Date</span>
                <span className="info-value">{formatDate(event.date)}</span>
              </div>
              <div className="info-item">
                <span className="info-label">Time</span>
                <span className="info-value">
                  {formatTime(event.startTime)} - {formatTime(event.endTime)}
                </span>
              </div>
              <div className="info-item">
                <span className="info-label">Location</span>
                <span className="info-value">{event.venue}</span>
              </div>
              {event.entryFee > 0 && (
                <div className="info-item">
                  <span className="info-label">Entry Fee</span>
                  <span className="info-value">₹{event.entryFee}</span>
                </div>
              )}
            </div>

            <div className="event-details-description">
              <h3>About this Event</h3>
              <p>{event.description || 'No description available.'}</p>
            </div>

            <div className="event-details-actions">
              <button className="btn-share" onClick={handleShare}>
                <span>Share</span>
              </button>
              <button className="btn-register" onClick={handleRegister}>
                Register for Event
              </button>
            </div>
          </div>
        </div>
      </div>

      {showPaymentModal && (
        <PaymentModal
          event={event}
          onClose={() => setShowPaymentModal(false)}
          onSuccess={handlePaymentSuccess}
        />
      )}
    </>
  );
};

export default EventDetails;
