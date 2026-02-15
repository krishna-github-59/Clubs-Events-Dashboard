import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import EventService from "../services/EventService";
import "../styles/EventMediaModal.css";

const EventMediaModal = ({ eventId, canUpload, onClose }) => {
  const [media, setMedia] = useState([]);
  const [file, setFile] = useState(null);

  useEffect(() => {
    loadMedia();
  }, [eventId]);

  const loadMedia = async () => {
    const res = await EventService.getEventMedia(eventId);
    if (res.success) setMedia(res.data || []);
  };

  const handleUpload = async () => {
    if (!file) return alert("Select a file");

    const res = await EventService.uploadEventMedia(eventId, file);
    if (res.success) {
      setFile(null);
      loadMedia();
    }
  };

  const handleDelete = async (mediaId) => {
    if (!window.confirm("Delete this media?")) return;

    try {
        await EventService.deleteEventMedia(mediaId);
        loadMedia(); // refresh list
    } catch (err) {
        alert(err.message);
    }
  };


  return createPortal(
    <div className="modal-backdrop">
      <div className="modal">
        <h2>Event Media</h2>

        {/* Upload section */}
        {canUpload && (
          <div className="upload-section">
            <input
              type="file"
              onChange={(e) => setFile(e.target.files[0])}
            />
            <button onClick={handleUpload}>Upload</button>
          </div>
        )}

        {/* Media list */}
        <div className="media-grid">
          {media.length === 0 ? (
            <p>No media uploaded</p>
          ) : (
           media.map(m => (
            <div key={m.id} className="media-wrapper">
                <img
                src={m.url}
                alt="event media"
                className="media-item"
                />

                {canUpload && (
                <button
                    className="btn-delete-media"
                    onClick={() => handleDelete(m.id)}
                >
                    🗑
                </button>
                )}
            </div>
            ))

          )}
        </div>

        <button className="btn-close" onClick={onClose}>
          Close
        </button>
      </div>
    </div>,
    document.body
  );
};

export default EventMediaModal;
