import type { Doctor } from '../store';

export type { Doctor };

export interface ApiResponse<T> {
  data: T;
  message?: string;
  status?: number;
}
