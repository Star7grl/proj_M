import { create } from 'zustand';
import apiClient from '../config/apiClient';

export const useUserStore = create((set, get) => ({
  isAuth: false,
  isLoading: true,

  // Проверка аутентификации при загрузке
  checkAuth: async () => {
    try {
      await apiClient.get('/api/auth/check', { withCredentials: true });
      set({ isAuth: true, isLoading: false });
    } catch {
      set({ isAuth: false, isLoading: false });
    }
  },

  // Регистрация пользователя
  register: async (data) => {
    try {
      const response = await apiClient.post('/api/auth/reg', data, { 
        withCredentials: true 
      });
      return response;
    } catch (error) {
      throw error;
    }
  },

  // Вход пользователя
  login: async (data) => {
    try {
      const response = await apiClient.post('/api/auth/login', data, { 
        withCredentials: true 
      });
      
      if (response.status >= 200 && response.status < 300) {
        set({ isAuth: true });
        await get().checkAuth(); // Используем get() вместо прямого вызова
      }
      return response;
    } catch (error) {
      throw error;
    }
  },

  // Выход пользователя
  logout: async () => {
    try {
      await apiClient.post('/api/auth/logout', {}, { withCredentials: true });
      set({ isAuth: false });
    } catch (error) {
      console.error('Logout failed:', error);
      throw error;
    }
  },
}));


export default useUserStore;

