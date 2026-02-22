import { jwtDecode } from 'jwt-decode';

// const API_BASE_URL = 'http://localhost:8080/api';
const API_BASE_URL = process.env.REACT_APP_BASE_URL;


class EventService {
  getAuthHeaders(){
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
  }


  // Get all events
  async getAllEvents() {
    const response = await fetch(`${API_BASE_URL}/events/all`);
    return response.json();
  }


  // Get event by ID
  async getEventById(id) {
    const response = await fetch(`${API_BASE_URL}/events/${id}`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }


  // Get events for logged-in club admin
  async getMyClubEvents() {
    const response = await fetch(`${API_BASE_URL}/events/my-club`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }


  async createEvent(eventData, posterFile) {
    const formData = new FormData();

    // VERY IMPORTANT: event must be STRING
    formData.append('event', JSON.stringify(eventData));

    if (posterFile) {
      formData.append('poster', posterFile);
    }

    const response = await fetch(`${API_BASE_URL}/events/add`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: formData,
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message || 'Failed to create event');
    }

    return result;
  }


  // async updateEvent(id, payload) {
  //   const response = await fetch(`${API_BASE_URL}/events/update/${id}`, {
  //     method: "PUT",
  //     headers: {
  //       "Content-Type": "application/json",
  //       Authorization: `Bearer ${token}`
  //     },
  //     body: JSON.stringify(payload)
  //   });
  //     if (!response.ok) {
  //       if (response.status === 403) {
  //         throw new Error('Access denied');
  //       }
  //       throw new Error('Update failed');
  //     }
  //   return response.json();
  // }
  // EventService.js
  async updateEvent(id, eventData, posterFile) {
    const formData = new FormData();

    // IMPORTANT: event must be JSON
    formData.append(
      "event",
      new Blob([JSON.stringify(eventData)], { type: "application/json" })
    );

    // optional poster
    if (posterFile) {
      formData.append("poster", posterFile);
    }

    const response = await fetch(
      `${API_BASE_URL}/events/update/${id}`,
      {
        method: "PUT",
        headers: this.getAuthHeaders(),
        body: formData
      }
    );

    // handle errors cleanly
    if (!response.ok) {
      const err = await response.json().catch(() => null);
      throw new Error(err?.message || "Access denied");
    }

    return response.json();
  }



  async deleteEvent(id){
    const response = await fetch(`${API_BASE_URL}/events/delete/${id}`, {
      method: "DELETE",
      headers: this.getAuthHeaders()
    });
    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || "Delete failed");
    }
    return response.json();
  }



  // Filter events
  async filterEvents(filters = {}) {
    const params = new URLSearchParams();
    if (filters.clubId) params.append('clubId', filters.clubId);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);
    if (filters.name) params.append('name', filters.name);

    const response = await fetch(`${API_BASE_URL}/events/filter?${params.toString()}`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }



  // Get all clubs
  async getAllClubs() {
    const response = await fetch(`${API_BASE_URL}/clubs/all`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }



  async getEventsByClub(clubId){
    const response = await fetch(`${API_BASE_URL}/events/club/${clubId}`, {});
    return response.json();
  }



  // Get upcoming events for logged-in club admin
  async getMyClubUpcomingEvents() {
    const response = await fetch(`${API_BASE_URL}/events/my-club/upcoming`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }



 // Get past events for logged-in club admin
  async getMyClubPastEvents() {
    const response = await fetch(
      `${API_BASE_URL}/events/my-club/past`,
      {
        headers: this.getAuthHeaders(),
      }
    );
    return response.json();
  }



  // Get all upcoming events for students
  async getAllUpcomingEvents() {
    const response = await fetch(`${API_BASE_URL}/events/upcoming`, {
      headers: this.getAuthHeaders()
    });
    return response.json();
  }



 // Get all past events for students
  async getAllPastEvents() {
    const response = await fetch(`${API_BASE_URL}/events/past`, {
        headers: this.getAuthHeaders(),
      }
    );
    return response.json();
  }



  // Upload media for an event
  async uploadEventMedia(eventId, file) {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${API_BASE_URL}/media/upload/${eventId}`, {
      method: "POST",
      headers: this.getAuthHeaders(),
      body: formData
    });

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      throw new Error(err?.message || "Failed to upload media");
    }

    return response.json();
  }



  // Get media for an event
  async getEventMedia(eventId) {
    const response = await fetch(`${API_BASE_URL}/media/get/${eventId}`, {
      headers: this.getAuthHeaders()
    });

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      throw new Error(err?.message || "Failed to fetch media");
    }

    return response.json();
  }



  // Delete media by mediaId
  async deleteEventMedia(mediaId) {
    const response = await fetch(`${API_BASE_URL}/media/delete/${mediaId}`, {
      method: "DELETE",
      headers: this.getAuthHeaders()
    });

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      throw new Error(err?.message || "Failed to delete media");
    }

    return response.json();
  }



  // Register for event
  async registerForEvent(payload) {
    // const token = localStorage.getItem('token');

    const res = await fetch(`${API_BASE_URL}/events/register`, {
      method: "POST",
        headers: {
            ...this.getAuthHeaders(),
            "Content-Type": "application/json" // ✅ needed for JSON body
        },
      body: JSON.stringify(payload)
    });

    let data = null;
    try {
      data = await res.json();
    } catch {}

    if (!res.ok) {
      const error = new Error(data?.message || "Request failed");
      error.status = res.status;
      throw error;
    }

    return data;
  }



  // Create payment order
  async createPaymentOrder(eventId, email) {
    const res = await fetch(
      `${API_BASE_URL}/payments/create-order/${eventId}?userEmail=${email}`,
      { method: "POST" }
    );

    return res.json();
  }



  // Verify payment
  async verifyPayment(paymentData) {
    const response = await fetch(`${API_BASE_URL}/payments/verify`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(paymentData)
    });
    return response.json();
  }



  // Login
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password })
    });
    const data = await response.json();

    if (data.success && data.data?.token) {
        const token = data.data.token;

        // 1️⃣ Save token
        localStorage.setItem('token', token);

        // 2️⃣ Decode token to get email
        const decoded = jwtDecode(token);
        const userEmail = decoded.sub;

        // 3️⃣ Fetch full user from backend
        const userRes = await fetch(
          `${API_BASE_URL}/users/email/${userEmail}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );

        const fullUser = await userRes.json();

        const safeUser = {
          id: fullUser.data.id,
          name: fullUser.data.name,
          email: fullUser.data.email,
          role: fullUser.data.role,
          club: fullUser.data.club,
        };

        // 4️⃣ Save FULL user (this includes id)
        localStorage.setItem('user', JSON.stringify(safeUser));
      }
      
    return data;
  }



  // Register student
  async registerStudent(name, email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/register/student`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ name, email, password })
    });
    return response.json();
  }
}



const eventService = new EventService();
export default eventService;