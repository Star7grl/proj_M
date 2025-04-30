import React, { useEffect, useState } from 'react';
import RoomsApi from '../config/RoomsApi';
import Pagination from '../components/Pagination';
import RoomCard from '../components/RoomCard';
import '../styles/Rooms.css';
import hotelBackground from "../assets/images/номера.png";

const RoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const itemsPerPage = 9;
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchRooms = async () => {
      try {
        let data;
        if (searchQuery) {
          data = await RoomsApi.searchRoomsByTitle(searchQuery);
        } else {
          data = await RoomsApi.getAllRooms(currentPage, itemsPerPage);
        }
        setRooms(data.content || data); // Если есть пагинация, берем content, иначе весь массив
        setTotalItems(data.totalElements || data.length); // Устанавливаем общее количество
      } catch (error) {
        console.error('Ошибка загрузки комнат:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, [currentPage, searchQuery]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleSearch = (e) => {
    setSearchQuery(e.target.value);
    setCurrentPage(1); // Сбрасываем на первую страницу при новом поиске
  };

  if (loading) return <div>Загрузка...</div>;

  return (
    <div className="rooms-page-wrapper">
    {/* Фоновое изображение с затемнением */}
    <div className="rooms-background">
      <img src={hotelBackground} alt="Фон отеля" />
      <div className="background-overlay"></div>
    </div>

    <div className="rooms-page">
      <h1>НАШИ НОМЕРА</h1>
      <input
        type="text"
        placeholder="Поиск по названию"
        value={searchQuery}
        onChange={handleSearch}
        className="search-input"
      />
      <div className="cards_stay">
        {rooms.map((room) => (
          <RoomCard key={room.roomId} room={room} />
        ))}
      </div>
      {!searchQuery && (
        <Pagination
          totalItems={totalItems}
          itemsPerPage={itemsPerPage}
          currentPage={currentPage}
          onPageChange={handlePageChange}
        />
      )}
    </div>
  </div>
  );
};

export default RoomsPage;