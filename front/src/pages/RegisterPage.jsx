// src/pages/RegisterPage.jsx
import React, { useState } from "react";
import { useNavigate, Link } from 'react-router-dom';
import '../styles/RegisterPage.css';
import useUserStore from '../store/UserStore'; 
import bg from '../assets/images/номера.png'; 

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register } = useUserStore(); // Используем метод register из хранилища
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    email: ''
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrors({});

    try {
      if (formData.password !== formData.confirmPassword) {
        setErrors({ confirmPassword: 'Пароли не совпадают' });
        return;
      }

      // Используем метод register из UserStore
      await register(formData);
      
      alert('Регистрация прошла успешно! Теперь вы можете войти.');
      navigate('/login');
    } catch (error) {
      if (error.response) {
        setErrors(error.response.data);
      } else {
        console.error('Ошибка регистрации:', error);
        setErrors({ general: 'Ошибка подключения к серверу' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="register-container">

      <div className="bg">
        <img src={bg} alt="" className="bg" />
      </div>
      
      <form onSubmit={handleSubmit} className="register-form">
        <h2>Регистрация</h2>
        {errors.general && <div className="error-message">{errors.general}</div>}

        <div className="form-group">
          <input
            type="text"
            value={formData.username}
            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
            placeholder="Введите логин"
            className="form-control"
            disabled={isLoading}
          />
          {errors.username && <div className="error-message">{errors.username}</div>}
        </div>

        <div className="form-group">
          <input
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            placeholder="Введите пароль"
            className="form-control"
            disabled={isLoading}
          />
          {errors.password && <div className="error-message">{errors.password}</div>}
          <div className="password-hint">
            Пароль должен содержать минимум 8 символов, включая заглавную и строчную буквы, цифру и спец. символ (@#$%^&+=!)
          </div>
        </div>

        <div className="form-group">
          <input
            type="password"
            value={formData.confirmPassword}
            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
            placeholder="Подтвердите пароль"
            className="form-control"
            disabled={isLoading}
          />
          {errors.confirmPassword && <div className="error-message">{errors.confirmPassword}</div>}
        </div>

        <div className="form-group">
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            placeholder="Введите email"
            className="form-control"
            disabled={isLoading}
          />
          {errors.email && <div className="error-message">{errors.email}</div>}
        </div>

        <button 
          type="submit" 
          className="btn btn-primary register-button"
          disabled={isLoading}
        >
          {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
        </button>

        <div className="login-link">
          <Link to="/login">Уже есть аккаунт?</Link>
        </div>
      </form>
    </div>
  );
};

export default RegisterPage;