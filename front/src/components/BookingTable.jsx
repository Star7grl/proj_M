import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import '../styles/BookingTable.css';

const BookingTable = () => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const bookingsResponse = await apiClient.get('/api/bookings');
                const rentalsResponse = await apiClient.get('/api/rentals/all');

                const bookings = bookingsResponse.data.map(booking => ({
                    id: booking.bookingId,
                    type: 'booking',
                    userOrVisitor: booking.user.username,
                    room: booking.room ? booking.room.roomTitle : 'N/A',
                    services: booking.services,
                    checkInDate: booking.checkInDate,
                    checkOutDate: booking.checkOutDate,
                    status: booking.status
                }));

                const rentals = rentalsResponse.data.map(rental => ({
                    id: rental.id,
                    type: 'rental',
                    userOrVisitor: `${rental.visitorFirstName} ${rental.visitorLastName}`,
                    room: rental.room ? rental.room.roomTitle : 'N/A',
                    services: rental.services,
                    checkInDate: rental.checkInDate,
                    checkOutDate: rental.checkOutDate,
                    status: 'N/A'
                }));

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
            <table className="booking-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Тип</th>
                    <th>Пользователь/Посетитель</th>
                    <th>Комната</th>
                    <th>Услуги</th>
                    <th>Дата заезда</th>
                    <th>Дата выезда</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
  {items.map(item => (
    <tr key={item.id}>
      <td data-label="ID">{item.id}</td>
      <td data-label="Тип">{item.type === 'booking' ? 'Бронирование' : 'Аренда'}</td>
      <td data-label="Пользователь/Посетитель">{item.userOrVisitor}</td>
      <td data-label="Комната">{item.room}</td>
      <td data-label="Услуги">
        {item.services && item.services.length > 0 ? (
          <ul>
            {item.services.map(service => (
              <li key={service.serviceId}>{service.serviceName}</li>
            ))}
          </ul>
        ) : 'Нет услуг'}
      </td>
      <td data-label="Дата заезда">{item.checkInDate}</td>
      <td data-label="Дата выезда">{item.checkOutDate}</td>
      <td data-label="Статус">{item.type === 'booking' ? item.status : 'N/A'}</td>
      <td data-label="Действия">
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