import React from 'react';
import BookingTable from '../components/BookingTable';
import '../styles/BookingTable.css'; 

const BookingsManagementPage = () => {
    return (
        <div className="booking-table-container">
            <h1 className="booking-table-header">Управление бронированиями</h1>
            <BookingTable />
        </div>
    );
};

export default BookingsManagementPage;