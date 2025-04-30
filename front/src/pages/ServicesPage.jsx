import React, { useEffect, useState } from "react";
import ServicesApi from "../config/servicesApi";
import "../styles/Services.css";
import hotelBackground from "../assets/images/номера.png"; // Импортируем фоновое изображение

const ServicesPage = () => {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchServices = async () => {
            try {
                const data = await ServicesApi.getAllServices();
                setServices(data.services || []);
            } catch (error) {
                console.error("Ошибка загрузки услуг:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchServices();
    }, []);

    if (loading) return (
        <div className="loading-animation">
            <div className="hotel-loader"></div>
            <p>Загружаем лучшие услуги для вас...</p>
        </div>
    );

    return (
        <div className="services-page-wrapper">
            {/* Фоновое изображение с затемнением */}
            <div className="services-background">
                <img src={hotelBackground} alt="Фон отеля" />
                <div className="background-overlay"></div>
            </div>

            <div className="services-page">
                <div className="services-header">
                    <h2 className="services-title">Наши эксклюзивные услуги</h2>
                </div>

                <div className="services-timeline">
                    {services.map((service, index) => (
                        <div
                            className={`service-card ${index % 2 === 0 ? 'left' : 'right'}`}
                            key={service.serviceId}
                        >
                            <div className="service-content">
                                {service.imageUrl && (
                                    <img src={service.imageUrl} alt={service.serviceName} />
                                )}
                                <div className="service-details">
                                    <div className="service-icon">
                                        {getServiceIcon(service.serviceName)}
                                    </div>
                                    <h3>{service.serviceName}</h3>
                                    <p className="service-description">
                                        {generateDescription(service.serviceName)}
                                    </p>
                                    <div className="service-price">
                                        {service.servicePrice} ₽
                                    </div>
                                    <button className="service-button">
                                        Подробнее
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

// Генерация иконок в зависимости от типа услуги
const getServiceIcon = (serviceName) => {
    const icons = {
        "SPA": "💆‍♀️",
        "Ресторан": "🍽️",
        "Трансфер": "🚗",
        "Экскурсия": "🗺️",
        "Тренажерный зал": "💪",
        "Бассейн": "🏊"
    };
    return icons[serviceName] || "✨";
};

// Генерация описания
const generateDescription = (serviceName) => {
    const descriptions = {
        "SPA": "Расслабляющие процедуры с использованием натуральных материалов",
        "Ресторан": "Гастрономические шедевры от нашего шеф-повара",
        "Трансфер": "Комфортабельные автомобили с профессиональными водителями",
        "Экскурсия": "Уникальные маршруты по местным достопримечательностям",
        "Фитнес": "Современное оборудование и персональные тренировки",
        "Бассейн": "Огромный бассейн с подогревом и видом на море"
    };
    return descriptions[serviceName] || "Премиум сервис для наших гостей";
};

export default ServicesPage;