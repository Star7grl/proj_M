import authApi from "../config/AuthApi.js";
import apiClient from "../config/apiClient.js";

export default class UserApi {
  static async registerUser(data) {
    return authApi.post("/api/auth/reg", data);
  }

  static async login(data) {
    return authApi.post("/api/auth/login", data);
  }

  static async logout() {
    return authApi.post("/api/auth/logout", {});
  }

  static async uploadPhoto(userId, formData) {
    try {
      // Используем напрямую apiClient вместо authApi для формы с файлом
      const response = await apiClient.post(`/api/users/${userId}/uploadPhoto`, formData, {
        headers: { 
          "Content-Type": "multipart/form-data" 
        },
        withCredentials: true
      });
      return response.data;
    } catch (error) {
      console.error("Детали ошибки:", error.response || error);
      throw error;
    }
  }
}
