import React, { useState, useEffect } from 'react';
import apiClient from '../config/apiClient';
import '../styles/UsersManagementPage.css';

const UsersManagementPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({ username: '', email: '', firstName: '', lastName: '' });

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await apiClient.get('/api/users');
                const filteredUsers = response.data.filter(user => user.role && user.role.name === 'ROLE_USER');
                setUsers(filteredUsers);
            } catch (error) {
                console.error('Ошибка загрузки пользователей:', error);
                setError('Не удалось загрузить пользователей');
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    const handleDeleteUser = async (userId) => {
        try {
            await apiClient.delete(`/api/users/delete/${userId}`);
            setUsers(users.filter(user => user.id !== userId));
        } catch (error) {
            console.error('Ошибка удаления пользователя:', error);
            if (error.response && error.response.data) {
                // Проверяем, является ли data строкой, если нет — пытаемся извлечь message
                const message = typeof error.response.data === 'string'
                    ? error.response.data
                    : error.response.data.message || 'Неизвестная ошибка';
                alert(message);
            } else {
                alert('Не удалось удалить пользователя');
            }
        }
    };

    const handleEditUser = (user) => {
        setEditingUser(user.id);
        setFormData({
            username: user.username,
            email: user.email,
            firstName: user.firstName || '',
            lastName: user.lastName || ''
        });
    };

    const handleSaveUser = async () => {
        try {
            const updatedUser = { ...formData, id: editingUser };
            await apiClient.put(`/api/users/update/${editingUser}`, updatedUser);
            setUsers(users.map(user => (user.id === editingUser ? { ...user, ...updatedUser } : user)));
            setEditingUser(null);
        } catch (error) {
            console.error('Ошибка обновления пользователя:', error);
            alert('Не удалось обновить пользователя');
        }
    };

    const handleCancelEdit = () => {
        setEditingUser(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    if (loading) return <div>Загрузка...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="users-management-page">
            <h2>Управление пользователями с ролью "USER"</h2>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Имя пользователя</th>
                    <th>Email</th>
                    <th>Роль</th>
                    <th>Имя</th>
                    <th>Фамилия</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                {users.map(user => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>{user.role ? user.role.name : 'Нет роли'}</td>
                        <td>{user.firstName || 'Не указано'}</td>
                        <td>{user.lastName || 'Не указано'}</td>
                        <td>
                            {editingUser === user.id ? (
                                <>
                                    <button onClick={handleSaveUser}>Сохранить</button>
                                    <button onClick={handleCancelEdit}>Отмена</button>
                                </>
                            ) : (
                                <>
                                    <button onClick={() => handleEditUser(user)}>Редактировать</button>
                                    <button onClick={() => handleDeleteUser(user.id)}>Удалить</button>
                                </>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {editingUser && (
                <div className="edit-form">
                    <h3>Редактирование пользователя</h3>
                    <form>
                        <label>Имя пользователя:</label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleInputChange}
                        />
                        <label>Email:</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                        />
                        <label>Имя:</label>
                        <input
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleInputChange}
                        />
                        <label>Фамилия:</label>
                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleInputChange}
                        />
                    </form>
                </div>
            )}
        </div>
    );
};

export default UsersManagementPage;