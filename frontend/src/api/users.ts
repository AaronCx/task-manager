import axiosClient from './axiosClient';

export interface UserSummary {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export const usersApi = {
  getAll: () => axiosClient.get<UserSummary[]>('/users'),
};
