// components/Header.js
import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Header.css';
import useUserStore from '../store/UserStore';
import logo from '../assets/images/logo.png'; 


const Header = () => {
  const navigate = useNavigate();
  // Берем нужные данные из хранилища
  const { isAuth, isLoading, checkAuth, logout } = useUserStore();

  // Проверяем авторизацию при загрузке
  useEffect(() => {
    checkAuth();
  }, [checkAuth]);


  const handleLogin = () => {
    navigate('/login'); // Просто переход без лишней логики
  };


  const handleLogout = async () => {
    try {
      await logout();
      if (!useUserStore.getState().isAuth) {
        navigate('/login'); // Перенаправляем на страницу входа
      }
    } catch (error) {
      console.error('Ошибка при выходе:', error);
      // Добавили уведомление пользователя об ошибке
      alert('Не удалось выйти. Попробуйте ещё раз.');
    }
  };

  // Показываем заглушку при загрузке
  if (isLoading) {
    return <div className="header-loading">Загрузка...</div>;
  }

  return (
    <header className="header">
      <div className="container">
      <div className="logo">
        <img src={logo} alt="Логотип отеля" className="logo-image" />
      </div>
      
      <nav className='main-nav'>
        <ul>
            <li><Link to="/rooms">НОМЕРА</Link></li>
            <li><Link to="/services">УСЛУГИ</Link></li>
            <li><Link to="/contacts">КОНТАКТЫ</Link></li>
        </ul>
      </nav>

      <div className='auth-nav'>
        <ul>
          {/* Если НЕ авторизован - показываем регистрацию/вход */}
          {!isAuth ? (
            <>
              <button onClick={handleLogin} className="button login-button">
                  Войти
                </button>
                <button onClick={handleLogout} className="button book-button">
                  Зарегистрироваться
                </button>
            </>
          ) : (
            <>
              {/* Если авторизован - показываем профиль и кнопку выхода */}
              <li><Link to="/profile">Профиль</Link></li>
              <li>
                <button onClick={handleLogout} className="logout-button">
                  Выйти
                </button>
              </li>
            </>
          )}
        </ul>
      </div>
      </div>
    </header>
  );
};

export default Header;