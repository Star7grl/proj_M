import React from 'react';
import '../styles/ProfilePage.css';

const ProfilePhoto = ({ photoPath }) => {
    return photoPath ? (
        <img
            src={`http://localhost:8080/uploads/${photoPath}`}
            alt="Фото профиля"
            className="profile-photo"
        />
    ) : (
        <p>Фото не загружено</p>
    );
};

export default ProfilePhoto;