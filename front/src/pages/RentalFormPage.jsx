import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import apiClient from "../config/apiClient";
import "../styles/Admin.css";

const RentalFormPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const queryParams = new URLSearchParams(location.search);
    const roomId = queryParams.get("roomId");
    const checkInDate = queryParams.get("checkInDate");
    const checkOutDate = queryParams.get("checkOutDate");

    const [formData, setFormData] = useState({
        visitorFirstName: "",
        visitorLastName: "",
        visitorPhone: "",
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await apiClient.post("/api/rentals", {
                ...formData,
                room: { roomId: parseInt(roomId) },
                checkInDate: checkInDate,
                checkOutDate: checkOutDate,
            });
            alert("Аренда успешно создана!");
            navigate("/hostes/rentals");
        } catch (error) {
            console.error("Ошибка создания аренды:", error);
        }
    };

    return (
        <div className="rental">
            <h2>Аренда комнаты</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    name="visitorFirstName"
                    value={formData.visitorFirstName}
                    onChange={handleInputChange}
                    placeholder="Имя посетителя"
                    required
                />
                <input
                    type="text"
                    name="visitorLastName"
                    value={formData.visitorLastName}
                    onChange={handleInputChange}
                    placeholder="Фамилия посетителя"
                    required
                />
                <input
                    type="text"
                    name="visitorPhone"
                    value={formData.visitorPhone}
                    onChange={handleInputChange}
                    placeholder="Телефон посетителя"
                    required
                />
                <button type="submit">Арендовать</button>
            </form>
        </div>
    );
};

export default RentalFormPage;
