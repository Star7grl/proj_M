import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import RoomsApi from '../config/RoomsApi';
import ServicesApi from '../config/servicesApi';
import useUserStore from '../store/UserStore';
import DatePicker from 'react-datepicker';
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
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
  const [services, setServices] = useState([]);
  const [selectedServices, setSelectedServices] = useState([]);
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

    const fetchServices = async () => {
      try {
        const data = await ServicesApi.getAllServices();
        setServices(data.services || []);
      } catch (error) {
        console.error('Ошибка загрузки услуг:', error);
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
    fetchServices();
    fetchBookedDates();
  }, [id]);

  const isDateBooked = (date) => {
    return bookedDates.some(bookedDate => bookedDate.toDateString() === date.toDateString());
  };

  const isDatePast = (date) => {
    return pastDates.some(pastDate => pastDate.toDateString() === date.toDateString());
  };

  const handleSelectService = (serviceId) => {
    if (selectedServices.includes(serviceId)) {
      setSelectedServices(selectedServices.filter(id => id !== serviceId));
    } else {
      setSelectedServices([...selectedServices, serviceId]);
    }
  };

  const handleBookRoom = () => {
    if (!checkInDate || !checkOutDate) {
      alert('Пожалуйста, выберите даты заезда и выезда.');
      return;
    }

    // Проверка, попадают ли выбранные даты в занятые
    const selectedDates = [];
    let currentDate = new Date(checkInDate);
    while (currentDate <= checkOutDate) {
      selectedDates.push(new Date(currentDate));
      currentDate.setDate(currentDate.getDate() + 1);
    }

    const isOverlap = selectedDates.some(date => isDateBooked(date));
    if (isOverlap) {
      alert('Выбранные даты уже заняты. Пожалуйста, выберите другие даты.');
      return;
    }

    if (user && user.role === 'ROLE_HOSTES') {
      navigate(`/hostes/rentals/new?roomId=${room.roomId}&checkInDate=${checkInDate.toISOString()}&checkOutDate=${checkOutDate.toISOString()}`);
    } else {
      const days = Math.ceil((checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
      const roomPrice = days * room.price;
      const servicesPrice = selectedServices.reduce((total, serviceId) => {
        const service = services.find(s => s.serviceId === serviceId);
        return total + (service ? service.servicePrice : 0);
      }, 0);
      const totalPrice = roomPrice + servicesPrice;

      navigate('/payment', {
        state: {
          roomTitle: room.roomTitle,
          checkInDate: checkInDate.toISOString().split('T')[0],
          checkOutDate: checkOutDate.toISOString().split('T')[0],
          totalPrice: totalPrice,
          roomId: room.roomId,
          userId: user.id,
          serviceIds: selectedServices,
        },
      });
    }
  };

  const sliderSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 1,
    centerMode: true,
    centerPadding: '60px',
    arrows: true,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 1,
        }
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1
        }
      }
    ]
  };

  if (!room) return <div>Загрузка...</div>;

  return (
      <div className="room-detail-bg">
        <div className="services-background">
          <img src={hotelBackground} alt="Фон отеля" />
          <div className="background-overlay"></div>
        </div>
        <div className="room-detail">
          <div className="room-detail-container">
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
                    minDate={new Date()}
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
                    minDate={checkInDate || new Date()}
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
          <div className="services-slider">
            <h3>Дополнительные услуги</h3>
            <Slider {...sliderSettings}>
              {services.map(service => (
                  <div
                      key={service.serviceId}
                      className={`service-card ${selectedServices.includes(service.serviceId) ? 'selected' : ''}`}
                      onClick={() => handleSelectService(service.serviceId)}
                  >
                    <img src={service.imageUrl} alt={service.serviceName} />
                    <h4>{service.serviceName}</h4>
                    <p>{service.servicePrice} руб.</p>
                  </div>
              ))}
            </Slider>
          </div>
        </div>
      </div>
  );
};

export default RoomDetailPage;