import React, { useState } from 'react';
import useUserStore from '../store/UserStore';
import SupportApi from '../config/SupportApi';
import "../styles/Contacts.css";
import support from "../assets/images/поддержка.png";

const ContactsPage = () => {
  const { isAuth } = useUserStore();
  const [message, setMessage] = useState('');
  const [status, setStatus] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAuth) {
      setStatus('Авторизируйтесь для отправки сообщения');
      return;
    }
    try {
      await SupportApi.sendMessage({ messageText: message });
      setStatus('Сообщение успешно отправлено');
      setMessage('');
    } catch (error) {
      setStatus('Ошибка при отправке сообщения');
    }
  };

  return (
      <div className="contacts-container">
        <h1>Контакты</h1>

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
              <img src={support} alt="Поддержка" className="support-image" />
            </div>
            <form className="contact-form" onSubmit={handleSubmit}>
              <div className="form-group">
              <textarea
                  rows="7"
                  placeholder="Ваш вопрос"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
              ></textarea>
              </div>
              <button className="send-button" type="submit">Отправить</button>
              {status && <p>{status}</p>}
            </form>
          </div>
        </div>
      </div>
  );
};

export default ContactsPage;