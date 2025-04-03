import React, { useState, useEffect } from 'react';
import useUserStore from '../store/UserStore';
import { useNavigate } from 'react-router-dom';
import apiClient from '../config/apiClient';
import '../styles/ForgotPassword.css';


const ProfilePage = () => {
  const { user, setUser, logout } = useUserStore();
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [isEditing, setIsEditing] = useState(false); // Состояние для режима редактирования


  const handleLogout = async () => {
    try {
      await logout();
      if (!useUserStore.getState().isAuth) {
        navigate('/login'); // Перенаправляем на страницу входа
      }
    } catch (error) {
      console.error('Ошибка при выходе:', error);
      // Добавили уведомление пользователя об ошибке
      alert('Не удалось выйти. Попробуйте ещё раз.');
    }
  };

  
  useEffect(() => {
    if (user && user.role === 'ROLE_ADMIN') {
      navigate('/admin');
    }
  }, [user, navigate]);

  const handleEdit = () => {
    setIsEditing(true); // Включаем режим редактирования
  };

  const handleSave = async () => {
    try {
      const response = await apiClient.put(`/api/users/profile/${user.id}`, {
        firstName,
        lastName,
      }, { withCredentials: true });
      setUser(response.data);
      console.log('Профиль успешно обновлен:', response.data);
      setIsEditing(false); // Выключаем режим редактирования после сохранения
    } catch (error) {
      if (error.response) {
        console.error('Ошибка ответа сервера:', error.response.data);
        if (error.response.status === 403) {
          alert('У вас нет прав для редактирования этого профиля');
        }
      } else if (error.request) {
        console.error('Сетевой запрос не выполнен:', error.request);
      } else {
        console.error('Ошибка:', error.message);
      }
      console.error('Ошибка при обновлении профиля:', error);
    }
  };

  if (!user) {
    return <div>Загрузка...</div>;
  }

  return (
    <div className="profile-page">
      <h2>Профиль пользователя</h2>
      <div>
        <label>Имя:</label>
        <input
          type="text"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          disabled={!isEditing} // Поле активно только в режиме редактирования
        />
      </div>
      <div>
        <label>Фамилия:</label>
        <input
          type="text"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          disabled={!isEditing} // Поле активно только в режиме редактирования
        />
      </div>
      {isEditing ? (
        <button onClick={handleSave}>Сохранить</button>
      ) : (
        <button onClick={handleEdit}>Редактировать</button>
      )}
      <p>Email: {user.email}</p>

      <button onClick={handleLogout} className="logout-button">
                  Выйти
                </button>
    </div>
  );
};

export default ProfilePage;