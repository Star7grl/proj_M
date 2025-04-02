import React from 'react';
import '../styles/Rooms.css';
import nom1 from '../assets/images/ном 1.png'; 
import nom2 from '../assets/images/ном 2.png'; 
import nom3 from '../assets/images/ном 3.png'; 
import nom4 from '../assets/images/ном 4.png'; 


const ServicesPage = () => {
  return (
    <div className="home-page">
      {/* hero */}
      <section className="hero">
        <img src={nom1} alt="Поддержка" className="support-image" />
        <div className="hero-content"><h1>Номера</h1></div>
      </section>

      <section className="cards_stay container">
        <div className="card_stay">
          <img src={nom1} alt="Классик" />
          <div className="about_text">
            <h2>Классик</h2>
            <p>
              Стильный и комфортабельный двухкомнатный двухместный номер с балконом. В номере предусмотрено все для Вашего удобства: кухонный уголок, гардеробная, ванная комната, гостиная с мягкой мебелью и спальня с одной двуспальной кроватью.
            </p>
            <h3> от 5 000 руб./сутки</h3>
            <button><a href="./basket.html">Забронировать</a></button>
          </div>
        </div>

        <div className="card_stay reverse_sh">
          <div className="about_text">
            <h2>Делюкс</h2>
            <p>
              Стильный и комфортабельный двухкомнатный двухместный номер с балконом. В номере предусмотрено все для Вашего удобства: кухонный уголок, гардеробная, ванная комната, гостиная с мягкой мебелью и спальня с одной двуспальной кроватью.
            </p>
            <h3> от 7 000 руб./сутки</h3>
            <button><a href="./basket.html">Забронировать</a></button>
          </div>
          <img src={nom2} alt="Делюкс" />
        </div>

        <div className="card_stay">
          <img src={nom3} alt="Люкс / Люкс Премьер" />
          <div className="about_text">
            <h2>Люкс / Люкс Премьер</h2>
            <p>
              Стильный и комфортабельный двухкомнатный двухместный номер с балконом. В номере предусмотрено все для Вашего удобства: кухонный уголок, гардеробная, ванная комната, гостиная с мягкой мебелью и спальня с одной двуспальной кроватью.
            </p>
            <h3> от 10 000 руб./сутки</h3>
            <button><a href="./basket.html">Забронировать</a></button>
          </div>
        </div>

        <div className="card_stay reverse_sh">
          <div className="about_text">
            <h2>Гранд Люкс</h2>
            <p>
              Стильный и комфортабельный двухкомнатный двухместный номер с балконом. В номере предусмотрено все для Вашего удобства: кухонный уголок, гардеробная, ванная комната, гостиная с мягкой мебелью и спальня с одной двуспальной кроватью.
            </p>
            <h3> от 15 000 руб./сутки</h3>
            <button><a href="./basket.html">Забронировать</a></button>
          </div>
          <img src={nom4} alt="Гранд Люкс" />
        </div>
      </section>
    </div>
  );
};

export default ServicesPage;