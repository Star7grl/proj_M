import axios from "axios";

// Url хоста на беке (порт на джаве 8080), мне нужно чтобы каждый запрос с куками отправлялся. Базовая настройка для шаблонизации, чтобы потом меньше писать в api f/e
 const apiClient = axios.create({
    baseURL:"http://localhost:8080", // Теперь все запросы будут автоматически включать куки (JWT)
    withCredentials:true
});

apiClient.interceptors.request.use(
  (config) => {
      // Извлекаем токен из кук (предполагаем, что он называется "jwt")
      const token = document.cookie.split('; ')
          .find(row => row.startsWith('jwt='))
          ?.split('=')[1];
      if (token) {
          config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
    (response) => {
      // Успешные ответы (статус 200-299) проходят без изменений
      return response;
    },
    (error) => {
      // Проверяем, есть ли ответ и статус 401
      if (error.response && error.response.status === 401) {
        // Возвращаем "успешный" объект вместо ошибки
        return Promise.resolve({ status: 401, data: null });
      }
      // Для остальных ошибок сохраняем стандартное поведение
      return Promise.reject(error);
    }
  );

export default apiClient; // Экспорт по умолчанию