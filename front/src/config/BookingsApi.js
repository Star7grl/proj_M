import apiClient from './apiClient';

const BookingsApi = {
  createBooking: async (bookingData) => {
    try {
      const response = await apiClient.post('/api/bookings/add', bookingData);
      return response.data;
    } catch (error) {
      if (error.response && error.response.data) {
        throw new Error(error.response.data); // Используем сообщение от сервера
      } else {
        throw new Error('Ошибка при бронировании');
      }
    }
  },
  fetchUserBookings: async (userId) => {
    try {
      const response = await apiClient.get(`/api/bookings/user/${userId}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};

export default BookingsApi;