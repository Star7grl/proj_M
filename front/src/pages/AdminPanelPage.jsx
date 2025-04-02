import React from "react";
import "../styles/Admin.css";
import { Link } from "react-router-dom";

const AdminPanelPage = () => {
    return (
        <div className="admin-panel-page">
            <h2>Админ-панель</h2>
            <p>Добро пожаловать в панель администратора.</p>
            <Link to="/admin/services">
                <button>Управление сервисами</button>
            </Link>
        </div>
    );
};

export default AdminPanelPage;