import React, { useState, useEffect, useCallback } from 'react';
import { createPortal } from "react-dom";
import EventService from '../services/EventService';
import PaymentModal from './PaymentModal';
import EventForm from './EventForm';
import '../styles/EventDetails.css';
import { getCategoryName } from '../utils/categoryUtils';
import { getLoggedInUser } from '../utils/authUtils';

const EventDetails = ({ eventId, viewOnly = true, isPast = false, onClose, onRegisterSuccess }) => {
  const [event, setEvent] = useState(null);
  const [form, setForm] = useState({
    name: '',
    description: '',
    date: '',
    startTime: '',
    endTime: '',
    venue: '',
    entryFee: 0
  });
  const [posterPreview, setPosterPreview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  // const [user, setUser] = useState(null);


  const loadEvent = useCallback(async () => {
    try {
      setLoading(true);
      const response = await EventService.getEventById(eventId);

      if (response.success) {
        const data = response.data;
        setEvent(data);
        setForm({
          name: data.name || '',
          description: data.description || '',
          date: data.date || '',
          startTime: data.startTime || '',
          endTime: data.endTime || '',
          venue: data.venue || '',
          entryFee: data.entryFee || 0
        });
        setPosterPreview(data.imageUrl || null);
      }
    } catch (error) {
      console.error('Error loading event:', error);
    } finally {
      setLoading(false);
    }
  },[eventId]);


  useEffect(() => {
    loadEvent();

    // const userData = getLoggedInUser();
    // if (userData && userData.id) {
    //   setUser(userData);
    // } else {
    //   setUser(null);
    // }
  }, [loadEvent]);


  const handleUpdate = async () => {
    if (!form.name || !form.date || !form.startTime || !form.endTime || !form.venue) {
      alert('Please fill all required fields');
      return;
    }

    try {
      const updatedEvent = {
        name: form.name,
        description: form.description,
        date: form.date,
        startTime: form.startTime,
        endTime: form.endTime,
        venue: form.venue,
        entryFee: Number(form.entryFee) || 0
      };

      const response = await EventService.updateEvent(eventId, updatedEvent);

      if (response.success) {
        alert('Event updated successfully');
        setEvent(prev => ({ ...prev, ...updatedEvent }));
        if (onClose) onClose();
      } else {
        alert(response.message || 'Failed to update event');
      }
    } catch (error) {
      console.error('Update event error:', error);
      alert('Error updating event');
    }
  };


  const handleRegister = async () => {
    const loggedInUser = getLoggedInUser();
    
    try {
      // const payload = user
      //               ? { eventId }
      //               : { eventId, guestName, guestEmail };

      if (!loggedInUser) {
        alert('Please login to register for events');
        return;
      }
      await EventService.registerForEvent({eventId});

      alert('Successfully registered for the event!');
      if (onRegisterSuccess) onRegisterSuccess();

    } catch (error) {
      if (error.status === 402) {
        // Backend says payment required
        setShowPaymentModal(true);
      } else {
        alert(error.message || 'Registration failed');
      }
    }
  };


  const handlePaymentSuccess = () => {
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
      navigator.clipboard.writeText(window.location.href);
      alert('Event link copied to clipboard!');
    }
  };

  if (loading) {
    return (
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={e => e.stopPropagation()}>
          <div className="loading">Loading...</div>
        </div>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={e => e.stopPropagation()}>
          <button className="close-button" onClick={onClose}>×</button>
          <div className="error">Event not found</div>
        </div>
      </div>
    );
  }



  return createPortal(
    <>
      <div className="event-details-overlay" onClick={onClose}>
        <div className="event-details-container" onClick={e => e.stopPropagation()}>
          <button className="close-button" onClick={onClose}>×</button>

          <div className="event-details-banner">
            <img
              src={posterPreview || 'https://via.placeholder.com/800x400?text=Event+Banner'}
              alt={event.name}
            />
          </div>

          <div className="event-details-content">
            <div className="event-details-header">
              {!viewOnly ? (
                <input
                  className="edit-input title"
                  name="name"
                  value={form.name}
                  onChange={e => setForm(prev => ({ ...prev, name: e.target.value }))}
                />
              ) : (
                <h1 className="event-details-title">{event.name}</h1>
              )}
              <div className="event-details-meta">
                {!viewOnly && (
                  <p className="edit-note">Club & category cannot be changed once an event is created.</p>
                )}
                <span className="event-org">{event.clubName || 'Club'}</span>
                <span className="event-category-tag">{getCategoryName(event.clubName) || 'General'}</span>
              </div>
            </div>

            {/* USE EventForm COMPONENT */}
            <EventForm
              form={form}
              setForm={setForm}
              isEditMode={!viewOnly}
              posterPreview={posterPreview}
            />

            <div className="event-details-actions">
              <button className="btn-share" onClick={handleShare}>Share</button>
              <button 
                onClick={!viewOnly ? handleUpdate : handleRegister} 
                disabled={isPast}
                className={isPast ? 'disabled' : ''}
              >
                {!viewOnly ? "Save Changes" : "Register"}
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
    </>,
    document.body
  );
};

export default EventDetails;
