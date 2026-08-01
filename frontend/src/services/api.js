import api from '../api/axios';

export const authService = {
    login: (data) => api.post('/auth/login', data),
    register: (data) => api.post('/auth/register', data),
};

export const policyService = {
    getMine: () => api.get('/policies/me'),
    getAll: () => api.get('/policies'),
    create: (data) => api.post('/policies', data),
    delete: (id) => api.delete('/policies/' + id),
};

export const claimService = {
    getMine: () => api.get('/claims/me'),
    getAll: () => api.get('/claims'),
    file: (data) => api.post('/claims', data),
    review: (id, data) => api.patch('/claims/' + id + '/review', data),
    delete: (id) => api.delete('/claims/' + id),
};

export const paymentService = {
    getMine: () => api.get('/payments/me'),
    getAll: () => api.get('/payments'),
    create: (data) => api.post('/payments', data),
    settle: (id, data) => api.patch('/payments/' + id + '/settle', data),
    delete: (id) => api.delete('/payments/' + id),
};

export const reportService = {
    getSummary: () => api.get('/reports/summary'),
};

export const customerService = {
    getMine: () => api.get('/customers/me'),
    getAll: () => api.get('/customers'),
    create: (data) => api.post('/customers/me', data),
};

export const documentService = {
    getMine: () => api.get('/documents/me'),
    upload: (formData) => api.post('/documents', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    }),
    download: (id) => api.get('/documents/' + id + '/download', { responseType: 'blob' }),
};