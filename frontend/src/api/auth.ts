import axiosClient from './axiosClient';
import { AuthResponse, LoginRequest, RegisterRequest } from '../types';

export const authApi = {
  register: (data: RegisterRequest) =>
    axiosClient.post<AuthResponse>('/auth/register', data),

  login: (data: LoginRequest) =>
    axiosClient.post<AuthResponse>('/auth/login', data),
};
