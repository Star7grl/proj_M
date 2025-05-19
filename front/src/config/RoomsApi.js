import apiClient from './apiClient';

const RoomsApi = {
  getAllRooms: async (page = 1, size = 9) => {
    try {
      const response = await apiClient.get(`/api/rooms?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  getAllRoomsAdmin: async (page = 1, size = 10) => {
    try {
      const response = await apiClient.get(`/api/rooms/admin?page=${page}&size=${size}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  getRoomById: async (id) => {
    try {
      const response = await apiClient.get(`/api/rooms/${id}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  createRoom: async (roomData) => {
    try {
      const response = await apiClient.post('/api/rooms/add', roomData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  updateRoom: async (id, roomData) => {
    try {
      const response = await apiClient.put(`/api/rooms/update/${id}`, roomData);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  deleteRoom: async (id) => {
    try {
      await apiClient.delete(`/api/rooms/delete/${id}`);
    } catch (error) {
      throw error;
    }
  },
  getAvailableRooms: async (checkInDate, checkOutDate) => {
    try {
      const response = await apiClient.get('/api/rooms/available', {
        params: { checkInDate, checkOutDate }
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  searchRooms: async (roomTitle = '', minPrice = 0, maxPrice = 100000, page = 1, size = 9) => {
    try {
      const params = {
        minPrice,
        maxPrice,
        page,
        size,
      };
      if (roomTitle) {
        params.roomTitle = roomTitle;
      }
      const response = await apiClient.get('/api/rooms/search', { params });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
};

export default RoomsApi;