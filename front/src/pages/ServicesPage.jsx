import React, { useEffect, useState } from "react";
import ServicesApi from "../config/servicesApi";
import "../styles/Services.css";

const ServicesPage = () => {
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);

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

    if (loading) return <div>Загрузка...</div>;

    return (
        <div className="services-page">
            <h2>Список услуг</h2>
            <table>
                <thead>
                    <tr className="thStyle">
                        <th>ID</th>
                        <th>Название</th>
                        <th>Цена</th>
                    </tr>
                </thead>
                <tbody>
                    {services.map((service) => (
                        <tr className="thStyle" key={service.serviceId}>
                            <td>{service.serviceId}</td>
                            <td>{service.serviceName}</td>
                            <td>{service.servicePrice}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ServicesPage;