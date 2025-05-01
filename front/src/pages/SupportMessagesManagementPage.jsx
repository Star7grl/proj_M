import React, { useState, useEffect } from 'react';
import SupportApi from '../config/SupportApi';
import '../styles/SupportMessagesManagement.css';

const SupportMessagesManagementPage = () => {
    const [messages, setMessages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const data = await SupportApi.getAllMessages();
                setMessages(data);
            } catch (error) {
                console.error('Ошибка загрузки сообщений:', error);
                setError('Не удалось загрузить сообщения');
            } finally {
                setLoading(false);
            }
        };
        fetchMessages();
    }, []);

    const handleStatusChange = async (id, newStatus) => {
        try {
            await SupportApi.updateMessageStatus(id, newStatus);
            setMessages(messages.map(msg => msg.id === id ? { ...msg, status: newStatus } : msg));
        } catch (error) {
            console.error('Ошибка обновления статуса:', error);
            setError('Не удалось обновить статус');
        }
    };

    if (loading) return <div>Загрузка...</div>;
    if (error) return <div>{error}</div>;

    return (
        <div className="support-messages-container">
            <h2>Сообщения поддержки</h2>
            {messages.length > 0 ? (
                <ul>
                    {messages.map((message) => (
                        <li key={message.id}>
                            <strong>{message.username}</strong> ({new Date(message.createdAt).toLocaleString()}): {message.messageText}
                            <div>
                                <label>Статус: </label>
                                <select
                                    value={message.status}
                                    onChange={(e) => handleStatusChange(message.id, e.target.value)}
                                >
                                    <option value="Не назначен">Не назначен</option>
                                    <option value="На рассмотрении">На рассмотрении</option>
                                    <option value="Решён">Решён</option>
                                </select>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Нет сообщений</p>
            )}
        </div>
    );
};

export default SupportMessagesManagementPage;