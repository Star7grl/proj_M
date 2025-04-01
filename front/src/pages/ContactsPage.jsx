import React from "react";
import "../styles/Contacts.css"; // Создайте отдельный файл для стилей
import support from '../assets/images/поддержка.png';

const Contacts = () => {
  return (
    <div className="contacts-container">
      {/* Hero Section */}
      <div className="hero">
        <img src="/access/img/номера.png" alt="Номера" />
        <div className="hero-content">
          <h1>Контакты</h1>
        </div>
      </div>

      {/* Map Section */}
      <div className="map-container">
        <iframe
          src="https://yandex.ru/map-widget/v1/?ll=39.909058%2C43.497425&mode=search&oid=169795129608&ol=biz&z=12.05"
          width="100%"
          height="400"
          title="Yandex Map"
          allowFullScreen
        ></iframe>
      </div>

      {/* Contact Form Section */}
      <div className="contact-section">
        <h2>Свяжитесь с нами</h2>
        
        <div className="contact-content">
          <div className="contact-image">
            <img src={support} alt="Поддержка" className="support-image"/>
          </div>
          
          <form className="contact-form">
            <div className="form-group">
              <input type="text" placeholder="Ваше имя" />
            </div>
            <div className="form-group">
              <input type="email" placeholder="Ваш E-mail" />
            </div>
            <div className="form-group">
              <textarea rows="7" placeholder="Вопрос"></textarea>
            </div>
            <button type="submit">Отправить</button>
          </form>
        </div>
      </div>
    </div>
  );
};


export default Contacts;