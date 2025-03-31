// src/components/ProtectedRoute.js
// Для защиты маршрутов от неавторизованных пользователей.
import { Navigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
import apiClient from '../config/apiClient';

const ProtectedRoute = ({ children }) => {
  const [isValid, setIsValid] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        // Запрос к защищенному эндпоинту (сервер проверит куки автоматически)
        await apiClient.get('/api/auth/check', { withCredentials: true });

        // Если запрос прошел успешно (статус 200), пользователь авторизован
        setIsValid(true);
      } catch (error) {
        console.error('Ошибка проверки авторизации:', error);
        setIsValid(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  if (isLoading) {
    return <div>Проверка авторизации...</div>;
  }

  if (!isValid) {
     // Через state={{ from: location }} можно вернуть пользователя на страницу, которую он пытался открыть до входа
     return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

export default ProtectedRoute;
