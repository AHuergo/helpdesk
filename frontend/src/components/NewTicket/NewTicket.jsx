import { useState } from 'react'
import { ticketService } from '../../services/api'
import './NewTicket.css'

function NewTicket({ onCreated }) {
  const [subject, setSubject] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState('NORMAL')

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await ticketService.create({ subject, description, priority })
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

export default NewTicket