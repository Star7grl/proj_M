import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import "../styles/Admin.css";

const RentalsListPage = () => {
    const [rentals, setRentals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchRentals = async () => {
            try {
                setLoading(true);
                const response = await apiClient.get('/api/rentals');
                setRentals(response.data);
            } catch (error) {
                console.error('Ошибка загрузки аренд:', error);
                setError('Не удалось загрузить список аренд.');
            } finally {
                setLoading(false);
            }
        };
        fetchRentals();
    }, []);

    if (loading) {
        return <div>Загрузка...</div>;
    }

    if (error) {
        return <div>Ошибка: {error}</div>;
    }

    return (
        <div>
            <h2>Список аренд</h2>
            {rentals.length > 0 ? (
                <ul>
                    {rentals.map(rental => (
                        <li key={rental.id}>
                            {rental.visitorFirstName} {rental.visitorLastName} - Комната: {rental.room.roomTitle} -
                            Заезд: {rental.checkInDate} - Выезд: {rental.checkOutDate} - Телефон: {rental.visitorPhone}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Нет доступных аренд.</p>
            )}
        </div>
    );
};

export default RentalsListPage;