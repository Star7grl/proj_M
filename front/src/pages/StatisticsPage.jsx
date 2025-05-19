import React, { useState, useEffect } from 'react';
import AdminApi from '../api/AdminApi';
import PieChart from '../components/PieChart';
import '../styles/StatisticsPage.css';

const StatisticsPage = () => {
    const [roomStats, setRoomStats] = useState([]);
    const [serviceStats, setServiceStats] = useState([]);
    const [loadingRooms, setLoadingRooms] = useState(true);
    const [loadingServices, setLoadingServices] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                setLoadingRooms(true);
                const roomsData = await AdminApi.getRoomPopularityStats();
                setRoomStats(roomsData);
            } catch (err) {
                setError('Ошибка загрузки статистики комнат.');
                console.error(err);
            } finally {
                setLoadingRooms(false);
            }

            try {
                setLoadingServices(true);
                const servicesData = await AdminApi.getServicePopularityStats();
                setServiceStats(servicesData);
            } catch (err) {
                setError(prevError => prevError ? prevError + ' Ошибка загрузки статистики услуг.' : 'Ошибка загрузки статистики услуг.');
                console.error(err);
            } finally {
                setLoadingServices(false);
            }
        };

        fetchStats();
    }, []);

    if (loadingRooms || loadingServices) {
        return <div className="statistics-loading">Загрузка статистики...</div>;
    }

    if (error) {
        return <div className="statistics-error">{error}</div>;
    }

    return (
        <div className="statistics-page">
            <h2 className="statistics-main-title">Статистика отеля</h2>
            <div className="charts-grid-container">
                <PieChart data={roomStats} title="Популярность комнат" />
                <PieChart data={serviceStats} title="Популярность услуг" />
            </div>
        </div>
    );
};

export default StatisticsPage;
