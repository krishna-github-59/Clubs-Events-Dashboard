import React, { useState } from 'react';
import EventService from '../services/EventService';
import { getLoggedInUser } from '../utils/authUtils';
import EventForm from './EventForm';
import '../styles/CreateEvent.css';


const CreateEvent = ({ onClose, onSuccess }) => {
  const user = getLoggedInUser();

  const [form, setForm] = useState({
    name: '',
    description: '',
    date: '',
    startTime: '',
    endTime: '',
    venue: '',
    entryFee: 0,
  });

  const [poster, setPoster] = useState(null);
  const [posterPreview, setPosterPreview] = useState(null);
  const [loading, setLoading] = useState(false);

  const handlePosterChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setPoster(file);

    const reader = new FileReader();
    reader.onloadend = () => setPosterPreview(reader.result);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async () => {
    if (!form.name || !form.date || !form.startTime || !form.endTime || !form.venue) {
      alert('Please fill all required fields');
      return;
    }

    try {
      setLoading(true);

      const eventPayload = {
        ...form,
        entryFee: Number(form.entryFee) || 0,
        clubId: user.clubId,
      };

      const response = await EventService.createEvent(eventPayload, poster);

      alert('Event created successfully');
      if (onSuccess) onSuccess(response.data);
      onClose();
    } catch (error) {
      console.error('Create event error:', error);
      alert(error.message || 'Failed to create event');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-event-overlay" onClick={onClose}>
      <div className="create-event-container" onClick={e => e.stopPropagation()}>
        <h2>Create New Event</h2>

        <EventForm
          form={form}
          setForm={setForm}
          poster={poster}
          posterPreview={posterPreview}
          onPosterChange={handlePosterChange}
          isEditMode={true}
        />

        <div className="actions">
          <button className="btn-cancel" onClick={onClose}>Cancel</button>
          <button className="btn-primary" onClick={handleSubmit} disabled={loading}>
            {loading ? 'Creating...' : 'Create Event'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateEvent;
