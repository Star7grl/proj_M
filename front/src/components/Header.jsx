import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/Header.css";
import useUserStore from "../store/UserStore";
import logo from '../assets/images/logo.png';


const Header = () => {
  const navigate = useNavigate();
  const { isAuth, user, isLoading, checkAuth, logout } = useUserStore();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  // Проверяем авторизацию при загрузке
  useEffect(() => {
    checkAuth();
  }, [checkAuth]);


  // Перенаправляем на страницу входа
  const handleLogin = () => {
    navigate("/login");
  };

  // Обработчик открытия/закрытия бургер-меню
  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };


  const handleLogout = async () => {
    try {
      await logout();
      if (!useUserStore.getState().isAuth) {
        navigate('/login'); 
      }
    } catch (error) {
      console.error('Ошибка при выходе:', error);
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
        {/* Логотип */}
        <div className="logo">
          <Link to="/">
            <img src={logo} alt="Логотип отеля" className="logo-image" />
          </Link>
        </div>

        {/* Бургер-меню */}
        <div className="burger-menu" onClick={toggleMenu}>
          <span style={{ transform: isMenuOpen ? "rotate(45deg)" : "none" }}></span>
          <span style={{ opacity: isMenuOpen ? 0 : 1 }}></span>
          <span style={{ transform: isMenuOpen ? "rotate(-45deg)" : "none" }}></span>
        </div>

        {/* Основная навигация */}
        <nav className={`main-nav ${isMenuOpen ? "mobile-menu-open" : ""}`}>
          <ul>
            <li>
              <Link to="/rooms">НОМЕРА</Link>
            </li>
            <li>
              <Link to="/services">УСЛУГИ</Link>
            </li>
            <li>
              <Link to="/contacts">КОНТАКТЫ</Link>
            </li>
          </ul>
        </nav>

        {/* Авторизация */}
        <div className={`auth-nav ${isMenuOpen ? "mobile-menu-open" : ""}`}>
          <ul>
            {!isAuth ? (
              <>
                <button onClick={handleLogin} className="button login-button">
                  Войти
                </button>
                <Link to="/book" className="book-button">
                  Забронировать
                </Link>
              </>
            ) : (
              <>
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
                    Выйти
                  </button>
                  </li>
              </>
            )}
          </ul>
        </div>

        {/* Мобильное меню */}
        {isMenuOpen && (
          <div className="mobile-menu-overlay" onClick={toggleMenu}>
            <div className="mobile-menu-content" onClick={(e) => e.stopPropagation()}>
              <div className="mobile-menu-close" onClick={toggleMenu}>
                &times;
              </div>
              <ul>
                <li>
                  <Link to="/rooms" onClick={toggleMenu}>
                    НОМЕРА
                  </Link>
                </li>
                <li>
                  <Link to="/services" onClick={toggleMenu}>
                    УСЛУГИ
                  </Link>
                </li>
                <li>
                  <Link to="/contacts" onClick={toggleMenu}>
                    КОНТАКТЫ
                  </Link>
                </li>
                <li>
                  {!isAuth ? (
                    <>
                      <button onClick={() => { handleLogin(); toggleMenu(); }}>
                        Войти
                      </button>
                      <Link to="/book" onClick={toggleMenu}>
                        Забронировать
                      </Link>
                    </>
                  ) : (
                    <>
                      <Link to="/profile" onClick={toggleMenu}>
                        Профиль
                      </Link>
                      <Link to="/book" onClick={toggleMenu}>
                        Забронировать
                      </Link>
                    </>
                  )}
                </li>
              </ul>
            </div>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;