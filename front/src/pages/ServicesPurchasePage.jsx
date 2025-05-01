import React, { useState, useEffect } from 'react';
import ServicesApi from '../config/servicesApi';
import BookingsApi from '../config/BookingsApi';
import useUserStore from '../store/UserStore';
import { useNavigate } from 'react-router-dom';
import '../styles/ServicesPurchase.css';

const ServicesPurchasePage = () => {
    const [services, setServices] = useState([]);
    const [selectedServices, setSelectedServices] = useState([]);
    const { user } = useUserStore();
    const navigate = useNavigate();

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

    const handleSelectService = (serviceId) => {
        if (selectedServices.includes(serviceId)) {
            setSelectedServices(selectedServices.filter(id => id !== serviceId));
        } else {
            setSelectedServices([...selectedServices, serviceId]);
        }
    };

    const handlePurchase = async () => {
        if (!user) {
            alert('Пожалуйста, войдите в систему для приобретения услуг.');
            navigate('/login');
            return;
        }
        if (selectedServices.length === 0) {
            alert('Пожалуйста, выберите хотя бы одну услугу.');
            return;
        }
        try {
            const bookingData = {
                userId: user.id,
                roomId: null,
                checkInDate: new Date().toISOString().split('T')[0],
                checkOutDate: new Date().toISOString().split('T')[0],
                serviceIds: selectedServices,
            };
            await BookingsApi.createBooking(bookingData);
            alert('Услуги успешно приобретены!');
            navigate('/profile');
        } catch (error) {
            console.error('Ошибка при приобретении услуг:', error);
            alert('Не удалось приобрести услуги. Попробуйте ещё раз.');
        }
    };

    return (
        <div className="services-purchase-page">
            <h2>Приобретение услуг</h2>
            <div className="services-list">
                {services.map(service => (
                    <div key={service.serviceId} className="service-item">
                        <input
                            type="checkbox"
                            checked={selectedServices.includes(service.serviceId)}
                            onChange={() => handleSelectService(service.serviceId)}
                        />
                        <span>{service.serviceName} - {service.servicePrice} руб.</span>
                    </div>
                ))}
            </div>
            <button onClick={handlePurchase}>Приобрести</button>
        </div>
    );
};

export default ServicesPurchasePage;