// очень круто, когда папка апи исключительно для запросов 

//без базовой настройки писали бы:
// ictient } from " . . / config/apiclient";
// § from "axios";

// const response = await axios.post ('http: //localhost:8080/user /create , data,withcredentials:true});


import apiClient from "../config/apiClient";

export default class UserAPI {
  static async registerUser(data) {
    try {
      const response = await apiClient.post('/api/auth/reg', data, {
        withCredentials: true // Добавляем для поддержки кук
      });
      return response.data; // Теперь методы возвращают только данные из ответа, а не весь response
    } catch (error) {
      // Улучшенная обработка ошибок
      throw new Error(
        error.response?.data?.message || 
        error.message || 
        'Ошибка регистрации'
      );
    }
  }

  static async login(data) {
    try {
      const response = await apiClient.post('/api/auth/login', data, {
        withCredentials: true // Ключевое изменение для работы с куками
      });
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 
        error.message || 
        'Ошибка входа'
      );
    }
  }

  
  static async logout() {
    try {
      await apiClient.post('/api/auth/logout', {}, {
        withCredentials: true
      });
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 
        error.message || 
        'Ошибка выхода'
      );
    }
  }
}

