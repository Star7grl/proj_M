import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import BookingsApi from '../config/BookingsApi';
import '../styles/Payment.css';
import hotelBackground from "../assets/images/номера.png";

const PaymentPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { roomTitle, checkInDate, checkOutDate, totalPrice, roomId, userId, serviceIds } = location.state || {};

  const [cardNumber, setCardNumber] = useState('');
  const [expiryDate, setExpiryDate] = useState('');
  const [cvc, setCvc] = useState('');
  const [error, setError] = useState(null);
  const [expiryError, setExpiryError] = useState(null);

  const handlePayment = async () => {
    setError(null);

    if (!validateCardNumber(cardNumber)) {
      setError('Некорректный номер карты (должен быть 16 цифр)');
      return;
    }
    if (!validateExpiryDate(expiryDate)) {
      setExpiryError('Некорректный срок действия карты');
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
        serviceIds,
      });
      alert('Бронирование успешно');
      navigate('/profile');
    } catch (error) {
      console.error('Ошибка при бронировании:', error.message);
      setError(error.message);
      alert(error.message);
    }
  };

  //валидация номера карты
  const validateCardNumber = (number) => /^\d{16}$/.test(number);

  //валидация даты
  const validateExpiryDate = (date) => {
    if (!/^\d{2}\/\d{2}$/.test(date)) return false;

    const [monthStr, yearStr] = date.split('/');
    const month = parseInt(monthStr, 10);
    const year = parseInt(yearStr, 10);

    if (month < 1 || month > 12) return false;

    const now = new Date();
    const currentYear = now.getFullYear() % 100;
    const currentMonth = now.getMonth() + 1;

    if (year < currentYear || year > currentYear + 10) return false;

    if (year === currentYear && month < currentMonth) return false;

    return true;
  };

  const validateCvc = (cvc) => /^\d{3}$/.test(cvc);

  const handleExpiryDateChange = (value) => {
    // Оставляем только цифры и "/"
    let cleaned = value.replace(/[^\d\/]/g, '');
    // Автоматически добавляем "/" после 2 цифр месяца, если его нет
    if (cleaned.length === 2 && !cleaned.includes('/')) {
      cleaned += '/';
    }

    // Ограничиваем длину до 5 символов (MM/YY)
    if (cleaned.length > 5) {
      cleaned = cleaned.slice(0, 5);
    }

    setExpiryDate(cleaned);

    // Валидация месяца
    const [month = '', year = ''] = cleaned.split('/');
    if (month.length === 2) {
      const monthNum = parseInt(month, 10);
      if (isNaN(monthNum) || monthNum < 1 || monthNum > 12) {
        setExpiryError('Некорректный месяц');
        return;
      } else {
        setExpiryError(null);
      }
    } else {
      setExpiryError(null);
    }

    // Если дата полностью введена, проверяем срок действия
    if (month.length === 2 && year.length === 2) {
      if (!validateExpiryDate(cleaned)) {
        setExpiryError('Срок действия карты в прошлом или слишком далек');
      } else {
        setExpiryError(null);
      }
    }
  };

  if (!location.state) {
    return <div>Ошибка: данные бронирования не найдены</div>;
  }

  return (
    <div className="payment-page-wrapper">
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
        <form className="payment-form" onSubmit={(e) => e.preventDefault()}>
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
              onChange={(e) => handleExpiryDateChange(e.target.value)}
              maxLength={5}
              placeholder="MM/YY"
            />
          </label>
          {expiryError && <p className="error-message">{expiryError}</p>}
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
          <button
            type="button"
            onClick={handlePayment}
            className="pay-button"
            disabled={!!expiryError}
          >
            Оплатить
          </button>
        </form>
      </div>
    </div>
  );
};

export default PaymentPage;
