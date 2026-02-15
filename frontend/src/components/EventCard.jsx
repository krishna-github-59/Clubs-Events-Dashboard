import React from 'react';
import '../styles/EventCard.css';
import { getCategoryName } from '../utils/categoryUtils';

const EventCard = ({ event, onViewDetails, onEdit, onDelete, isPast=false, onAddMedia, onViewMedia }) => {
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


  return (
    <div className="event-card">
      <div className="event-card-image-container">
        <img 
          src={event.imageUrl || 'https://via.placeholder.com/400x250?text=Event+Image'} 
          alt={event.name}
          className="event-card-image"
        />
        {/* 🔥 Admin actions */}
        {!isPast && (event.canEdit || event.canDelete) && (
          <div className="event-admin-actions">
            {event.canEdit && (
              <span
                className="event-admin-icon edit"
                title="Edit Event"
                onClick={(e) => {
                  e.stopPropagation();
                  onEdit(event.id);
                }}
              >
                ✏️
              </span>
            )}
            {event.canDelete && (
              <span
                className="event-admin-icon delete"
                title="Delete Event"
                onClick={(e) => {
                  e.stopPropagation();
                  onDelete(event.id);
                }}
              >
                🗑
              </span>
            )}
          </div>
        )}
        {(!event.entryFee || event.entryFee === 0) ? (
          <span className="event-badge free">Free</span>
        ) : (
          <span className="event-badge paid">Paid</span>
        )}
      </div>
      <div className="event-card-content">
        <h3 className="event-card-title">{event.name}</h3>
        <div className="event-card-meta">
          <span className="event-category">{getCategoryName(event.clubName) || 'General'}</span>
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
          {isPast && (
            <div>
              {event.canAddMedia && (<button
                className="btn-secondary"
                onClick={(e) => {
                  e.stopPropagation();
                  onAddMedia();
                }}
              >
                Add Media
              </button>)}
              <button
                className="btn-secondary"
                onClick={(e) => {
                  e.stopPropagation();
                  onViewMedia();
                }}
              >
                View Media
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default EventCard;
