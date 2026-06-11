import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 120000,
  withCredentials: true
})

request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('role')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// Upload PDF resume
export function uploadResume(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/resume/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// Get resume list with filters
export function listResumes(params) {
  return request.get('/resume/list', { params })
}

// Get single resume by id
export function getResume(id) {
  return request.get(`/resume/${id}`)
}

// Update resume
export function updateResume(id, data) {
  return request.put(`/resume/${id}`, data)
}

// Delete resume
export function deleteResume(id) {
  return request.delete(`/resume/${id}`)
}

// Toggle favorite status
export function toggleFavorite(id, data) {
  return request.put(`/resume/${id}/favorite`, data)
}
