import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Footer.css';
import '../styles/Header.css'
import { FaInstagram, FaFacebook, FaTwitter, FaYoutube } from 'react-icons/fa';
import logo from '../assets/images/logo.png'; 

const Footer = () => {
  return (
    <footer className="footer">
      <div className="container">

      <div className="footer-right">
      <div className="logo">
        <img src={logo} alt="Логотип отеля" className="logo-image" />
      </div>

        <div className="footer-links">
          <ul>
            <li><Link to="/rooms">НОМЕРА</Link></li>
            <li><Link to="/services">УСЛУГИ</Link></li>
            <li><Link to="/contacts">КОНТАКТЫ</Link></li>
          </ul>
        </div>
        </div>

        <div className="footer-social">
          <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">
            <FaInstagram className="social-icon" />
          </a>
          <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">
            <FaFacebook className="social-icon" />
          </a>
          <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">
            <FaTwitter className="social-icon" />
          </a>
          <a href="https://youtube.com" target="_blank" rel="noopener noreferrer">
            <FaYoutube className="social-icon" />
          </a>
        </div>

        <div className="footer-address">
          <p>Россия, г. Сочи</p>
          <p>Ул. Шевченко, 120 - 50</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;