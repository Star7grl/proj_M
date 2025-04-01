import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import '../styles/ForgotPassword.css';

const ForgotPasswordPage = () => {
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [step, setStep] = useState(1); // 1: email, 2: код, 3: новый пароль
  const navigate = useNavigate();

  const apiClient = axios.create({
    baseURL: 'http://localhost:8080', // Укажи свой URL
    headers: { 'Content-Type': 'application/json' },
  });

  const handleSendCode = async () => {
    try {
      const response = await apiClient.post('/api/auth/forgot-password', { email });
      if (response.status === 200) {
        setStep(2);
      }
    } catch (error) {
      alert('Ошибка: ' + error.response.data);
    }
  };

  const handleVerifyCode = async () => {
    try {
      const response = await apiClient.post('/api/auth/verify-code', { email, code });
      if (response.status === 200) {
        setStep(3);
      }
    } catch (error) {
      alert('Неверный код');
    }
  };

  const handleResetPassword = async () => {
    try {
      await apiClient.post('/api/auth/reset-password', { email, newPassword });
      alert('Пароль обновлен');
      navigate('/login');
    } catch (error) {
      alert('Ошибка при обновлении пароля');
    }
  };

  return (
    <div className='ForgotPassword'>
      {step === 1 && (
        <div>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Введите вашу почту"
          />
          <button onClick={handleSendCode}>Отправить код</button>
        </div>
      )}
      {step === 2 && (
        <div>
          <input
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Введите код"
          />
          <button onClick={handleVerifyCode}>Проверить код</button>
        </div>
      )}
      {step === 3 && (
        <div>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="Введите новый пароль"
          />
          <button onClick={handleResetPassword}>Сохранить</button>
        </div>
      )}
    </div>
  );
};

export default ForgotPasswordPage;