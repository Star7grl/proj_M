import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import '../styles/BookingTable.css';

const BookingTable = () => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Загружаем бронирования
                const bookingsResponse = await apiClient.get('/api/bookings');
                // Загружаем аренды
                const rentalsResponse = await apiClient.get('/api/rentals/all');

                // Преобразуем бронирования в общий формат
                const bookings = bookingsResponse.data.map(booking => ({
                    id: booking.bookingId,
                    type: 'booking',
                    userOrVisitor: booking.user.username,
                    room: booking.room.roomTitle,
                    checkInDate: booking.checkInDate,
                    checkOutDate: booking.checkOutDate,
                    status: booking.status
                }));

                // Преобразуем аренды в общий формат
                const rentals = rentalsResponse.data.map(rental => ({
                    id: rental.id,
                    type: 'rental',
                    userOrVisitor: `${rental.visitorFirstName} ${rental.visitorLastName}`,
                    room: rental.room.roomTitle,
                    checkInDate: rental.checkInDate,
                    checkOutDate: rental.checkOutDate,
                    status: 'N/A' // У аренд нет статуса
                }));

                // Объединяем списки
                setItems([...bookings, ...rentals]);
            } catch (error) {
                console.error('Ошибка загрузки данных:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleStatusChange = async (id, newStatus) => {
        const item = items.find(i => i.id === id);
        if (item.type !== 'booking') {
            alert('Нельзя изменить статус для аренды');
            return;
        }
        try {
            await apiClient.put(`/api/bookings/updateStatus/${id}`, null, {
                params: { status: newStatus }
            });
            setItems(items.map(i => i.id === id ? { ...i, status: newStatus } : i));
        } catch (error) {
            console.error('Ошибка обновления статуса:', error);
            alert('Не удалось обновить статус');
        }
    };

    const handleDeleteBooking = async (id) => {
        if (window.confirm('Вы уверены, что хотите удалить это бронирование?')) {
            try {
                await apiClient.delete(`/api/bookings/delete/${id}`);
                setItems(items.filter(i => i.id !== id));
            } catch (error) {
                console.error('Ошибка удаления бронирования:', error);
                alert('Не удалось удалить бронирование');
            }
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
                    <th>Тип</th>
                    <th>Пользователь/Посетитель</th>
                    <th>Комната</th>
                    <th>Дата заезда</th>
                    <th>Дата выезда</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {items.map(item => (
                    <tr key={item.id}>
                        <td>{item.id}</td>
                        <td>{item.type === 'booking' ? 'Бронирование' : 'Аренда'}</td>
                        <td>{item.userOrVisitor}</td>
                        <td>{item.room}</td>
                        <td>{item.checkInDate}</td>
                        <td>{item.checkOutDate}</td>
                        <td>{item.type === 'booking' ? item.status : 'N/A'}</td>
                        <td>
                            {item.type === 'booking' ? (
                                <>
                                    <select
                                        value={item.status}
                                        onChange={(e) => handleStatusChange(item.id, e.target.value)}
                                    >
                                        <option value="PENDING">В ОЖИДАНИИ</option>
                                        <option value="CONFIRMED">ПОДТВЕРЖДЕННЫЙ</option>
                                        <option value="REJECTED">ОТКЛОНЕННЫЙ</option>
                                    </select>
                                    <button onClick={() => handleDeleteBooking(item.id)}>Удалить</button>
                                </>
                            ) : (
                                'Нет действий'
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default BookingTable;