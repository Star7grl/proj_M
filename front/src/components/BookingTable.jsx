import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import '../styles/BookingTable.css'; // Импортируем стили

const BookingTable = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                const response = await apiClient.get('/api/bookings');
                setBookings(response.data);
            } catch (error) {
                console.error('Ошибка загрузки бронирований:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchBookings();
    }, []);

    const handleStatusChange = async (bookingId, newStatus) => {
        try {
            await apiClient.put(`/api/bookings/updateStatus/${bookingId}`, null, {
                params: { status: newStatus }
            });
            setBookings(bookings.map(booking =>
                booking.bookingId === bookingId ? { ...booking, status: newStatus } : booking
            ));
        } catch (error) {
            console.error('Ошибка обновления статуса:', error);
            alert('Не удалось обновить статус');
        }
    };

    if (loading) return <div className="loading">Загрузка...</div>;

    return (
        <div className="booking-table-container">
            <h2 className="booking-table-header">Управление бронированиями</h2>
            <table className="booking-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Пользователь</th>
                    <th>Комната</th>
                    <th>Дата заезда</th>
                    <th>Дата выезда</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {bookings.map(booking => (
                    <tr key={booking.bookingId}>
                        <td>{booking.bookingId}</td>
                        <td>{booking.user.username}</td>
                        <td>{booking.room.roomTitle}</td>
                        <td>{booking.checkInDate}</td>
                        <td>{booking.checkOutDate}</td>
                        <td>{booking.status}</td>
                        <td>
                            <select
                                value={booking.status}
                                onChange={(e) => handleStatusChange(booking.bookingId, e.target.value)}
                            >
                                <option value="PENDING">В ОЖИДАНИИ</option>
                                <option value="CONFIRMED">ПОДТВЕРЖДЕННЫЙ</option>
                                <option value="REJECTED">ОТКЛОНЕННЫЙ</option>
                            </select>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default BookingTable;