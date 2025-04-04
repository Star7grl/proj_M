import React, { useEffect, useState } from "react";
import "../styles/Admin.css";
import ServicesApi from "../config/servicesApi";

const ServicesManagementPage = () => {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({ serviceName: "", servicePrice: "" });
    const [editingService, setEditingService] = useState(null);

    useEffect(() => {
        const fetchServices = async () => {
            try {
                const data = await ServicesApi.getAllServices();
                setServices(data);
            } catch (error) {
                console.error("Ошибка загрузки услуг:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchServices();
    }, []);
    

    const handleDelete = async (id) => {
        try {
            await ServicesApi.deleteService(id);
            setServices(services.filter((service) => service.serviceId !== id));
        } catch (error) {
            console.error("Ошибка удаления услуги:", error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const newService = await ServicesApi.createService({
                serviceName: formData.serviceName,
                servicePrice: parseFloat(formData.servicePrice),
            });
            setServices([...services, newService]);
            setFormData({ serviceName: "", servicePrice: "" });
        } catch (error) {
            console.error("Ошибка создания услуги:", error);
        }
    };

    const handleEditInputChange = (e) => {
        const { name, value } = e.target;
        setEditingService({ ...editingService, [name]: value });
    };

    const handleEditSubmit = async (e) => {
        e.preventDefault();
        try {
            const updatedService = await ServicesApi.updateService(editingService.serviceId, {
                serviceName: editingService.serviceName,
                servicePrice: parseFloat(editingService.servicePrice),
            });
            setServices(services.map((s) => (s.serviceId === updatedService.serviceId ? updatedService : s)));
            setEditingService(null);
        } catch (error) {
            console.error("Ошибка обновления услуги:", error);
        }
    };

    if (loading) return <div>Загрузка...</div>;

    return (
        <div className="services-management-page">
            <h2>Управление услугами</h2>

            <h3>Добавить услугу</h3>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="serviceName"
                    value={formData.serviceName}
                    onChange={handleInputChange}
                    placeholder="Название услуги"
                    required
                />
                <input
                    type="number"
                    name="servicePrice"
                    value={formData.servicePrice}
                    onChange={handleInputChange}
                    placeholder="Цена"
                    step="0.01"
                    required
                />
                <button type="submit">Добавить услугу</button>
            </form>

            {editingService && (
                <>
                    <h3>Редактировать услугу</h3>
                    <form onSubmit={handleEditSubmit}>
                        <input
                            type="text"
                            name="serviceName"
                            value={editingService.serviceName}
                            onChange={handleEditInputChange}
                            placeholder="Название услуги"
                            required
                        />
                        <input
                            type="number"
                            name="servicePrice"
                            value={editingService.servicePrice}
                            onChange={handleEditInputChange}
                            placeholder="Цена"
                            step="0.01"
                            required
                        />
                        <button type="submit">Сохранить изменения</button>
                        <button type="button" onClick={() => setEditingService(null)}>Отмена</button>
                    </form>
                </>
            )}

            <h3>Список услуг</h3>
            <table>
                <thead>
                    <tr className="thStyle">
                        <th>ID</th>
                        <th>Название</th>
                        <th>Цена</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    {services.map((service) => (
                        <tr className="thStyle" key={service.serviceId}>
                            <td>{service.serviceId}</td>
                            <td>{service.serviceName}</td>
                            <td>{service.servicePrice}</td>
                            <td>
                                <button onClick={() => setEditingService(service)}>Редактировать</button>
                                <button onClick={() => handleDelete(service.serviceId)}>Удалить</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ServicesManagementPage;