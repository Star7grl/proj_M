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
export default apiClient; // Экспорт по умолчанию