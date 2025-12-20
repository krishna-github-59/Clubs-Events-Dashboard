// const API_BASE_URL = 'http://localhost:8080/api';
const API_BASE_URL = 'https://clubs-events-dashboard.onrender.com/api';
const token = localStorage.getItem('token');

class EventService {
  // Get all events
  async getAllEvents() {
    const response = await fetch(`${API_BASE_URL}/events/all`);
    return response.json();
  }

  // Get all events in dashboard
  async getDashboardEvents() {
    const response = await fetch(`${API_BASE_URL}/events/dashboard`);
    const result = await response.json();
    return result.data;
  }

  // Get event by ID
  async getEventById(id) {
    const response = await fetch(`${API_BASE_URL}/events/${id}`, {
      headers: token
            ? { Authorization: `Bearer ${token}` }
            : {}
    });
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
      headers: token
            ? { Authorization: `Bearer ${token}` }
            : {}
    });
    return response.json();
  }

  // Get all clubs
  async getAllClubs() {
    const response = await fetch(`${API_BASE_URL}/clubs/all`, {
      headers: token
            ? { Authorization: `Bearer ${token}` }
            : {}
    });
    return response.json();
  }

  // Register for event
  async registerForEvent(eventId, userId, guestId = null) {
    const response = await fetch(`${API_BASE_URL}/events/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        eventId,
        userId,
        guestId
      })
    });
    return response.json();
  }

  // Create payment order
  async createPaymentOrder(eventId, userEmail) {
    const response = await fetch(`${API_BASE_URL}/payments/create-order/${eventId}?userEmail=${userEmail}`);
    return response.json();
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
      localStorage.setItem('token', data.data.token);
      localStorage.setItem('user', JSON.stringify(data.data));
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

export default new EventService();
