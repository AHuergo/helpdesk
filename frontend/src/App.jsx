import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

const API_URL = 'http://localhost:8080/api'

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [user, setUser] = useState(null)
  const [tickets, setTickets] = useState([])
  const [view, setView] = useState('tickets')
  const [selectedTicket, setSelectedTicket] = useState(null)

  useEffect(() => {
    if (token) {
      loadTickets()
    }
  }, [token])

  const loadTickets = async () => {
    try {
      const res = await axios.get(`${API_URL}/tickets`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      setTickets(res.data)
    } catch (err) {
      console.error(err)
      if (err.response?.status === 403) {
        logout()
      }
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    setToken(null)
    setUser(null)
    setTickets([])
  }

  const openTicketDetail = (ticket) => {
    setSelectedTicket(ticket)
    setView('detail')
  }

  if (!token) {
    return <Login setToken={setToken} setUser={setUser} />
  }

  return (
    <div className="app">
      <header>
        <h1>Helpdesk</h1>
        <button onClick={logout}>Cerrar sesión</button>
      </header>
      
      <nav>
        <button onClick={() => setView('tickets')}>Tickets</button>
        <button onClick={() => setView('new')}>Nuevo Ticket</button>
      </nav>

      <main>
        {view === 'tickets' && (
          <TicketList 
            tickets={tickets} 
            onRefresh={loadTickets} 
            token={token} 
            onSelect={openTicketDetail}
          />
        )}
        {view === 'new' && (
          <NewTicket token={token} onCreated={() => { loadTickets(); setView('tickets') }} />
        )}
        {view === 'detail' && selectedTicket && (
          <TicketDetail 
            ticket={selectedTicket} 
            token={token} 
            onBack={() => { loadTickets(); setView('tickets') }}
            onUpdate={(updated) => setSelectedTicket(updated)}
          />
        )}
      </main>
    </div>
  )
}

function Login({ setToken, setUser }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleLogin = async (e) => {
    e.preventDefault()
    try {
      const res = await axios.post(`${API_URL}/auth/login`, { email, password })
      localStorage.setItem('token', res.data.token)
      setToken(res.data.token)
      setUser(res.data.user)
    } catch (err) {
      setError('Credenciales inválidas')
    }
  }

  return (
    <div className="login">
      <h1>Helpdesk</h1>
      <form onSubmit={handleLogin}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {error && <p className="error">{error}</p>}
        <button type="submit">Entrar</button>
      </form>
    </div>
  )
}

function TicketList({ tickets, onRefresh, token, onSelect }) {
  const updateStatus = async (e, id, status) => {
    e.stopPropagation()
    try {
      await axios.put(`${API_URL}/tickets/${id}/status?status=${status}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      })
      onRefresh()
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="ticket-list">
      <h2>Tickets ({tickets.length})</h2>
      <button onClick={onRefresh}>Actualizar</button>
      
      {tickets.length === 0 ? (
        <p>No hay tickets</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Código</th>
              <th>Asunto</th>
              <th>Estado</th>
              <th>Prioridad</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map(ticket => (
              <tr key={ticket.id} onClick={() => onSelect(ticket)} className="clickable">
                <td>{ticket.code}</td>
                <td>{ticket.subject}</td>
                <td><span className={`status ${ticket.status.toLowerCase()}`}>{ticket.status}</span></td>
                <td><span className={`priority ${ticket.priority.toLowerCase()}`}>{ticket.priority}</span></td>
                <td>
                  {ticket.status === 'NEW' && (
                    <button onClick={(e) => updateStatus(e, ticket.id, 'OPEN')}>Abrir</button>
                  )}
                  {ticket.status === 'OPEN' && (
                    <button onClick={(e) => updateStatus(e, ticket.id, 'RESOLVED')}>Resolver</button>
                  )}
                  {ticket.status === 'RESOLVED' && (
                    <button onClick={(e) => updateStatus(e, ticket.id, 'CLOSED')}>Cerrar</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function NewTicket({ token, onCreated }) {
  const [subject, setSubject] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState('NORMAL')

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await axios.post(`${API_URL}/tickets`, 
        { subject, description, priority },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      setSubject('')
      setDescription('')
      setPriority('NORMAL')
      onCreated()
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="new-ticket">
      <h2>Nuevo Ticket</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Asunto"
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          required
        />
        <textarea
          placeholder="Descripción"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={5}
        />
        <select value={priority} onChange={(e) => setPriority(e.target.value)}>
          <option value="LOW">Baja</option>
          <option value="NORMAL">Normal</option>
          <option value="HIGH">Alta</option>
          <option value="URGENT">Urgente</option>
        </select>
        <button type="submit">Crear Ticket</button>
      </form>
    </div>
  )
}

function TicketDetail({ ticket, token, onBack, onUpdate }) {
  const [messages, setMessages] = useState([])
  const [newMessage, setNewMessage] = useState('')

  useEffect(() => {
    loadMessages()
  }, [ticket.id])

  const loadMessages = async () => {
    try {
      const res = await axios.get(`${API_URL}/tickets/${ticket.id}/messages`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      setMessages(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const sendMessage = async (e) => {
    e.preventDefault()
    if (!newMessage.trim()) return
    try {
      await axios.post(
        `${API_URL}/tickets/${ticket.id}/messages?authorId=${ticket.requester.id}&visibility=PUBLIC`,
        newMessage,
        { 
          headers: { 
            Authorization: `Bearer ${token}`,
            'Content-Type': 'text/plain'
          } 
        }
      )
      setNewMessage('')
      loadMessages()
    } catch (err) {
      console.error(err)
    }
  }

  const updateStatus = async (status) => {
    try {
      const res = await axios.put(`${API_URL}/tickets/${ticket.id}/status?status=${status}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      })
      onUpdate(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="ticket-detail">
      <button className="back-btn" onClick={onBack}>← Volver</button>
      
      <div className="ticket-header">
        <h2>{ticket.subject}</h2>
        <span className="ticket-code">{ticket.code}</span>
      </div>

      <div className="ticket-info">
        <div className="info-row">
          <span className="label">Estado:</span>
          <span className={`status ${ticket.status.toLowerCase()}`}>{ticket.status}</span>
          {ticket.status === 'NEW' && <button onClick={() => updateStatus('OPEN')}>Abrir</button>}
          {ticket.status === 'OPEN' && <button onClick={() => updateStatus('RESOLVED')}>Resolver</button>}
          {ticket.status === 'RESOLVED' && <button onClick={() => updateStatus('CLOSED')}>Cerrar</button>}
        </div>
        <div className="info-row">
          <span className="label">Prioridad:</span>
          <span className={`priority ${ticket.priority.toLowerCase()}`}>{ticket.priority}</span>
        </div>
        <div className="info-row">
          <span className="label">Creado:</span>
          <span>{new Date(ticket.createdAt).toLocaleString()}</span>
        </div>
        {ticket.assignee && (
          <div className="info-row">
            <span className="label">Asignado a:</span>
            <span>{ticket.assignee.name}</span>
          </div>
        )}
      </div>

      <div className="ticket-description">
        <h3>Descripción</h3>
        <p>{ticket.description || 'Sin descripción'}</p>
      </div>

      <div className="ticket-messages">
        <h3>Mensajes ({messages.length})</h3>
        <div className="messages-list">
          {messages.map(msg => (
            <div key={msg.id} className={`message ${msg.visibility.toLowerCase()}`}>
              <div className="message-header">
                <strong>{msg.authorName}</strong>
                <span>{new Date(msg.createdAt).toLocaleString()}</span>
                {msg.visibility === 'INTERNAL' && <span className="internal-badge">Interno</span>}
              </div>
              <p>{msg.body}</p>
            </div>
          ))}
        </div>
        
        <form onSubmit={sendMessage} className="message-form">
          <textarea
            placeholder="Escribe un mensaje..."
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            rows={3}
          />
          <button type="submit">Enviar</button>
        </form>
      </div>
    </div>
  )
}

export default App