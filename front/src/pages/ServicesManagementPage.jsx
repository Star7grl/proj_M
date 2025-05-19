import React, { useEffect, useState } from "react";
import Pagination from "../components/Pagination";
import "../styles/Admin.css";
import "../styles/ServicesManager.css"; // Ensure this path is correct
import "../styles/Pagination.css";
import ServicesApi from "../config/servicesApi";

const ServicesManagementPage = () => {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    serviceName: "",
    servicePrice: "",
    imageUrl: "",
    description: "", // Added description field
  });
  const [editingService, setEditingService] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const itemsPerPage = 10;
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchServices = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await ServicesApi.getAllServices(
            currentPage,
            itemsPerPage
        );
        if (!response || !Array.isArray(response.services)) {
          throw new Error("Сервер вернул неверный формат данных");
        }

        setServices(response.services);
        setTotalItems(response.total || response.services.length);
      } catch (error) {
        console.error("Ошибка загрузки услуг:", error);
        setError("Не удалось загрузить список услуг");
        setServices([]);
      } finally {
        setLoading(false);
      }
    };
    fetchServices();
  }, [currentPage, itemsPerPage]);

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handleDelete = async (id) => {
    try {
      await ServicesApi.deleteService(id);
      setServices((prevServices) =>
          prevServices.filter((service) => service.serviceId !== id)
      );
      setTotalItems((prevTotal) => prevTotal - 1);
    } catch (error) {
      console.error("Ошибка удаления услуги:", error);
      setError("Не удалось удалить услугу");
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const newService = await ServicesApi.createService({
        serviceName: formData.serviceName,
        servicePrice: parseFloat(formData.servicePrice),
        imageUrl: formData.imageUrl,
        description: formData.description, // Include description
      });
      setServices([...services, newService]);
      setFormData({ serviceName: "", servicePrice: "", imageUrl: "", description: "" }); // Reset description
      setTotalItems(totalItems + 1);
    } catch (error) {
      console.error("Ошибка создания услуги:", error);
      // Optionally, set an error state to display to the user
      setError("Не удалось создать услугу. " + (error.response?.data?.message || error.message || ""));
    }
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditingService({ ...editingService, [name]: value });
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedService = await ServicesApi.updateService(
          editingService.serviceId,
          {
            serviceName: editingService.serviceName,
            servicePrice: parseFloat(editingService.servicePrice),
            imageUrl: editingService.imageUrl,
            description: editingService.description, // Include description
          }
      );
      setServices(
          services.map((s) =>
              s.serviceId === updatedService.serviceId ? updatedService : s
          )
      );
      setEditingService(null);
    } catch (error) {
      console.error("Ошибка обновления услуги:", error);
      // Optionally, set an error state to display to the user
      setError("Не удалось обновить услугу. " + (error.response?.data?.message || error.message || ""));
    }
  };

  if (loading) return <div>Загрузка...</div>;

  return (
      <div className="services-management">
        <h2 className="admin-section-title">Управление услугами</h2>

        {error && <div className="admin-error" style={{ color: 'red', textAlign: 'center', marginBottom: '20px' }}>{error}</div>}

        <div className="admin-form-container">
          <h3 className="admin-subsection-title">Добавить услугу:</h3>
          <form onSubmit={handleSubmit} className="admin-form">
            <input
                type="text"
                name="serviceName"
                value={formData.serviceName}
                onChange={handleInputChange}
                placeholder="Название услуги"
                className="admin-input"
                required
            />
            <input
                type="number"
                name="servicePrice"
                value={formData.servicePrice}
                onChange={handleInputChange}
                placeholder="Цена"
                className="admin-input"
                step="1"
                min="0"
                required
            />
            <input
                type="text"
                name="imageUrl"
                value={formData.imageUrl}
                onChange={handleInputChange}
                placeholder="URL изображения"
                className="admin-input"
                required // Assuming image URL is still required
            />
            <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Описание услуги"
                className="admin-input" // You might want a different class for styling textareas
                rows="3"
            />
            <button
                type="submit"
                className="button admin-button admin-button-primary"
            >
              Добавить услугу
            </button>
          </form>
        </div>

        {editingService && (
            <div className="admin-form-container admin-edit-form">
              <h3 className="admin-subsection-title">Редактировать услугу</h3>
              <form onSubmit={handleEditSubmit} className="admin-form">
                <input
                    type="text"
                    name="serviceName"
                    value={editingService.serviceName || ""}
                    onChange={handleEditInputChange}
                    placeholder="Название услуги"
                    className="admin-input"
                    required
                />
                <input
                    type="number"
                    name="servicePrice"
                    value={editingService.servicePrice || ""}
                    onChange={handleEditInputChange}
                    placeholder="Цена"
                    className="admin-input"
                    step="1"
                    min="0"
                    required
                />
                <input
                    type="text"
                    name="imageUrl"
                    value={editingService.imageUrl || ""}
                    onChange={handleEditInputChange}
                    placeholder="URL изображения"
                    className="admin-input"
                    required // Assuming image URL is still required
                />
                <textarea
                    name="description"
                    value={editingService.description || ""}
                    onChange={handleEditInputChange}
                    placeholder="Описание услуги"
                    className="admin-input" // You might want a different class for styling textareas
                    rows="3"
                />
                <div className="admin-form-actions">
                  <button type="submit" className="admin-button admin-button-save">
                    Сохранить изменения
                  </button>
                  <button
                      type="button"
                      onClick={() => setEditingService(null)}
                      className="admin-button admin-button-cancel"
                  >
                    Отмена
                  </button>
                </div>
              </form>
            </div>
        )}

        <div className="services-cards-admin">
          {services && services.length > 0 ? (
              services.map((service) => (
                  <div className="service-card-admin" key={service.serviceId}>
                    <div className="service-content-admin">
                      {service.imageUrl && (
                          <img src={service.imageUrl} alt={service.serviceName} />
                      )}
                      <div className="service-details-admin">
                        <h3>{service.serviceName}</h3>
                        {/* Display description */}
                        <p className="service-description-admin">
                          {service.description || "Описание отсутствует"}
                        </p>
                        <div className="service-price-admin">{service.servicePrice} ₽</div>
                        <div style={{ display: "flex", gap: "12px", marginTop: "10px" }}>
                          <button
                              onClick={() => setEditingService(service)} // Ensure description is passed here
                              className="admin-button admin-button-edit"
                          >
                            Редактировать
                          </button>
                          <button
                              onClick={() => handleDelete(service.serviceId)}
                              className="admin-button admin-button-delete"
                          >
                            Удалить
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
              ))
          ) : (
              <div style={{ textAlign: "center", color: "#888", margin: 40 }}>
                Нет услуг
              </div>
          )}
        </div>
        <Pagination
            totalItems={totalItems}
            itemsPerPage={itemsPerPage}
            currentPage={currentPage}
            onPageChange={handlePageChange}
        />
      </div>
  );
};

export default ServicesManagementPage;