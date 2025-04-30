import { create } from "zustand";
import Cookies from "js-cookie";
import apiClient from "../config/apiClient"; // Используем apiClient напрямую

const useUserStore = create((set, get) => ({
  isAuth: false,
  isLoading: true,
  user: null,
  checkAuth: async () => {
    try {
      const response = await apiClient.get("/api/auth/me", { withCredentials: true });
      if (response.status === 200) {
        set({ isAuth: true, user: response.data, isLoading: false });
        Cookies.set("user", JSON.stringify(response.data), { expires: 7 });
      } else if (response.status === 401) {
        set({ isAuth: false, user: null, isLoading: false });
        Cookies.remove("user");
      }
    } catch (error) {
      console.error("Неожиданная ошибка при проверке аутентификации:", error);
      set({ isAuth: false, user: null, isLoading: false });
    }
  },
  register: async (data) => {
    return apiClient.post("/api/auth/reg", data, { withCredentials: true });
  },
  login: async (data) => {
    const response = await apiClient.post("/api/auth/login", data, { withCredentials: true });
    if (response.status >= 200 && response.status < 300) {
      set({ isAuth: true, user: response.data, isLoading: false });
      Cookies.set("user", JSON.stringify(response.data), { expires: 7 });
      await get().checkAuth();
    }
    return response;
  },
  logout: async () => {
    await apiClient.post("/api/auth/logout", {}, { withCredentials: true });
    set({ isAuth: false, user: null, isLoading: false });
    Cookies.remove("user");
  },
  setUser: (userData) => {
    set({ user: userData });
    Cookies.set("user", JSON.stringify(userData), { expires: 7 });
  },
}));

export default useUserStore;