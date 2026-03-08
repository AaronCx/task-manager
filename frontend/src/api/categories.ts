import axiosClient from './axiosClient';
import { Category } from '../types';

export interface CategoryRequest {
  name: string;
  color?: string;
}

export const categoriesApi = {
  getAll: () => axiosClient.get<Category[]>('/categories'),

  create: (data: CategoryRequest) =>
    axiosClient.post<Category>('/categories', data),

  update: (id: number, data: CategoryRequest) =>
    axiosClient.put<Category>(`/categories/${id}`, data),

  remove: (id: number) =>
    axiosClient.delete(`/categories/${id}`),
};
