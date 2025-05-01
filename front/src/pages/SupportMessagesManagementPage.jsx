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