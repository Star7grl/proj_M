import axios from "axios";

// Url хоста на беке (порт на джаве 8080), мне нужно чтобы каждый запрос с куками отправлялся. Базовая настройка для шаблонизации, чтобы потом меньше писать в api f/e
 const apiClient = axios.create({
    baseURL:"http://localhost:8080", // Теперь все запросы будут автоматически включать куки (JWT)
    withCredentials:true
    // ,
    // headers: {
    //     'Content-Type': 'application/json',
    //     'Accept': 'application/json'
    //   }
});


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