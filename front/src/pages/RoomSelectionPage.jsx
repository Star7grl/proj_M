import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../config/apiClient";

const RoomSelectionPage = () => {
    const [rooms, setRooms] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchRooms = async () => {
            try {
                const response = await apiClient.get("/api/rooms/available");
                setRooms(response.data);
            } catch (error) {
                console.error("Ошибка загрузки комнат:", error);
            }
        };
        fetchRooms();
    }, []);

    const handleSelectRoom = (roomId) => {
        navigate(`/hostes/rentals/new?roomId=${roomId}`);
    };

    return (
        <div>
            <h2>Выбор комнаты</h2>
            <ul>
                {rooms.map((room) => (
                    <li key={room.roomId}>
                        {room.roomTitle} - {room.roomType} - {room.price}
                        <button onClick={() => handleSelectRoom(room.roomId)}>Выбрать</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default RoomSelectionPage;