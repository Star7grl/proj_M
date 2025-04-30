// PaymentPage.jsx
import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import BookingsApi from '../config/BookingsApi';
import '../styles/Payment.css';
import hotelBackground from "../assets/images/номера.png";

const PaymentPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { roomTitle, checkInDate, checkOutDate, totalPrice, roomId, userId } = location.state || {};

    const [cardNumber, setCardNumber] = useState('');
    const [expiryDate, setExpiryDate] = useState('');
    const [cvc, setCvc] = useState('');
    const [error, setError] = useState(null);

    const handlePayment = async () => {
        setError(null);

        if (!validateCardNumber(cardNumber)) {
            setError('Некорректный номер карты (должен быть 16 цифр)');
            return;
        }
        if (!validateExpiryDate(expiryDate)) {
            setError('Некорректный срок действия (формат MM/YY)');
            return;
        }
        if (!validateCvc(cvc)) {
            setError('Некорректный CVC (должен быть 3 цифры)');
            return;
        }

        alert('Оплата прошла успешно!');

        try {
            await BookingsApi.createBooking({
                userId,
                roomId,
                checkInDate,
                checkOutDate,
            });
            alert('Бронирование успешно');
            navigate('/profile');
        } catch (error) {
            console.error('Ошибка при бронировании:', error.message);
            setError(error.message); // Отображаем сообщение пользователю
            alert(error.message);
        }
    };

    const validateCardNumber = (number) => /^\d{16}$/.test(number);
    const validateExpiryDate = (date) => {
        const [month, year] = date.split('/');
        const currentYear = new Date().getFullYear() % 100;
        const currentMonth = new Date().getMonth() + 1;
        return (
            /^\d{2}\/\d{2}$/.test(date) &&
            parseInt(month) >= 1 &&
            parseInt(month) <= 12 &&
            (parseInt(year) > currentYear || (parseInt(year) === currentYear && parseInt(month) >= currentMonth))
        );
    };
    const validateCvc = (cvc) => /^\d{3}$/.test(cvc);

    if (!location.state) {
        return <div>Ошибка: данные бронирования не найдены</div>;
    }

    return (
        <div className="payment-page-wrapper">
            {/* Фоновое изображение с затемнением */}
            <div className="services-background">
                <img src={hotelBackground} alt="Фон отеля" />
                <div className="background-overlay"></div>
            </div>
        <div className="payment-page">
            <h2>Оплата бронирования</h2>
            <div className="booking-info">
                <p><strong>Комната:</strong> {roomTitle}</p>
                <p><strong>Дата заезда:</strong> {checkInDate}</p>
                <p><strong>Дата выезда:</strong> {checkOutDate}</p>
                <p><strong>Итоговая цена:</strong> {totalPrice} руб.</p>
            </div>
            <form className="payment-form">
                <label>
                    Номер карты:
                    <input
                        type="text"
                        value={cardNumber}
                        onChange={(e) => setCardNumber(e.target.value.replace(/\D/g, ''))}
                        maxLength={16}
                        placeholder="1234 5678 9012 3456"
                    />
                </label>
                <label>
                    Срок действия (MM/YY):
                    <input
                        type="text"
                        value={expiryDate}
                        onChange={(e) => setExpiryDate(e.target.value)}
                        maxLength={5}
                        placeholder="MM/YY"
                    />
                </label>
                <label>
                    CVC:
                    <input
                        type="text"
                        value={cvc}
                        onChange={(e) => setCvc(e.target.value.replace(/\D/g, ''))}
                        maxLength={3}
                        placeholder="123"
                    />
                </label>
                {error && <p className="error-message">{error}</p>}
                <button type="button" onClick={handlePayment} className='pay-button'>Оплатить</button>
            </form>
        </div>
        </div>
    );
};

export default PaymentPage;
