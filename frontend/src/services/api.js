import axios from 'axios';

const BASE_URL = 'https://image-steganography-o6mn.onrender.com';

const api = axios.create({
  baseURL: BASE_URL,
});

// Request interceptor
api.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || error.message || 'Something went wrong';
    return Promise.reject(new Error(message));
  }
);

export const stegoAPI = {
  // Encode image
  encodeImage: (file, message, description = '') => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('message', message);
    if (description) formData.append('description', description);
    return api.post('/images/encode', formData);
  },

  // Decode by ID
  decodeById: (id) => api.post(`/images/${id}/decode`),

  // Decode uploaded image
  decodeUploadedImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/images/decode/upload', formData);
  },

  // Get all images (paginated)
  getAllImages: (page = 0, size = 10, sortBy = 'createdAt', direction = 'desc') =>
    api.get(`/images?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`),

  // Get by ID
  getImageById: (id) => api.get(`/images/${id}`),

  // Get by status
  getByStatus: (status, page = 0, size = 10) =>
    api.get(`/images/status/${status}?page=${page}&size=${size}`),

  // Search
  searchImages: (keyword, page = 0, size = 10) =>
    api.get(`/images/search?keyword=${keyword}&page=${page}&size=${size}`),

  // Update
  updateImage: (id, data) => api.put(`/images/${id}`, data),

  // Delete
  deleteImage: (id) => api.delete(`/images/${id}`),

  // Download
  downloadImage: (id) => `${BASE_URL}/images/${id}/download`,

  // Stats
  getStats: () => api.get('/stats'),

  // Latest
  getLatest: () => api.get('/images/latest'),

  // Capacity
  getCapacity: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/images/capacity', formData);
  },

  // Health
  health: () => api.get('/health'),
};

export default stegoAPI;
