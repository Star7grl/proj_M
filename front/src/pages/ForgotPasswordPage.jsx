import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/ResetPassword.css';

const ForgotPasswordPage = () => {
  const [email, setEmail] = useState('');
  const navigate = useNavigate();

    const apiClient = axios.create({
        baseURL: 'http://localhost:8080', // Укажите базовый URL вашего backend-а
        headers: { 'Content-Type': 'application/json' },
    });

    const handleSendResetLink = async () => {
        try {
            const response = await apiClient.post('/api/auth/forgot-password', { email });
            if (response.status === 200) {
                alert('Ссылка для сброса пароля отправлена на вашу почту');
                navigate('/login');
            }
        } catch (error) {
            alert('Ошибка: ' + (error.response?.data || 'Неизвестная ошибка'));
        }
    };


  return (
      <div className="forgot-password-page">
        <h2>Восстановление пароля</h2>
        <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Введите вашу почту"
        />
        <button onClick={handleSendResetLink}>Отправить ссылку для сброса</button>
      </div>
  );
};

export default ForgotPasswordPage;