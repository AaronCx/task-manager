import axiosClient from './axiosClient';
import { Task, TaskRequest, TaskStatus } from '../types';

export const tasksApi = {
  /** Fetch all tasks, optionally filtered by status. */
  getAll: (status?: TaskStatus) =>
    axiosClient.get<Task[]>('/tasks', { params: status ? { status } : undefined }),

  getById: (id: number) =>
    axiosClient.get<Task>(`/tasks/${id}`),

  create: (data: TaskRequest) =>
    axiosClient.post<Task>('/tasks', data),

  update: (id: number, data: TaskRequest) =>
    axiosClient.put<Task>(`/tasks/${id}`, data),

  remove: (id: number) =>
    axiosClient.delete(`/tasks/${id}`),
};
