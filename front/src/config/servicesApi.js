import apiClient from "../config/apiClient";

export default class ServicesApi {
    static async getAllServices() {
        const response = await apiClient.get("/api/services");
        return response.data;
    }

    static async createService(data) {
        const response = await apiClient.post("/api/services/add", data);
        return response.data;
    }

    static async updateService(id, data) {
        const response = await apiClient.put(`/api/services/update/${id}`, data);
        return response.data;
    }

    static async deleteService(id) {
        await apiClient.delete(`/api/services/delete/${id}`);
    }
}