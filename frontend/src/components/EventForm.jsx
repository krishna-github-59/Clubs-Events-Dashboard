import React from 'react';
import "../styles/EventForm.css"

const EventForm = ({
  form,
  setForm,
  posterPreview,
  onPosterChange,
  isEditMode = false,
}) => {

  const handleChange = (e) => {
    if (!isEditMode) return; // extra safety
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="event-form">

      <input
        name="name"
        placeholder="Event name"
        value={form.name}
        onChange={handleChange}
        disabled={!isEditMode}
      />

      <textarea
        name="description"
        placeholder="Event description"
        value={form.description}
        onChange={handleChange}
        disabled={!isEditMode}
      />

      <input
        type="date"
        name="date"
        value={form.date}
        onChange={handleChange}
        disabled={!isEditMode}
      />

      <div className="time-row">
        <input
          type="time"
          name="startTime"
          value={form.startTime}
          onChange={handleChange}
          disabled={!isEditMode}
        />
        <input
          type="time"
          name="endTime"
          value={form.endTime}
          onChange={handleChange}
          disabled={!isEditMode}
        />
      </div>

      <input
        name="venue"
        placeholder="Venue"
        value={form.venue}
        onChange={handleChange}
        disabled={!isEditMode}
      />

      <input
        type="number"
        name="entryFee"
        min="0"
        placeholder="Entry Fee (₹)"
        value={form.entryFee}
        onChange={handleChange}
        disabled={!isEditMode}
      />

      {/* Poster upload ONLY in edit mode */}
      {isEditMode && onPosterChange && (
        <>
          <input type="file" accept="image/*" onChange={onPosterChange} />
          {posterPreview && (
            <img
              className="poster-preview-img"
              src={posterPreview}
              alt="preview"
            />
          )}
        </>
      )}
    </div>
  );
};

export default EventForm;
