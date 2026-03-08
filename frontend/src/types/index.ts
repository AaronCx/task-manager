// ── Auth ──────────────────────────────────────────────────────────────────────

export interface AuthResponse {
  token: string;
  tokenType: string;
  refreshToken: string;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

// ── Task ──────────────────────────────────────────────────────────────────────

export type TaskStatus   = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate: string | null;        // ISO date string e.g. "2024-06-15"
  ownerId: number;
  ownerName: string;
  assignedToId: number | null;
  assignedToName: string | null;
  createdAt: string;             // ISO datetime
  updatedAt: string;
}

export interface TaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string | null;
  assignedToId?: number | null;
}

// ── Misc ──────────────────────────────────────────────────────────────────────

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
}

export interface AuthUser {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
}
