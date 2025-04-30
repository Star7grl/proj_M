// src/components/ProtectedRoute.js
import { Navigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
import apiClient from '../config/apiClient';

// Компонент для защиты маршрутов с проверкой роли
const ProtectedRoute = ({ children, role }) => {
  const [isValid, setIsValid] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const checkAuth = async () => {
      try {
        // Проверка аутентификации
        await apiClient.get('/api/auth/check', { withCredentials: true });

        if (role) {
          // Получение информации о пользователе для проверки роли
          const response = await apiClient.get('/api/auth/me');
          const userRole = response.data.role.replace("ROLE_", ""); // Убираем префикс "ROLE_"
          if (userRole !== role) {
            throw new Error("Недостаточно прав");
          }
        }
        setIsValid(true);
      } catch (error) {
        console.error('Ошибка проверки авторизации:', error);
        setIsValid(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, [role]);

  if (isLoading) {
    return <div>Проверка авторизации...</div>;
  }

  if (!isValid) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
};

export default ProtectedRoute;