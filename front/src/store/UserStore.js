import { create } from 'zustand';
import apiClient from '../config/apiClient';

export const useUserStore = create((set, get) => ({
  isAuth: false,
  isLoading: true,
  user: null, // Добавляем поле для данных пользователя

  // Проверка аутентификации при загрузке
  checkAuth: async () => {
    try {
      const response = await apiClient.get('/api/auth/me');
      if (response.status === 200) {
        set({ isAuth: true, user: response.data, isLoading: false });
      } else if (response.status === 401) {
        set({ isAuth: false, user: null, isLoading: false });
      }
    } catch (error) {
      console.error('Неожиданная ошибка при проверке аутентификации:', error);
      set({ isAuth: false, user: null, isLoading: false });
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
        set({ isAuth: true, user: response.data.user });
        await get().checkAuth(); // Проверяем аутентификацию и обновляем данные
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
      set({ isAuth: false, user: null });
    } catch (error) {
      console.error('Logout failed:', error);
      throw error;
    }
  },

  setUser: (userData) => set({ user: userData }), // Новый метод для обновления данных пользователя
}));

export default useUserStore;