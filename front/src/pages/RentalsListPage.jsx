import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import "../styles/Admin.css";

const RentalsListPage = () => {
    const [rentals, setRentals] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingRental, setEditingRental] = useState(null);

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

    const handleEdit = (rental) => {
        setEditingRental(rental);
    };

    const handleUpdate = async (updatedRental) => {
        try {
            const response = await apiClient.put(`/api/rentals/${updatedRental.id}`, updatedRental);
            setRentals(rentals.map(r => r.id === response.data.id ? response.data : r));
            setEditingRental(null);
        } catch (error) {
            console.error('Ошибка обновления аренды:', error);
            setError('Не удалось обновить аренду.');
        }
    };

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
                            <button onClick={() => handleEdit(rental)}>Редактировать</button>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Нет доступных аренд.</p>
            )}
            {editingRental && (
                <RentalEditForm rental={editingRental} onUpdate={handleUpdate} onCancel={() => setEditingRental(null)} />
            )}
        </div>
    );
};

const RentalEditForm = ({ rental, onUpdate, onCancel }) => {
    const [formData, setFormData] = useState(rental);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onUpdate(formData);
    };

    return (
        <form onSubmit={handleSubmit}>
            <input
                name="visitorFirstName"
                value={formData.visitorFirstName}
                onChange={handleChange}
                placeholder="Имя"
            />
            <input
                name="visitorLastName"
                value={formData.visitorLastName}
                onChange={handleChange}
                placeholder="Фамилия"
            />
            <input
                name="visitorPhone"
                value={formData.visitorPhone}
                onChange={handleChange}
                placeholder="Телефон"
            />
            <input
                name="checkInDate"
                type="date"
                value={formData.checkInDate}
                onChange={handleChange}
            />
            <input
                name="checkOutDate"
                type="date"
                value={formData.checkOutDate}
                onChange={handleChange}
            />
            <button type="submit">Сохранить</button>
            <button type="button" onClick={onCancel}>Отмена</button>
        </form>
    );
};

export default RentalsListPage;