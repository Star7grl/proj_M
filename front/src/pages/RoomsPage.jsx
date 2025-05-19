import React, { useEffect, useState } from 'react';
import RoomsApi from '../config/RoomsApi';
import Pagination from '../components/Pagination';
import RoomCard from '../components/RoomCard';

import { Slider, Typography, Box } from '@mui/material';

import '../styles/Rooms.css';
import hotelBackground from "../assets/images/номера.png";

const RoomsPage = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const itemsPerPage = 9;

  // searchTerm stores the live input value
  const [searchTerm, setSearchTerm] = useState('');
  // searchQuery triggers the API call upon submission
  const [searchQuery, setSearchQuery] = useState('');

  // Основной диапазон цены для фильтрации (запрос на сервер)
  const [priceRange, setPriceRange] = useState([0, 50000]);
  // Временный диапазон для отображения при движении ползунка
  const [tempPriceRange, setTempPriceRange] = useState([0, 50000]);

  useEffect(() => {
    const fetchRooms = async () => {
      setLoading(true);
      try {
        let data;
        // Check if searchQuery has a value or if priceRange is not default
        if (searchQuery || priceRange[0] !== 0 || priceRange[1] !== 50000) {
          data = await RoomsApi.searchRooms(
              searchQuery, // Use the submitted searchQuery
              priceRange[0],
              priceRange[1],
              currentPage,
              itemsPerPage
          );
        } else {
          data = await RoomsApi.getAllRooms(currentPage, itemsPerPage);
        }
        setRooms(data.content); // Assuming backend always returns paginated structure
        setTotalItems(data.totalElements);
      } catch (error) {
        console.error('Ошибка загрузки комнат:', error);
        setRooms([]);
        setTotalItems(0);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, [currentPage, searchQuery, priceRange]); // searchQuery triggers fetch

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  // Handles form submission (button click or Enter press)
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setSearchQuery(searchTerm); // Update searchQuery with the current input term
    setCurrentPage(1); // Reset to first page for new search
  };

  const handlePriceChange = (event, newValue) => {
    setTempPriceRange(newValue);
  };

  const handlePriceChangeCommitted = (event, newValue) => {
    if (event && event.preventDefault) event.preventDefault();
    setPriceRange(newValue);
    setCurrentPage(1); // Reset to first page when price filter changes
  };

  const totalPages = Math.ceil(totalItems / itemsPerPage);

  if (loading) return <div className="loading-indicator">Загрузка...</div>; // Added a class for styling if needed

  return (
      <div className="rooms-page-wrapper">
        <div className="rooms-background">
          <img src={hotelBackground} alt="Фон отеля" />
          <div className="background-overlay"></div>
        </div>

        <div className="rooms-page">
          <h1>НАШИ НОМЕРА</h1>

          <form className="search-form-container" onSubmit={handleSearchSubmit}>
            <input
                type="text"
                placeholder="Поиск по названию"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)} // Update searchTerm on input change
                className="search-input"
            />
            <button type="submit" className="search-button">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
            </button>
          </form>

          <Box sx={{ maxWidth: 500, margin: '0 auto 2rem', px: 2 }}>
            <Typography
                variant="subtitle1"
                align="center"
                gutterBottom
                sx={{ fontWeight: 600, color: '#333' }}
            >
              Цена: {tempPriceRange[0]}₽ - {tempPriceRange[1]}₽
            </Typography>
            <Slider
                value={tempPriceRange}
                onChange={handlePriceChange}
                onChangeCommitted={handlePriceChangeCommitted}
                valueLabelDisplay="auto"
                min={0}
                max={50000}
                disableSwap
                sx={{
                  color: '#9b59b6',
                  '& .MuiSlider-thumb': {
                    backgroundColor: '#fff',
                    border: '2px solid #9b59b6',
                  },
                  '& .MuiSlider-track': {
                    border: 'none',
                  },
                  '& .MuiSlider-rail': {
                    opacity: 0.5,
                    backgroundColor: '#ddd',
                  },
                }}
            />
          </Box>

          {rooms.length > 0 ? (
              <div className="cards_stay">
                {rooms.map((room) => (
                    <RoomCard key={room.roomId} room={room} />
                ))}
              </div>
          ) : (
              !loading && <p className="no-rooms-message">По вашему запросу номера не найдены.</p> // Message if no rooms
          )}

          {totalPages > 1 && (
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
