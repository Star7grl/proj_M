// components/Header.js
import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Header.css';
import useUserStore from '../store/UserStore';
import logo from '../assets/images/logo.png'; 


const Header = () => {
  const navigate = useNavigate();
  // Берем нужные данные из хранилища
  const { isAuth, user, isLoading, checkAuth, logout } = useUserStore();

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
                  Забронировать
                </button>
              </>
            ) : (
              <>
                {/* Если авторизован - показываем иконку профиля и кнопку выхода */}
                <li className="profile-icon-container">
                  <Link to="/profile" className="profile-link">
                    <div className="profile-icon">
                      <svg viewBox="0 0 24 24" className="profile-svg">
                        <path 
                          d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" 
                          fill="white"
                        />
                      </svg>
                    </div>
                  </Link>
                </li>
                <li>
                  <button onClick={handleLogout} className="button book-button">
                    Забронировать
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