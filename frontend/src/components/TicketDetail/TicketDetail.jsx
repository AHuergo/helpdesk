import { useState, useEffect } from 'react'
import { ticketService } from '../../services/api'
import './TicketDetail.css'

function TicketDetail({ ticket, onBack, onUpdate }) {
  const [messages, setMessages] = useState([])
  const [newMessage, setNewMessage] = useState('')

  useEffect(() => {
    loadMessages()
  }, [ticket.id])

  const loadMessages = async () => {
    try {
      const res = await ticketService.getMessages(ticket.id)
      setMessages(res.data)
    } catch (err) {
      console.error(err)
    }
  }

  const sendMessage = async (e) => {
    e.preventDefault()
    if (!newMessage.trim()) return
    try {
      await ticketService.addMessage(ticket.id, ticket.requester.id, newMessage)
      setNewMessage('')
      loadMessages()
    } catch (err) {
      console.error(err)
    }
  }

  const updateStatus = async (status) => {
    try {
      const res = await ticketService.updateStatus(ticket.id, status)
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

export default TicketDetail