import React, { useState, useEffect } from 'react';
import useUserStore from '../store/UserStore';
import { useNavigate } from 'react-router-dom';
import apiClient from "../config/apiClient.js";
import '../styles/ProfilePage.css';
import BookingsApi from '../config/BookingsApi';
import UserApi from '../api/UserAPI';
import Cookies from "js-cookie";
import ProfilePhoto from "../components/ProfilePhoto.jsx";

const ProfilePage = () => {
  const { user, setUser, logout } = useUserStore();
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [isEditing, setIsEditing] = useState(false);
  const [bookings, setBookings] = useState([]);
  const [photo, setPhoto] = useState(null);

  const handleLogout = async () => {
    try {
      await logout();
      if (!useUserStore.getState().isAuth) {
        navigate('/login');
      }
    } catch (error) {
      console.error('Ошибка при выходе:', error);
      alert('Не удалось выйти. Попробуйте ещё раз.');
    }
  };

  useEffect(() => {
    if (user) {
      if (user.role === 'ROLE_ADMIN') {
        navigate('/admin');
      } else if (user.role === 'ROLE_HOSTES') {
        navigate('/hostes/rentals');
      }
    }

    const fetchBookings = async () => {
      try {
        const data = await BookingsApi.fetchUserBookings(user.id);
        setBookings(data);
      } catch (error) {
        console.error('Ошибка загрузки броней:', error);
      }
    };
    if (user) {
      fetchBookings();
    }
  }, [user, navigate]);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async () => {
    try {
      const response = await apiClient.put(`/api/users/profile/${user.id}`, {
        firstName,
        lastName,
      }, { withCredentials: true });
      setUser(response.data);
      console.log('Профиль успешно обновлен:', response.data);
      setIsEditing(false);
    } catch (error) {
      console.error('Ошибка при обновлении профиля:', error);
    }
  };

  const handleFileChange = (event) => {
    setPhoto(event.target.files[0]);
  };

  const handleUploadPhoto = async () => {
    if (!photo) {
      alert('Пожалуйста, выберите фото!');
      return;
    }

    const formData = new FormData();
    formData.append('photo', photo);

    try {
      console.log("Отправка фото для пользователя ID:", user.id);
      console.log("Размер файла:", photo.size, "байт");

      const response = await UserApi.uploadPhoto(user.id, formData);
      console.log("Успешный ответ:", response);

      setUser(response);
      Cookies.set('user', JSON.stringify(response), { expires: 7 });
      alert('Фото успешно загружено!');
    } catch (error) {
      console.error('Ошибка при загрузке фото:', error);
      const message = error.response?.data || 'Неизвестная ошибка';
      alert('Ошибка при загрузке фото: ' + message);
    }
  };

  const translateStatus = (status) => {
    switch (status) {
      case 'PENDING':
        return 'В ОЖИДАНИИ';
      case 'CONFIRMED':
        return 'ПОДТВЕРЖДЕННЫЙ';
      case 'REJECTED':
        return 'ОТКЛОНЕННЫЙ';
      default:
        return 'Неизвестный статус';
    }
  };

  if (!user) {
    return <div>Загрузка...</div>;
  }

  return (
      <div className="profile-page">
        <h2>Профиль пользователя</h2>

        <div className="profile-container">
          <div className="profile-header">
            <div className="profile-photo">
              <ProfilePhoto photoPath={user.photoPath} />
            </div>
            <div className="profile-info">
              <h3>{`${firstName} ${lastName}`}</h3>
              <p>Дата регистрации: {user.registrationDate}</p>
              <p>Email: {user.email}</p>
            </div>
            <div className="profile-actions">
              {isEditing ? (
                  <button onClick={handleSave}>Сохранить</button>
              ) : (
                  <button onClick={handleEdit}>Редактировать</button>
              )}
            </div>
          </div>

          {isEditing && (
              <div className="edit-section">
                <label>Имя:</label>
                <input
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                />
                <label>Фамилия:</label>
                <input
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                />
                <input type="file" accept="image/*" onChange={handleFileChange} />
                <button onClick={handleUploadPhoto}>Загрузить фото</button>
              </div>
          )}
        </div>

        <div className="bookings-container">
          <h3>Ваши бронирования и услуги</h3>
          <ul>
            {bookings.map((booking) => (
                <li key={booking.bookingId} className="booking-item">
                  {booking.room ? (
                      <p>Комната: {booking.room.roomTitle}</p>
                  ) : (
                      <p>Услуги:</p>
                  )}
                  {booking.services && booking.services.length > 0 && (
                      <ul>
                        {booking.services.map(service => (
                            <li key={service.serviceId}>{service.serviceName} - {service.servicePrice} руб.</li>
                        ))}
                      </ul>
                  )}
                  <p>Дата заезда: {booking.checkInDate}</p>
                  <p>Дата выезда: {booking.checkOutDate}</p>
                  <p>Статус: {translateStatus(booking.status)}</p>
                  <p>Общая сумма: {booking.totalSum} руб.</p>
                </li>
            ))}
          </ul>
        </div>

        <button onClick={handleLogout} className="logout-button">
          Выйти
        </button>
      </div>
  );
};

export default ProfilePage;