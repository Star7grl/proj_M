import React, { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import useUserStore from '../store/UserStore'; // Импортируем наше хранилище
import '../styles/LoginPage.css';
import bg from '../assets/images/номера.png'; 


const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { login, isAuth } = useUserStore(); // Используем методы из хранилища

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      // Используем метод login из нашего хранилища
      await login({ username, password });
      
      // Если авторизация успешна (isAuth = true), перенаправляем
      if (isAuth) {
        const from = location.state?.from?.pathname || '/';
        navigate(from, { replace: true });
      } else {
        setError('Неверные учетные данные');
      }
    } catch (error) {
      // Обрабатываем ошибку CORS и другие ошибки
      if (error.message.includes('Network Error') || !error.response) {
        setError('Проблемы с соединением. Проверьте CORS на сервере.');
      } else {
        setError(
          error.response?.data?.message || 
          error.message || 
          'Произошла ошибка при входе'
        );
      }
    }
  };

  return (
    <div className="login-container">
      <div className="bg">
        <img src={bg} alt="" className="bg" />
      </div>
      <form onSubmit={handleSubmit} className="login-form">
        <h2>Вход</h2>
        {error && <div className="error-message">{error}</div>}

        <input 
          type="text" 
          value={username} 
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Имя пользователя"
          required
          autoComplete="username"
        />
        
        <input 
          type="password" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Пароль"
          required
          autoComplete="current-password"
        />
        
        <button type="submit">
          Войти
        </button>
        
        <div className="register-link">
          <Link to="/register">Зарегистрироваться</Link>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;