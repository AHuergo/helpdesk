import axios from 'axios'

const API_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_URL
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const authService = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (userData) => api.post('/auth/register', userData),
}

export const ticketService = {
  getAll: () => api.get('/tickets'),
  getById: (id) => api.get(`/tickets/${id}`),
  create: (ticket) => api.post('/tickets', ticket),
  updateStatus: (id, status) => api.put(`/tickets/${id}/status?status=${status}`),
  assign: (id, agentId) => api.put(`/tickets/${id}/assign?agentId=${agentId}`),
  getMessages: (id) => api.get(`/tickets/${id}/messages`),
  addMessage: (id, authorId, body, visibility = 'PUBLIC') => 
    api.post(`/tickets/${id}/messages?authorId=${authorId}&visibility=${visibility}`, body, {
      headers: { 'Content-Type': 'text/plain' }
    }),
}

export default api