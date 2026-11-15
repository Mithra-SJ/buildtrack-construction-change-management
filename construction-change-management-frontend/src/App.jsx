import { useEffect, useMemo, useState } from 'react'
import { api } from './api'

const money = (value) => new Intl.NumberFormat('en-IN', {
  style: 'currency', currency: 'INR', maximumFractionDigits: 0
}).format(Number(value || 0))

const date = (value) => value ? new Date(`${value}T00:00:00`).toLocaleDateString('en-IN', {
  day: '2-digit', month: 'short', year: 'numeric'
}) : '—'

function App() {
  const [page, setPage] = useState('Overview')
  const [projects, setProjects] = useState([])
  const [changes, setChanges] = useState([])
  const [selectedProject, setSelectedProject] = useState('all')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [showProjectForm, setShowProjectForm] = useState(false)
  const [showChangeForm, setShowChangeForm] = useState(false)
  const [decision, setDecision] = useState(null)
  const [decisionComment, setDecisionComment] = useState('')
  const [search, setSearch] = useState('')

  async function loadData() {
    setLoading(true); setError('')
    try {
      const [projectData, changeData] = await Promise.all([
        api.getProjects(), api.getChangeRequests()
      ])
      setProjects(projectData || [])
      setChanges(changeData || [])
    } catch (e) {
      setError(`${e.message}. Check that Spring Boot is running on port 8080 and PostgreSQL is connected.`)
    } finally { setLoading(false) }
  }

  useEffect(() => { loadData() }, [])

  const visibleChanges = useMemo(() => {
    return changes.filter((item) => {
      const projectMatch = selectedProject === 'all' || String(item.projectId) === selectedProject
      const query = search.toLowerCase()
      const searchMatch = !query || `${item.title} ${item.projectCode} ${item.category} ${item.status}`.toLowerCase().includes(query)
      return projectMatch && searchMatch
    })
  }, [changes, selectedProject, search])

  const pending = changes.filter(c => ['DRAFT', 'SUBMITTED', 'PENDING'].includes(c.status)).length
  const approved = changes.filter(c => c.status === 'APPROVED').length
  const rejected = changes.filter(c => c.status === 'REJECTED').length
  const totalExposure = changes.filter(c => c.status === 'APPROVED').reduce((sum, c) => sum + Number(c.estimatedCost || 0), 0)

  async function handleProjectSubmit(event) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    try {
      await api.createProject({
        projectCode: form.get('projectCode').trim(),
        name: form.get('name').trim(),
        budget: Number(form.get('budget')),
        startDate: form.get('startDate'),
        plannedEndDate: form.get('plannedEndDate')
      })
      setShowProjectForm(false); setNotice('Project created successfully.'); await loadData()
    } catch (e) { setError(e.message) }
  }

  async function handleChangeSubmit(event) {
    event.preventDefault()
    const form = new FormData(event.currentTarget)
    try {
      await api.createChangeRequest({
        projectId: Number(form.get('projectId')),
        title: form.get('title').trim(),
        description: form.get('description').trim(),
        category: form.get('category'),
        estimatedCost: Number(form.get('estimatedCost')),
        estimatedDelayDays: Number(form.get('estimatedDelayDays'))
      })
      setShowChangeForm(false); setNotice('Change request created successfully.'); await loadData()
    } catch (e) { setError(e.message) }
  }

  async function submitDecision() {
    if (!decision) return
    try {
      if (decision.action === 'approve') await api.approveChange(decision.item.id, decisionComment)
      else await api.rejectChange(decision.item.id, decisionComment)
      setDecision(null); setDecisionComment('')
      setNotice(`Change request ${decision.action === 'approve' ? 'approved' : 'rejected'}.`)
      await loadData()
    } catch (e) { setError(e.message) }
  }

  async function makeBaseline(project) {
    if (!window.confirm(`Create the initial baseline for ${project.projectCode}?`)) return
    try {
      await api.createBaseline(project.id)
      setNotice(`Baseline created for ${project.projectCode}.`)
      await loadData()
    } catch (e) { setError(e.message) }
  }

  const navItems = [
    { label: 'Overview', icon: 'bi-grid-1x2' },
    { label: 'Projects', icon: 'bi-buildings' },
    { label: 'Change Requests', icon: 'bi-arrow-left-right' }
  ]

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark"><i className="bi bi-buildings" /></div><div><strong>BuildTrack</strong><small>PROJECT CONTROLS</small></div></div>
      <div className="workspace-label">WORKSPACE</div>
      <nav className="nav flex-column">
        {navItems.map(item => <button key={item.label} className={`nav-link ${page === item.label ? 'active' : ''}`} onClick={() => setPage(item.label)}><i className={`bi ${item.icon}`} />{item.label}</button>)}
      </nav>
      <div className="sidebar-bottom"><div className="help-card"><i className="bi bi-info-circle" /><div><strong>Project workspace</strong><span>Track scope, cost and schedule changes in one place.</span></div></div><div className="profile"><div className="avatar">PM</div><div><strong>Project Manager</strong><span>Workspace member</span></div><i className="bi bi-three-dots ms-auto" /></div></div>
    </aside>

    <main className="main-area">
      <header className="topbar"><div className="breadcrumb-text">Workspace <i className="bi bi-chevron-right" /> <strong>{page}</strong></div><div className="top-actions"><span className="system-status"><span /> System connected</span><button className="icon-button" title="Refresh data" onClick={loadData}><i className="bi bi-arrow-clockwise" /></button><div className="avatar small-avatar">PM</div></div></header>
      <div className="content">
        <div className="page-heading"><div><div className="eyebrow">CONSTRUCTION CHANGE CONTROL</div><h1>{page === 'Overview' ? 'Project overview' : page}</h1><p>{page === 'Overview' ? 'A clear view of project activity, approvals and cost impact.' : page === 'Projects' ? 'Manage project records and approved starting baselines.' : 'Review requested scope changes and record decisions.'}</p></div><div className="heading-actions">{page !== 'Change Requests' && <button className="btn btn-outline-secondary" onClick={() => setShowProjectForm(true)}><i className="bi bi-plus-lg me-2" />New project</button>}<button className="btn btn-primary" onClick={() => setShowChangeForm(true)} disabled={projects.length === 0}><i className="bi bi-plus-lg me-2" />New change request</button></div></div>

        {error && <div className="alert alert-danger alert-dismissible"><i className="bi bi-exclamation-triangle me-2" />{error}<button className="btn-close" onClick={() => setError('')} /></div>}
        {notice && <div className="alert alert-success alert-dismissible"><i className="bi bi-check-circle me-2" />{notice}<button className="btn-close" onClick={() => setNotice('')} /></div>}

        {loading ? <div className="loading-state"><div className="spinner-border text-primary" /><span>Loading workspace data…</span></div> : <>
          {page === 'Overview' && <Overview projects={projects} changes={changes} pending={pending} approved={approved} rejected={rejected} totalExposure={totalExposure} onNavigate={setPage} onDecision={setDecision} />}
          {page === 'Projects' && <Projects projects={projects} onBaseline={makeBaseline} />}
          {page === 'Change Requests' && <><div className="filter-bar"><div className="search-box"><i className="bi bi-search" /><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search requests, project or category…" /></div><select className="form-select project-filter" value={selectedProject} onChange={e => setSelectedProject(e.target.value)}><option value="all">All projects</option>{projects.map(p => <option key={p.id} value={p.id}>{p.projectCode} · {p.name}</option>)}</select></div><ChangeTable changes={visibleChanges} onDecision={setDecision} /></>}
        </>}
        <footer className="footer"><span>BuildTrack · Construction Change Management</span><span>Internal project workspace</span></footer>
      </div>
    </main>

    {showProjectForm && <Modal title="Create a project" onClose={() => setShowProjectForm(false)}><form onSubmit={handleProjectSubmit}><div className="row g-3"><Field label="Project code" name="projectCode" placeholder="e.g. PRJ-2026-001" required /><Field label="Project name" name="name" placeholder="Enter project name" required full /><Field label="Approved budget (₹)" name="budget" type="number" min="0.01" step="0.01" required /><Field label="Start date" name="startDate" type="date" required /><Field label="Planned end date" name="plannedEndDate" type="date" required /></div><ModalActions onCancel={() => setShowProjectForm(false)} submit="Create project" /></form></Modal>}

    {showChangeForm && <Modal title="Raise a change request" onClose={() => setShowChangeForm(false)}><form onSubmit={handleChangeSubmit}><div className="row g-3"><div className="col-12"><label className="form-label">Project</label><select className="form-select" name="projectId" required defaultValue=""><option value="" disabled>Select a project</option>{projects.map(p => <option key={p.id} value={p.id}>{p.projectCode} · {p.name}</option>)}</select></div><Field label="Change title" name="title" placeholder="Short, clear title" required full /><div className="col-12"><label className="form-label">Description</label><textarea className="form-control" name="description" rows="3" placeholder="Describe what needs to change and why…" required /></div><div className="col-md-6"><label className="form-label">Category</label><select className="form-select" name="category" required><option value="">Select category</option>{['SCOPE', 'DESIGN', 'MATERIAL', 'SCHEDULE', 'COST', 'OTHER'].map(v => <option key={v}>{v}</option>)}</select></div><Field label="Estimated cost impact (₹)" name="estimatedCost" type="number" min="0" step="0.01" required /><Field label="Estimated delay (days)" name="estimatedDelayDays" type="number" min="0" step="1" required /></div><ModalActions onCancel={() => setShowChangeForm(false)} submit="Submit request" /></form></Modal>}

    {decision && <Modal title={`${decision.action === 'approve' ? 'Approve' : 'Reject'} change request`} onClose={() => setDecision(null)}><div className="decision-summary"><span className="muted-label">REQUEST</span><strong>{decision.item.title}</strong><span>{decision.item.projectCode} · {money(decision.item.estimatedCost)} · {decision.item.estimatedDelayDays} day(s)</span></div><label className="form-label">Decision comment <span className="text-muted">(optional)</span></label><textarea className="form-control" rows="3" value={decisionComment} onChange={e => setDecisionComment(e.target.value)} placeholder="Add context for the decision…" /><div className="modal-actions"><button className="btn btn-outline-secondary" onClick={() => setDecision(null)}>Cancel</button><button className={`btn ${decision.action === 'approve' ? 'btn-success' : 'btn-danger'}`} onClick={submitDecision}>Confirm {decision.action}</button></div></Modal>}
  </div>
}

function Overview({ projects, changes, pending, approved, rejected, totalExposure, onNavigate, onDecision }) {
  const recent = [...changes].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 6)
  return <>
    <div className="metrics-grid">
      <Metric icon="bi-buildings" label="Active projects" value={projects.length} note="Projects in workspace" tone="blue" />
      <Metric icon="bi-hourglass-split" label="Awaiting decision" value={pending} note="Requests to review" tone="amber" />
      <Metric icon="bi-check2-circle" label="Approved changes" value={approved} note={`${rejected} rejected`} tone="green" />
      <Metric icon="bi-currency-rupee" label="Approved cost impact" value={money(totalExposure)} note="Sum of approved estimates" tone="violet" />
    </div>
    <div className="section-grid">
      <section className="panel recent-panel"><div className="panel-header"><div><h2>Recent change requests</h2><p>Latest activity across your projects</p></div><button className="text-button" onClick={() => onNavigate('Change Requests')}>View all <i className="bi bi-arrow-right" /></button></div><ChangeTable changes={recent} onDecision={onDecision} compact /></section>
      <section className="panel portfolio-panel"><div className="panel-header"><div><h2>Project portfolio</h2><p>Current registered projects</p></div><button className="text-button" onClick={() => onNavigate('Projects')}>View all <i className="bi bi-arrow-right" /></button></div>{projects.length === 0 ? <Empty text="No projects yet. Create your first project to get started." /> : <div className="portfolio-list">{projects.slice(0, 5).map(p => <div className="portfolio-item" key={p.id}><div className="project-icon"><i className="bi bi-building" /></div><div className="portfolio-info"><strong>{p.name}</strong><span>{p.projectCode}</span></div><div className="portfolio-budget">{money(p.budget)}<small>Budget</small></div></div>)}</div>}</section>
    </div>
  </>
}

function Projects({ projects, onBaseline }) {
  return <section className="panel"><div className="panel-header"><div><h2>All projects</h2><p>{projects.length} project(s) registered</p></div></div>{projects.length === 0 ? <Empty text="No projects have been created yet." /> : <div className="table-responsive"><table className="table data-table align-middle"><thead><tr><th>PROJECT</th><th>APPROVED BUDGET</th><th>START DATE</th><th>PLANNED COMPLETION</th><th>STATUS</th><th></th></tr></thead><tbody>{projects.map(p => <tr key={p.id}><td><strong>{p.name}</strong><small className="cell-sub">{p.projectCode}</small></td><td>{money(p.budget)}</td><td>{date(p.startDate)}</td><td>{date(p.plannedEndDate)}</td><td><Status value={p.status} /></td><td><button className="btn btn-sm btn-outline-primary" onClick={() => onBaseline(p)}>Create baseline</button></td></tr>)}</tbody></table></div>}</section>
}

function ChangeTable({ changes, onDecision, compact = false }) {
  if (!changes.length) return <Empty text="No change requests found." />
  return <div className="table-responsive"><table className="table data-table align-middle"><thead><tr><th>CHANGE REQUEST</th>{!compact && <th>PROJECT</th>}<th>CATEGORY</th><th>COST IMPACT</th><th>DELAY</th><th>STATUS</th><th></th></tr></thead><tbody>{changes.map(c => <tr key={c.id}><td><strong>{c.title}</strong><small className="cell-sub">CR-{String(c.id).padStart(4, '0')} · {c.createdAt ? new Date(c.createdAt).toLocaleDateString('en-IN') : 'Recently created'}</small></td>{!compact && <td><span className="project-code">{c.projectCode}</span></td>}<td><span className="category-pill">{c.category}</span></td><td>{money(c.estimatedCost)}</td><td>{c.estimatedDelayDays} days</td><td><Status value={c.status} /></td><td>{['DRAFT', 'SUBMITTED', 'PENDING'].includes(c.status) && <div className="action-buttons"><button title="Approve" className="action-approve" onClick={() => onDecision({ item: c, action: 'approve' })}><i className="bi bi-check-lg" /></button><button title="Reject" className="action-reject" onClick={() => onDecision({ item: c, action: 'reject' })}><i className="bi bi-x-lg" /></button></div>}</td></tr>)}</tbody></table></div>
}

function Metric({ icon, label, value, note, tone }) { return <div className="metric-card"><div className={`metric-icon ${tone}`}><i className={`bi ${icon}`} /></div><div className="metric-label">{label}</div><div className="metric-value">{value}</div><div className="metric-note">{note}</div></div> }
function Status({ value }) { const status = (value || 'UNKNOWN').toLowerCase(); return <span className={`status-badge ${status}`}>{value || 'UNKNOWN'}</span> }
function Empty({ text }) { return <div className="empty-state"><i className="bi bi-inbox" /><span>{text}</span></div> }
function Field({ label, name, type = 'text', placeholder = '', required = false, full = false, min, step }) { return <div className={full ? 'col-12' : 'col-md-6'}><label className="form-label">{label}</label><input className="form-control" name={name} type={type} placeholder={placeholder} required={required} min={min} step={step} /></div> }
function Modal({ title, children, onClose }) { return <div className="modal-backdrop-custom" onMouseDown={e => e.target === e.currentTarget && onClose()}><div className="modal-card"><div className="modal-header-custom"><h2>{title}</h2><button className="icon-button" onClick={onClose}><i className="bi bi-x-lg" /></button></div>{children}</div></div> }
function ModalActions({ onCancel, submit }) { return <div className="modal-actions"><button type="button" className="btn btn-outline-secondary" onClick={onCancel}>Cancel</button><button type="submit" className="btn btn-primary">{submit}</button></div> }

export default App