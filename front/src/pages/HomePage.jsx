import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import RoomsApi from '../config/RoomsApi';
import { Link } from 'react-router-dom';
import '../styles/Home.css';
import hotelBackground from "../assets/images/номера.png";

// Вспомогательная функция для форматирования даты в YYYY-MM-DD в локальном времени
const formatDateToYYYYMMDD = (date) => {
    if (!date) return '';
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // getMonth() is 0-indexed
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const Home = () => {
    const [checkInDate, setCheckInDate] = useState(null);
    const [checkOutDate, setCheckOutDate] = useState(null);
    const [availableRooms, setAvailableRooms] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleSearch = async () => {
        if (!checkInDate || !checkOutDate) {
            alert('Пожалуйста, выберите даты заезда и выезда.');
            return;
        }
        setLoading(true);
        try {
            // Используем новую функцию форматирования
            const formattedCheckInDate = formatDateToYYYYMMDD(checkInDate);
            const formattedCheckOutDate = formatDateToYYYYMMDD(checkOutDate);

            const data = await RoomsApi.getAvailableRooms(
                formattedCheckInDate,
                formattedCheckOutDate
            );
            setAvailableRooms(data);
        } catch (error) {
            console.error('Ошибка загрузки доступных комнат:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="rooms-page-wrapper">
            {/* Фоновое изображение с затемнением */}
            <div className="rooms-background">
                <img src={hotelBackground} alt="Фон отеля" />
                <div className="background-overlay"></div>
            </div>

            <div className="home-page">
                <h1>Добро пожаловать!</h1>
                <div className="date-picker">
                    <label>Дата заезда:</label>
                    <DatePicker
                        selected={checkInDate}
                        onChange={(date) => setCheckInDate(date)}
                        minDate={new Date()} // Запрещаем выбор прошедших дат
                        dateFormat="dd/MM/yyyy"
                        placeholderText="Выберите дату заезда"
                        dayClassName={(date) => {
                            const today = new Date();
                            today.setHours(0, 0, 0, 0);
                            return date < today ? 'past-date' : null;
                        }}
                    />
                </div>
                <div className="date-picker">
                    <label>Дата выезда:</label>
                    <DatePicker
                        selected={checkOutDate}
                        onChange={(date) => setCheckOutDate(date)}
                        minDate={checkInDate || new Date()} // Минимальная дата выезда — дата заезда или сегодня
                        dateFormat="dd/MM/yyyy"
                        placeholderText="Выберите дату выезда"
                        dayClassName={(date) => {
                            const today = new Date();
                            today.setHours(0, 0, 0, 0);
                            return date < today ? 'past-date' : null;
                        } }
                    />
                </div>
                <button onClick={handleSearch}>Найти комнаты</button>
                {loading ? (
                    <div>Загрузка...</div>
                ) : (
                    <div className="rooms-list">
                        {availableRooms.map((room) => (
                            <Link
                                // Используем новую функцию форматирования для URL
                                to={`/rooms/${room.roomId}?checkIn=${formatDateToYYYYMMDD(checkInDate)}&checkOut=${formatDateToYYYYMMDD(checkOutDate)}`}
                                key={room.roomId}
                                className="room-card"
                            >
                                <img src={room.images[0]?.imageUrl || '/default-image.jpg'} alt={room.roomTitle} className="room-image" />
                                <h3>{room.roomTitle}</h3>
                                <p>{room.description}</p>
                                <p>Цена: {room.price} руб.</p>
                            </Link>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Home;
