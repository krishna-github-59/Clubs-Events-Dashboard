import { useEffect, useState } from "react";
import EventCard from "./EventCard";
import MediaModal from "./MediaModal";
import EventService from "../services/EventService";
import { useParams } from "react-router-dom";

function PastEventsClub() {
    const { clubId } = useParams();
    const [events, setEvents] = useState([]);
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [showModal, setShowModal] = useState(false);

    useEffect(() => {
        async function fetchPastEvents() {
        try {
            const res = await EventService.getPastEventsByClub(clubId);
            setEvents(res.data || []);
        } catch (err) {
            console.error(err);
        }
        }

        fetchPastEvents();
    }, [clubId]);

    const openMediaModal = (event) => {
        setSelectedEvent(event);
        setShowModal(true);
    };

    const closeMediaModal = () => {
        setSelectedEvent(null);
        setShowModal(false);
    };

    return (
        <div>
        <h2>Past Events</h2>
        <div className="events-grid">
            {events.map((event) => (
                <EventCard
                    key={event.id}
                    event={event}
                    onClick={() => openMediaModal(event)}
                />
            ))}
        </div>

        {showModal && selectedEvent && (
            <MediaModal
                event={selectedEvent}
                onClose={closeMediaModal}
            />
        )}
        </div>
    );
}

export default PastEventsClub;