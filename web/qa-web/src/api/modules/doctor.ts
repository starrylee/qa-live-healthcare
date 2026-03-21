import request from '../request';
import type { Doctor } from '../types';

export const doctorApi = {
  getAll(): Promise<Doctor[]> {
    return request.get('/doctors');
  },

  getById(id: string): Promise<Doctor> {
    return request.get(`/doctors/${id}`);
  },

  getByUsername(username: string): Promise<Doctor> {
    return request.get(`/doctors/username/${username}`);
  },

  getActive(): Promise<Doctor[]> {
    return request.get('/doctors/active');
  },

  create(data: Omit<Doctor, 'id'>): Promise<Doctor> {
    return request.post('/doctors', data);
  },

  update(id: string, data: Doctor): Promise<Doctor> {
    return request.put(`/doctors/${id}`, data);
  },

  delete(id: string): Promise<void> {
    return request.delete(`/doctors/${id}`);
  },
};
