import apiClient from "../config/apiClient";

const authApi = {
    async post(endpoint, data, config = {}) {
        try {
            const response = await apiClient.post(endpoint, data, {
                withCredentials: true,
                ...config,
            });
            return response;
        } catch (error) {
            throw new Error(
                error.response?.data?.message || error.message || `Ошибка при запросе к ${endpoint}`
            );
        }
    },
    async get(endpoint, config = {}) {
        try {
            const response = await apiClient.get(endpoint, {
                withCredentials: true,
                ...config,
            });
            return response;
        } catch (error) {
            throw new Error(
                error.response?.data?.message || error.message || `Ошибка при запросе к ${endpoint}`
            );
        }
    },
};

export default authApi;