const API = '/api'

async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  })
  const text = await response.text()
  let data = null
  try { data = text ? JSON.parse(text) : null } catch { data = text }
  if (!response.ok) {
    const message = data?.message || data?.error || `Request failed (${response.status})`
    throw new Error(message)
  }
  return data
}

export const api = {
  getProjects: () => request('/projects'),
  createProject: (body) => request('/projects', { method: 'POST', body: JSON.stringify(body) }),
  getChangeRequests: (projectId) => request(`/change-requests${projectId ? `?projectId=${projectId}` : ''}`),
  createChangeRequest: (body) => request('/change-requests', { method: 'POST', body: JSON.stringify(body) }),
  approveChange: (id, comment) => request(`/change-requests/${id}/approve`, { method: 'POST', body: JSON.stringify({ comment }) }),
  rejectChange: (id, comment) => request(`/change-requests/${id}/reject`, { method: 'POST', body: JSON.stringify({ comment }) }),
  createBaseline: (projectId) => request(`/projects/${projectId}/baselines`, { method: 'POST' }),
  getBaselines: (projectId) => request(`/projects/${projectId}/baselines`)
}