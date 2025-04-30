import React from 'react';
import { Link } from 'react-router-dom';

const RoomCard = ({ room }) => {
  const firstImage = room.images && room.images.length > 0 ? room.images[0].imageUrl : '/img/room_default.jpg';

  return (
    <div className="card_stay">
      <img src={firstImage} alt={room.roomTitle} />
      <div className="about_text">
        <Link to={`/rooms/${room.roomId}`}>
          <h2>{room.roomTitle}</h2>
        </Link>
        <p>{room.description}</p>
        <h3>Цена: {room.price} руб.</h3>
        {room.status === 'FREE' && (
          <button>
            <Link to={`/booking/${room.roomId}`}>Забронировать</Link>
          </button>
        )}
        {room.status === 'BOOKED' && (
          <button disabled>Забронировано</button>
        )}
      </div>
    </div>
  );
};

export default RoomCard;