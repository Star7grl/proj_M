import React from "react";
import "../styles/Admin.css";
import { Link } from "react-router-dom";

const AdminPanelPage = () => {
    return (
        <div className="admin-panel-page">
            <div className="admin-header">
                <h2 className="admin-title">Админ-панель</h2>
                <p className="admin-subtitle">Добро пожаловать в панель администратора</p>
            </div>

            <div className="admin-controls">
                <Link to="/admin/services" className="admin-link">
                    <button className="admin-button panel-btn">
                        <span>Управление сервисами</span>
                    </button>
                </Link>

                <Link to="/admin/rooms" className="admin-link">
                    <button className="admin-button panel-btn">
                        <span>Управление комнатами</span>
                    </button>
                </Link>

                <Link to="/admin/users" className="admin-link">
                    <button className="admin-button panel-btn">
                        <span>Управление пользователями</span>
                    </button>
                </Link>

                <Link to="/admin/bookings" className="admin-link">
                    <button className="admin-button panel-btn">
                        <span>Управление бронированиями</span>
                    </button>
                </Link>
                <Link to="/admin/support-messages" className="admin-link">
                    <button className="admin-button panel-btn">
                        <span>Сообщения от пользователей</span>
                    </button>
                </Link>
            </div>
        </div>
    );
};

export default AdminPanelPage;