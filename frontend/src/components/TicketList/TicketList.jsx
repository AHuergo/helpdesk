import { ticketService } from '../../services/api'
import './TicketList.css'

function TicketList({ tickets, onRefresh, onSelect }) {
  const stats = {
    total: tickets.length,
    new: tickets.filter(t => t.status === 'NEW').length,
    open: tickets.filter(t => t.status === 'OPEN').length,
    resolved: tickets.filter(t => t.status === 'RESOLVED').length,
    closed: tickets.filter(t => t.status === 'CLOSED').length,
  }

  const updateStatus = async (e, id, status) => {
    e.stopPropagation()
    try {
      await ticketService.updateStatus(id, status)
      onRefresh()
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div className="ticket-list">
      <div className="stats-row">
        <div className="stat-mini total">
          <span className="stat-number">{stats.total}</span>
          <span className="stat-label">Total</span>
        </div>
        <div className="stat-mini new">
          <span className="stat-number">{stats.new}</span>
          <span className="stat-label">Nuevos</span>
        </div>
        <div className="stat-mini open">
          <span className="stat-number">{stats.open}</span>
          <span className="stat-label">Abiertos</span>
        </div>
        <div className="stat-mini resolved">
          <span className="stat-number">{stats.resolved}</span>
          <span className="stat-label">Resueltos</span>
        </div>
        <div className="stat-mini closed">
          <span className="stat-number">{stats.closed}</span>
          <span className="stat-label">Cerrados</span>
        </div>
      </div>

      <div className="ticket-list-header">
        <h2>Tickets ({tickets.length})</h2>
        <button onClick={onRefresh}>Actualizar</button>
      </div>
      
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

export default TicketList