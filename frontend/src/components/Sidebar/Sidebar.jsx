import './Sidebar.css'

function Sidebar({ user, stats, view, setView, onLogout }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <h1>Helpdesk</h1>
      </div>
      
      <nav className="sidebar-nav">
        <button 
          className={view === 'tickets' ? 'active' : ''} 
          onClick={() => setView('tickets')}
        >
          <span className="nav-icon">📋</span>
          <span>Tickets</span>
          {stats.total > 0 && <span className="nav-badge">{stats.total}</span>}
        </button>
        <button 
          className={view === 'new' ? 'active' : ''} 
          onClick={() => setView('new')}
        >
          <span className="nav-icon">➕</span>
          <span>Nuevo Ticket</span>
        </button>
      </nav>

      <div className="sidebar-stats">
        <div className="sidebar-stat">
          <span className="stat-dot new"></span>
          <span>Nuevos</span>
          <span className="stat-count">{stats.new}</span>
        </div>
        <div className="sidebar-stat">
          <span className="stat-dot open"></span>
          <span>Abiertos</span>
          <span className="stat-count">{stats.open}</span>
        </div>
      </div>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-avatar">{user?.name?.charAt(0) || 'U'}</div>
          <div className="user-details">
            <span className="user-name">{user?.name || 'Usuario'}</span>
            <span className="user-role">{user?.role || 'REQUESTER'}</span>
          </div>
        </div>
        <button className="logout-btn" onClick={onLogout}>Salir</button>
      </div>
    </aside>
  )
}

export default Sidebar