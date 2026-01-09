import { useState, useEffect } from 'react'
import { ticketService } from './services/api'
import Login from './components/Login/Login'
import Sidebar from './components/Sidebar/Sidebar'
import TicketList from './components/TicketList/TicketList'
import TicketDetail from './components/TicketDetail/TicketDetail'
import NewTicket from './components/NewTicket/NewTicket'
import './App.css'

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'))
  const [user, setUser] = useState(null)
  const [tickets, setTickets] = useState([])
  const [view, setView] = useState('tickets')
  const [selectedTicket, setSelectedTicket] = useState(null)

  useEffect(() => {
    if (token) {
      loadTickets()
      loadUser()
    }
  }, [token])

  const loadUser = () => {
    const stored = localStorage.getItem('user')
    if (stored) {
      setUser(JSON.parse(stored))
    }
  }

  const loadTickets = async () => {
    try {
      const res = await ticketService.getAll()
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
    localStorage.removeItem('user')
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

  const stats = {
    total: tickets.length,
    new: tickets.filter(t => t.status === 'NEW').length,
    open: tickets.filter(t => t.status === 'OPEN').length,
  }

  return (
    <div className="app-container">
      <Sidebar 
        user={user} 
        stats={stats} 
        view={view} 
        setView={setView} 
        onLogout={logout} 
      />

      <main className="main-content">
        {view === 'tickets' && (
          <TicketList 
            tickets={tickets} 
            onRefresh={loadTickets} 
            onSelect={openTicketDetail}
          />
        )}
        {view === 'new' && (
          <NewTicket onCreated={() => { loadTickets(); setView('tickets') }} />
        )}
        {view === 'detail' && selectedTicket && (
          <TicketDetail 
            ticket={selectedTicket} 
            onBack={() => { loadTickets(); setView('tickets') }}
            onUpdate={(updated) => setSelectedTicket(updated)}
          />
        )}
      </main>
    </div>
  )
}

export default App