import React, { useEffect, useState } from "react";
import Pagination from "../components/Pagination";
import "../styles/Admin.css";
import "../styles/Pagination.css";
import RoomsApi from "../config/RoomsApi";

const RoomsManagementPage = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    roomTitle: "",
    roomType: "",
    description: "",
    price: "",
    status: "AVAILABLE",
    imageUrls: []
  });
  const [editingRoom, setEditingRoom] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const itemsPerPage = 10;
  const [error, setError] = useState(null);
  const [newImageUrl, setNewImageUrl] = useState("");

  const roomTypes = ["STANDARD", "DELUXE", "SUITE", "FAMILY"];
  const roomStatuses = ["AVAILABLE", "OCCUPIED", "MAINTENANCE"];

  useEffect(() => {
    const fetchRooms = async () => {
      try {
        setLoading(true);
        setError(null);
        const response = await RoomsApi.getAllRoomsAdmin(currentPage, itemsPerPage);
        if (!response || !Array.isArray(response.content)) {
          throw new Error("Сервер вернул неверный формат данных");
        }
        setRooms(response.content);
        setTotalItems(response.totalElements || response.content.length);
      } catch (error) {
        console.error("Ошибка загрузки номеров:", error);
        setRooms([]);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, [currentPage, itemsPerPage]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleDelete = async (id) => {
    try {
      await RoomsApi.deleteRoom(id);
      setRooms((prevRooms) => prevRooms.filter((room) => room.roomId !== id));
      setTotalItems((prevTotal) => prevTotal - 1);
    } catch (error) {
      console.error("Ошибка удаления номера:", error);
      setError("Не удалось удалить номер");
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const addImage = () => {
    if (newImageUrl.trim() !== "") {
      setFormData({ ...formData, imageUrls: [...formData.imageUrls, newImageUrl] });
      setNewImageUrl("");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const newRoom = await RoomsApi.createRoom({
        roomTitle: formData.roomTitle,
        roomType: formData.roomType,
        description: formData.description,
        price: parseFloat(formData.price),
        status: formData.status,
        imageUrls: formData.imageUrls
      });
      setRooms([...rooms, newRoom]);
      setFormData({
        roomTitle: "",
        roomType: "",
        description: "",
        price: "",
        status: "AVAILABLE",
        imageUrls: []
      });
      setTotalItems(totalItems + 1);
    } catch (error) {
      console.error("Ошибка создания номера:", error);
    }
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditingRoom({ ...editingRoom, [name]: value });
  };

  const addEditImage = () => {
    if (newImageUrl.trim() !== "") {
      setEditingRoom({ ...editingRoom, imageUrls: [...editingRoom.imageUrls, newImageUrl] });
      setNewImageUrl("");
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedRoom = await RoomsApi.updateRoom(editingRoom.roomId, {
        roomTitle: editingRoom.roomTitle,
        roomType: editingRoom.roomType,
        description: editingRoom.description,
        price: parseFloat(editingRoom.price),
        status: editingRoom.status,
        imageUrls: editingRoom.imageUrls
      });

      if (!updatedRoom || !updatedRoom.roomId) {
        throw new Error("Неверный ответ сервера");
      }

      setRooms((prevRooms) =>
          prevRooms.map((room) =>
              room.roomId.toString() === updatedRoom.roomId.toString()
                  ? updatedRoom
                  : room
          )
      );

      setEditingRoom(null);
    } catch (error) {
      console.error("Ошибка обновления номера:", error);
    }
  };

  const handleDeleteImage = (index) => {
    const updatedImageUrls = [...editingRoom.imageUrls];
    updatedImageUrls.splice(index, 1);
    setEditingRoom({ ...editingRoom, imageUrls: updatedImageUrls });
  };

  if (loading) return <div>Загрузка...</div>;

  return (
      <div className="rooms-management">
        <h2 className="admin-section-title">Управление номерами</h2>

        <div className="admin-form-container">
          <h3 className="admin-subsection-title">Добавить номер:</h3>
          <form onSubmit={handleSubmit} className="admin-form">
            <input
                type="text"
                name="roomTitle"
                value={formData.roomTitle}
                onChange={handleInputChange}
                placeholder="Название номера"
                className="admin-input"
                required
            />

            <select
                name="roomType"
                value={formData.roomType}
                onChange={handleInputChange}
                className="admin-input"
                required
            >
              <option value="">Выберите тип номера</option>
              {roomTypes.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
              ))}
            </select>

            <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Описание"
                className="admin-input"
                required
            />

            <input
                type="number"
                name="price"
                value={formData.price}
                onChange={handleInputChange}
                placeholder="Цена"
                className="admin-input"
                step="1"
                min="0"
                required
            />

            <select
                name="status"
                value={formData.status}
                onChange={handleInputChange}
                className="admin-input"
                required
            >
              {roomStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
              ))}
            </select>

            <input
                type="text"
                value={newImageUrl}
                onChange={(e) => setNewImageUrl(e.target.value)}
                placeholder="URL изображения"
                className="admin-input"
            />
            <button type="button" onClick={addImage} className="admin-button admin-button-add-image">
              Добавить изображение
            </button>
            <ul>
              {formData.imageUrls.map((url, index) => (
                  <li key={index}>{url}</li>
              ))}
            </ul>

            <button
                type="submit"
                className="button admin-button admin-button-primary"
            >
              Добавить номер
            </button>
          </form>
        </div>

        {editingRoom && (
            <div className="admin-form-container admin-edit-form">
              <h3 className="admin-subsection-title">Редактировать номер</h3>
              <form onSubmit={handleEditSubmit} className="admin-form">
                <input
                    type="text"
                    name="roomTitle"
                    value={editingRoom.roomTitle}
                    onChange={handleEditInputChange}
                    placeholder="Название номера"
                    className="admin-input"
                    required
                />

                <select
                    name="roomType"
                    value={editingRoom.roomType}
                    onChange={handleEditInputChange}
                    className="admin-input"
                    required
                >
                  {roomTypes.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                  ))}
                </select>

                <textarea
                    name="description"
                    value={editingRoom.description}
                    onChange={handleEditInputChange}
                    placeholder="Описание"
                    className="admin-input"
                    required
                />

                <input
                    type="number"
                    name="price"
                    value={editingRoom.price}
                    onChange={handleEditInputChange}
                    placeholder="Цена"
                    className="admin-input"
                    step="1"
                    min="0"
                    required
                />

                <select
                    name="status"
                    value={editingRoom.status}
                    onChange={handleEditInputChange}
                    className="admin-input"
                    required
                >
                  {roomStatuses.map((status) => (
                      <option key={status} value={status}>
                        {status}
                      </option>
                  ))}
                </select>

                <input
                    type="text"
                    value={newImageUrl}
                    onChange={(e) => setNewImageUrl(e.target.value)}
                    placeholder="URL изображения"
                    className="admin-input"
                />
                <button type="button" onClick={addEditImage} className="admin-button admin-button-add-image">
                  Добавить изображение
                </button>
                <ul>
                  {editingRoom.imageUrls && editingRoom.imageUrls.map((url, index) => (
                      <li key={index}>
                        {url}
                        <button
                            type="button"
                            onClick={() => handleDeleteImage(index)}
                            className="admin-button admin-button-delete-image"
                        >
                          Удалить
                        </button>
                      </li>
                  ))}
                </ul>

                <div className="admin-form-actions">
                  <button
                      type="submit"
                      className="admin-button admin-button-save"
                  >
                    Сохранить изменения
                  </button>
                  <button
                      type="button"
                      onClick={() => setEditingRoom(null)}
                      className="admin-button admin-button-cancel"
                  >
                    Отмена
                  </button>
                </div>
              </form>
            </div>
        )}

        <div className="admin-table-container">
          <h3 className="admin-subsection-title">Список номеров</h3>
          {error && <div className="admin-error">{error}</div>}
          <table className="admin-table">
            <thead>
            <tr>
              <th>ID</th>
              <th>Название</th>
              <th>Тип</th>
              <th>Описание</th>
              <th>Цена</th>
              <th>Статус</th>
              <th>Изображения</th>
              <th>Действия</th>
            </tr>
            </thead>
            <tbody>
            {rooms.map((room) => (
                <tr key={room.roomId}>
                  <td>{room.roomId}</td>
                  <td>{room.roomTitle}</td>
                  <td>{room.roomType}</td>
                  <td>{room.description}</td>
                  <td>{room.price.toFixed(2)} ₽</td>
                  <td>{room.status}</td>
                  <td>
                    {room.images && room.images.length > 0 ? (
                        <ul>
                          {room.images.map((image, index) => (
                              <li key={index}>{image.imageUrl}</li>
                          ))}
                        </ul>
                    ) : (
                        "Нет изображений"
                    )}
                  </td>
                  <td className="admin-actions">
                    <button
                        onClick={() => setEditingRoom({ ...room, imageUrls: room.images.map(img => img.imageUrl) })}
                        className="admin-button admin-button-edit"
                    >
                      Редактировать
                    </button>
                    <button
                        onClick={() => handleDelete(room.roomId)}
                        className="admin-button admin-button-delete"
                    >
                      Удалить
                    </button>
                  </td>
                </tr>
            ))}
            </tbody>
          </table>
          <Pagination
              totalItems={totalItems}
              itemsPerPage={itemsPerPage}
              currentPage={currentPage}
              onPageChange={handlePageChange}
          />
        </div>
      </div>
  );
};

export default RoomsManagementPage;