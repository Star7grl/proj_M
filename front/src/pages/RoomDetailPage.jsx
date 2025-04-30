import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import RoomsApi from '../config/RoomsApi';
import useUserStore from '../store/UserStore';
import DatePicker from 'react-datepicker';
import "../styles/RoomDetailPage.css";
import 'react-datepicker/dist/react-datepicker.css';
import apiClient from '../config/apiClient';
import hotelBackground from "../assets/images/номера.png";

const RoomDetailPage = () => {
  const { id } = useParams();
  const [room, setRoom] = useState(null);
  const [activeImage, setActiveImage] = useState(null);
  const [checkInDate, setCheckInDate] = useState(null);
  const [checkOutDate, setCheckOutDate] = useState(null);
  const { user } = useUserStore();
  const navigate = useNavigate();
  const [bookedDates, setBookedDates] = useState([]);
  const [pastDates, setPastDates] = useState([]);

  useEffect(() => {
    const fetchRoom = async () => {
      try {
        const data = await RoomsApi.getRoomById(id);
        setRoom(data);
        if (data.images && data.images.length > 0) {
          setActiveImage(data.images[0].imageUrl);
        }
      } catch (error) {
        console.error('Ошибка загрузки комнаты:', error);
      }
    };

    const fetchBookedDates = async () => {
      try {
        const response = await apiClient.get(`/api/bookings/${id}/booked-dates`);
        setBookedDates(response.data.booked.map(date => new Date(date)));
        setPastDates(response.data.past.map(date => new Date(date)));
      } catch (error) {
        console.error('Ошибка загрузки занятых дат:', error);
      }
    };

    fetchRoom();
    fetchBookedDates();
  }, [id]);

  const isDateBooked = (date) => {
    return bookedDates.some(bookedDate => bookedDate.toDateString() === date.toDateString());
  };

  const isDatePast = (date) => {
    return pastDates.some(pastDate => pastDate.toDateString() === date.toDateString());
  };

  const handleBookRoom = () => {
    if (!checkInDate || !checkOutDate) {
      alert('Пожалуйста, выберите даты заезда и выезда.');
      return;
    }

    if (user && user.role === 'ROLE_HOSTES') {
      navigate(`/hostes/rentals/new?roomId=${room.roomId}&checkInDate=${checkInDate.toISOString()}&checkOutDate=${checkOutDate.toISOString()}`);
    } else {
      const days = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
      const totalPrice = days * room.price;

      navigate('/payment', {
        state: {
          roomTitle: room.roomTitle,
          checkInDate: checkInDate.toISOString().split('T')[0],
          checkOutDate: checkOutDate.toISOString().split('T')[0],
          totalPrice: totalPrice,
          roomId: room.roomId,
          userId: user.id,
        },
      });
    }
  };

  if (!room) return <div>Загрузка...</div>;

  return (
      <div className="room-detail-bg">
        <div className="services-background">
          <img src={hotelBackground} alt="Фон отеля" />
          <div className="background-overlay"></div>
        </div>
        <div className="room-detail">
          <div className="room-slider">
            <div className="active-image">
              <img src={activeImage || '/img/room_default.jpg'} alt="Активное изображение" />
            </div>
            <div className="thumbnails">
              {room.images && room.images.length > 0 && room.images.map((image, index) => (
                  <img
                      key={index}
                      src={image.imageUrl}
                      alt={`Миниатюра ${index + 1}`}
                      onClick={() => setActiveImage(image.imageUrl)}
                      className={activeImage === image.imageUrl ? 'active' : ''}
                  />
              ))}
            </div>
          </div>
          <div className="room-info">
            <h2>{room.roomTitle}</h2>
            <p>{room.description}</p>
            <h3>Цена: {room.price} руб.</h3>
            <div className="date-picker">
              <label>Дата заезда:</label>
              <DatePicker
                  selected={checkInDate}
                  onChange={(date) => setCheckInDate(date)}
                  dateFormat="dd/MM/yyyy"
                  placeholderText="Выберите дату заезда"
                  minDate={new Date()} // Запрещаем выбор прошедших дат
                  excludeDates={bookedDates}
                  dayClassName={(date) => {
                    if (isDatePast(date)) return 'past-date';
                    if (isDateBooked(date)) return 'booked-date';
                    return null;
                  }}
              />
            </div>
            <div className="date-picker">
              <label>Дата выезда:</label>
              <DatePicker
                  selected={checkOutDate}
                  onChange={(date) => setCheckOutDate(date)}
                  dateFormat="dd/MM/yyyy"
                  placeholderText="Выберите дату выезда"
                  minDate={checkInDate || new Date()} // Минимальная дата выезда — дата заезда или сегодня
                  excludeDates={bookedDates}
                  dayClassName={(date) => {
                    if (isDatePast(date)) return 'past-date';
                    if (isDateBooked(date)) return 'booked-date';
                    return null;
                  }}
              />
            </div>
            {room.status === 'AVAILABLE' ? (
                <button onClick={handleBookRoom} className='book-button'>
                  Забронировать
                </button>
            ) : (
                <p>Комната забронирована</p>
            )}
          </div>
        </div>
      </div>
  );
};

export default RoomDetailPage;