import React from "react";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

const RoomImageSlider = ({ images }) => {
  const sliderHeight = 300; // фиксированная высота слайдера

  if (!images || images.length === 0) {
    return (
      <div style={{ width: "100%", height: sliderHeight, overflow: "hidden", borderRadius: "20px 0 0 20px" }}>
        <img
          src="/img/room_default.jpg"
          alt="Нет изображения"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      </div>
    );
  }

  if (images.length === 1) {
    return (
      <div style={{ width: "100%", height: sliderHeight, overflow: "hidden", borderRadius: "20px 0 0 20px" }}>
        <img
          src={images[0].imageUrl}
          alt="Фото номера"
          style={{ width: "100%", height: "100%", objectFit: "cover" }}
        />
      </div>
    );
  }

  const settings = {
    dots: true,
    infinite: true,
    speed: 400,
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
    adaptiveHeight: false,
  };

  return (
    <div style={{ width: "100%", height: sliderHeight, borderRadius: "20px 0 0 20px", overflow: "hidden" }}>
      <Slider {...settings}>
        {images.map((img, idx) => (
          <div key={idx}>
            <img
              src={img.imageUrl}
              alt={`Фото номера ${idx + 1}`}
              style={{ width: "100%", height: sliderHeight, objectFit: "cover" }}
            />
          </div>
        ))}
      </Slider>
    </div>
  );
};

export default RoomImageSlider;
