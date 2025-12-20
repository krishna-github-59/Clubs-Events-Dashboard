import React from 'react';
import '../styles/EventCard.css';
import { getClubName } from '../utils/categoryUtils';

const EventCard = ({ event, onViewDetails }) => {
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

  return (
    <div className="event-card">
      <div className="event-card-image-container">
        <img 
          src={event.imageUrl || 'https://via.placeholder.com/400x250?text=Event+Image'} 
          alt={event.name}
          className="event-card-image"
        />
        {(!event.entryFee || event.entryFee === 0) ? (
          <span className="event-badge free">Free</span>
        ) : (
          <span className="event-badge paid">Paid</span>
        )}
      </div>
      <div className="event-card-content">
        <h3 className="event-card-title">{event.name}</h3>
        <div className="event-card-meta">
          <span className="event-category">{getClubName(event.category) || 'General'}</span>
          <span className="event-club">{event.clubName || 'Club'}</span>
        </div>
        <div className="event-card-details">
          <div className="event-detail-item">
            <span className="event-detail-icon">📅</span>
            <span className="event-detail-value">{formatDate(event.date)}</span>
          </div>
          <div className="event-detail-item">
            <span className="event-detail-icon">🕐</span>
            <span className="event-detail-value">
              {formatTime(event.startTime)} - {formatTime(event.endTime)}
            </span>
          </div>
          <div className="event-detail-item">
            <span className="event-detail-icon">📍</span>
            <span className="event-detail-value">{event.venue}</span>
          </div>
        </div>
        <div className="event-card-actions">
          <button 
            className="btn-view-details"
            onClick={() => onViewDetails(event.id)}
          >
            View Details
          </button>
        </div>
      </div>
    </div>
  );
};

export default EventCard;
