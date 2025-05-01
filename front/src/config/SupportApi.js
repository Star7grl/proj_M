import apiClient from './apiClient';

const SupportApi = {
    sendMessage: async (messageData) => {
        try {
            const response = await apiClient.post('/api/support/send', messageData);
            return response.data;
        } catch (error) {
            throw error;
        }
    },
    getAllMessages: async () => {
        try {
            const response = await apiClient.get('/api/support/messages');
            return response.data;
        } catch (error) {
            throw error;
        }
    },
};

export default SupportApi;