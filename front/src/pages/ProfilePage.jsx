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
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [bookings, setBookings] = useState([]);
  const [photo, setPhoto] = useState(null);
  const [loadingBookings, setLoadingBookings] = useState(true); // Состояние загрузки для бронирований

  // Инициализация firstName и lastName при загрузке пользователя
  useEffect(() => {
    if (user) {
      setFirstName(user.firstName || '');
      setLastName(user.lastName || '');
    }
  }, [user]);


  const handleLogout = async () => {
    try {
      await logout();
      // Состояние isAuth в UserStore обновится автоматически,
      // ProtectedRoute или другие компоненты должны среагировать на это.
      // Принудительная навигация здесь может быть излишней, если ProtectedRoute работает корректно.
      navigate('/login');
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
      if (user && user.id) { // Убедимся, что user и user.id существуют
        setLoadingBookings(true);
        try {
          const data = await BookingsApi.fetchUserBookings(user.id);
          setBookings(data || []); // Устанавливаем пустой массив, если data undefined
        } catch (error) {
          console.error('Ошибка загрузки броней:', error);
          setBookings([]); // Устанавливаем пустой массив в случае ошибки
          // Можно добавить уведомление для пользователя
          // alert(`Не удалось загрузить ваши бронирования: ${error.message}`);
        } finally {
          setLoadingBookings(false);
        }
      } else {
        setLoadingBookings(false); // Если пользователя нет, загрузку завершаем
        setBookings([]);
      }
    };
    fetchBookings();
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
      setUser(response.data); // Обновляем пользователя в сторе
      Cookies.set('user', JSON.stringify(response.data), { expires: 7 }); // Обновляем куки
      console.log('Профиль успешно обновлен:', response.data);
      setIsEditing(false);
    } catch (error) {
      console.error('Ошибка при обновлении профиля:', error);
      alert('Ошибка при обновлении профиля: ' + (error.response?.data?.message || error.message));
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
    if (!user || !user.id) {
      alert('Пользователь не определен. Пожалуйста, войдите снова.');
      return;
    }

    const formData = new FormData();
    formData.append('photo', photo);

    try {
      const response = await UserApi.uploadPhoto(user.id, formData);
      setUser(response); // Обновляем пользователя в сторе
      Cookies.set('user', JSON.stringify(response), { expires: 7 }); // Обновляем куки
      alert('Фото успешно загружено!');
      setPhoto(null); // Сбрасываем выбранный файл
    } catch (error) {
      console.error('Ошибка при загрузке фото:', error);
      const message = error.response?.data || (error.message || 'Неизвестная ошибка');
      alert('Ошибка при загрузке фото: ' + message);
    }
  };

  const translateStatus = (status) => {
    switch (status) {
      case 'PENDING':
        return 'В ОЖИДАНИИ';
      case 'CONFIRMED':
        return 'ПОДТВЕРЖДЕНО';
      case 'REJECTED':
        return 'ОТКЛОНЕНО';
      default:
        return 'Неизвестный статус';
    }
  };

  // НОВЫЙ ОБРАБОТЧИК
  const handleCancelBooking = async (bookingId) => {
    if (window.confirm('Вы уверены, что хотите отменить это бронирование?')) {
      try {
        await BookingsApi.cancelUserBooking(bookingId);
        setBookings(prevBookings => prevBookings.filter(b => b.bookingId !== bookingId));
        alert('Бронирование успешно отменено.');
      } catch (error) {
        console.error('Ошибка отмены бронирования:', error);
        alert(`Не удалось отменить бронирование: ${error.message}`);
      }
    }
  };

  if (!user) {
    // Можно перенаправить на логин или показать сообщение
    // navigate('/login'); // или
    return <div>Пожалуйста, войдите в систему для просмотра профиля.</div>;
  }


  return (
      <div className="profile-page">
        <h2>Профиль пользователя</h2>

        <div className="profile-container">
          <div className="profile-header">
            <div className="profile-photo-container"> {/* Обертка для фото и загрузки */}
              <ProfilePhoto photoPath={user.photoPath} />
              {isEditing && ( /* Показываем загрузку фото только в режиме редактирования */
                  <div className="photo-upload-section">
                    <input type="file" accept="image/*" onChange={handleFileChange} id="photoUploadInput" style={{display: 'none'}} />
                    <label htmlFor="photoUploadInput" className="upload-photo-label-button">
                      Выбрать фото
                    </label>
                    {photo && <button onClick={handleUploadPhoto} className="upload-photo-button">Загрузить</button>}
                  </div>
              )}
            </div>
            <div className="profile-info">
              <h3>{isEditing ? (
                  <>
                    <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)} placeholder="Имя" className="profile-edit-input" />
                    <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)} placeholder="Фамилия" className="profile-edit-input" />
                  </>
              ) : (
                  `${user.firstName || 'Имя не указано'} ${user.lastName || 'Фамилия не указана'}`
              )}
              </h3>
              <p>Логин: {user.username}</p>
              <p>Email: {user.email}</p>
            </div>
            <div className="profile-actions">
              {isEditing ? (
                  <button onClick={handleSave} className="profile-button save">Сохранить</button>
              ) : (
                  <button onClick={handleEdit} className="profile-button edit">Редактировать</button>
              )}
            </div>
          </div>
        </div>

        <div className="bookings-container">
          <h3>Ваши бронирования и услуги</h3>
          {loadingBookings ? (
              <p>Загрузка бронирований...</p>
          ) : bookings.length > 0 ? (
              <ul>
                {bookings.map((booking) => (
                    <li key={booking.bookingId} className="booking-item">
                      {booking.room ? (
                          <p><strong>Комната:</strong> {booking.room.roomTitle}</p>
                      ) : (
                          <p><strong>Только услуги:</strong></p>
                      )}
                      {booking.services && booking.services.length > 0 && (
                          <div>
                            <strong>Услуги:</strong>
                            <ul>
                              {booking.services.map(service => (
                                  <li key={service.serviceId}>{service.serviceName} - {service.servicePrice} руб.</li>
                              ))}
                            </ul>
                          </div>
                      )}
                      {booking.room && ( // Показываем даты только если есть комната
                          <>
                            <p><strong>Дата заезда:</strong> {booking.checkInDate}</p>
                            <p><strong>Дата выезда:</strong> {booking.checkOutDate}</p>
                          </>
                      )}
                      <p><strong>Статус:</strong> {translateStatus(booking.status)}</p>
                      <p><strong>Общая сумма:</strong> {booking.totalSum} руб.</p>

                      {/* КНОПКА ОТМЕНЫ */}
                      {(booking.status === 'PENDING' || booking.status === 'CONFIRMED') && (
                          <button
                              onClick={() => handleCancelBooking(booking.bookingId)}
                              className="cancel-booking-button"
                          >
                            Отменить бронирование
                          </button>
                      )}
                    </li>
                ))}
              </ul>
          ) : (
              <p>У вас пока нет бронирований.</p>
          )}
        </div>

        <button onClick={handleLogout} className="logout-button">
          Выйти
        </button>
      </div>
  );
};

export default ProfilePage;
