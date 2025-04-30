import React, { useState, useEffect } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import useUserStore from '../store/UserStore'; 
import '../styles/LoginPage.css';
import bg from '../assets/images/номера.png'; 


const LoginPage = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const { login, isAuth } = useUserStore();

  useEffect(() => {
    if (isAuth) {
      navigate('/');
    }
  }, [isAuth, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      await login({ username, password });
    } catch (error) {
      if (error.message.includes('Network Error') || !error.response) {
        setError('Проблемы с соединением. Проверьте CORS на сервере.');
      } else {
        setError(
          error.response?.data?.message || 
          error.message || 
          'Неверные учетные данные'
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
        
        <button type="submit" className="button reg-button">
          Войти
        </button>
        
        <div className="register-link">
          <Link to="/register">Зарегистрироваться</Link>
          <Link to="/forgot-password">Забыли пароль?</Link>
        </div>
      </form>
    </div>
  );
};

export default LoginPage;