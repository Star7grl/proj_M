import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../styles/ResetPassword.css'

const ResetPasswordPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isValidToken, setIsValidToken] = useState(false);
    const navigate = useNavigate();

    const apiClient = axios.create({
        baseURL: 'http://localhost:8080',
        headers: { 'Content-Type': 'application/json' },
    });

    useEffect(() => {
        const verifyToken = async () => {
            try {
                const response = await apiClient.get(`/api/auth/reset-password?token=${token}`);
                if (response.status === 200) {
                    setIsValidToken(true);
                }
            } catch (error) {
                setIsValidToken(false);
            }
        };
        verifyToken();
    }, [token]);

    const handleResetPassword = async () => {
        if (newPassword !== confirmPassword) {
            alert('Пароли не совпадают');
            return;
        }
        try {
            await apiClient.post('/api/auth/reset-password', { token, newPassword });
            alert('Пароль успешно изменен');
            navigate('/login');
        } catch (error) {
            alert('Ошибка при смене пароля: ' + (error.response?.data || 'Неизвестная ошибка'));
        }
    };

    if (!isValidToken) {
        return <p>Недействительный или просроченный токен</p>;
    }

    return (
        <div className="reset_password-conteiner">
            <h2>Сброс пароля</h2>
            <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Новый пароль"
            />
            <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Подтвердите пароль"
            />
            <button onClick={handleResetPassword}>Сменить пароль</button>
        </div>
    );
};

export default ResetPasswordPage;