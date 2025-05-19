import apiClient from './apiClient';

const BookingsApi = {
  createBooking: async (bookingData) => {
    try {
      const response = await apiClient.post('/api/bookings/add', bookingData);
      return response.data;
    } catch (error) {
      if (error.response && error.response.data && typeof error.response.data === 'string') {
        throw new Error(error.response.data);
      } else if (error.response && error.response.data && error.response.data.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Ошибка при бронировании');
    }
  },
  fetchUserBookings: async (userId) => {
    try {
      const response = await apiClient.get(`/api/bookings/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Ошибка получения бронирований пользователя:', error.response || error);
      if (error.response && error.response.status === 403) {
        throw new Error("У вас нет прав для просмотра этих бронирований.");
      }
      throw new Error('Не удалось загрузить бронирования.');
    }
  },
  // НОВАЯ ФУНКЦИЯ
  cancelUserBooking: async (bookingId) => {
    try {
      const response = await apiClient.delete(`/api/bookings/user/delete/${bookingId}`);
      return response.data; // Ожидаем { message: "Бронирование успешно отменено" } или пустой ответ при успехе
    } catch (error) {
      console.error('Ошибка отмены бронирования на клиенте:', error.response || error);
      if (error.response && error.response.data && error.response.data.error) {
        throw new Error(error.response.data.error);
      } else if (error.response && error.response.data && typeof error.response.data === 'string') {
        throw new Error(error.response.data);
      }
      throw new Error('Не удалось отменить бронирование. Пожалуйста, попробуйте еще раз.');
    }
  },
};

export default BookingsApi;
