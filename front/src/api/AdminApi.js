import apiClient from '../config/apiClient';

const AdminApi = {
    getRoomPopularityStats: async () => {
        try {
            const response = await apiClient.get('/api/admin/statistics/rooms');
            return response.data;
        } catch (error) {
            console.error('Ошибка загрузки статистики комнат:', error);
            throw error;
        }
    },

    getServicePopularityStats: async () => {
        try {
            const response = await apiClient.get('/api/admin/statistics/services');
            return response.data;
        } catch (error) {
            console.error('Ошибка загрузки статистики услуг:', error);
            throw error;
        }
    },
};

export default AdminApi;
