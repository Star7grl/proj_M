import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import apiClient from "../config/apiClient";
import ServicesApi from '../config/servicesApi';
import "../styles/Admin.css";

const RentalFormPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);
    const roomId = queryParams.get("roomId");
    const checkInDate = queryParams.get("checkInDate");
    const checkOutDate = queryParams.get("checkOutDate");

    const [formData, setFormData] = useState({
        visitorFirstName: "",
        visitorLastName: "",
        visitorPhone: "",
    });
    const [services, setServices] = useState([]);
    const [selectedServices, setSelectedServices] = useState([]);

    useEffect(() => {
        const fetchServices = async () => {
            try {
                const data = await ServicesApi.getAllServices();
                setServices(data.services || []);
            } catch (error) {
                console.error('Ошибка загрузки услуг:', error);
            }
        };
        fetchServices();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSelectService = (serviceId) => {
        if (selectedServices.includes(serviceId)) {
            setSelectedServices(selectedServices.filter(id => id !== serviceId));
        } else {
            setSelectedServices([...selectedServices, serviceId]);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await apiClient.post("/api/rentals", {
                ...formData,
                room: { roomId: parseInt(roomId) },
                checkInDate: checkInDate,
                checkOutDate: checkOutDate,
                serviceIds: selectedServices,
            });
            alert("Аренда успешно создана!");
            navigate("/hostes/rentals");
        } catch (error) {
            console.error("Ошибка создания аренды:", error);
        }
    };

    return (
        <div className="rental">
            <h2>Аренда комнаты</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="visitorFirstName"
                    value={formData.visitorFirstName}
                    onChange={handleInputChange}
                    placeholder="Имя посетителя"
                    required
                />
                <input
                    type="text"
                    name="visitorLastName"
                    value={formData.visitorLastName}
                    onChange={handleInputChange}
                    placeholder="Фамилия посетителя"
                    required
                />
                <input
                    type="text"
                    name="visitorPhone"
                    value={formData.visitorPhone}
                    onChange={handleInputChange}
                    placeholder="Телефон посетителя"
                    required
                />
                <div className="services-selection">
                    <h3>Дополнительные услуги</h3>
                    {services.map(service => (
                        <div key={service.serviceId}>
                            <input
                                type="checkbox"
                                checked={selectedServices.includes(service.serviceId)}
                                onChange={() => handleSelectService(service.serviceId)}
                            />
                            <span>{service.serviceName} - {service.servicePrice} руб.</span>
                        </div>
                    ))}
                </div>
                <button type="submit">Арендовать</button>
            </form>
        </div>
    );
};

export default RentalFormPage;