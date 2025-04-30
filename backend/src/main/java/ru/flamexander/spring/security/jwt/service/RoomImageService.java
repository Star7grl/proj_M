package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Room;
import ru.flamexander.spring.security.jwt.entities.RoomImage;
import ru.flamexander.spring.security.jwt.repositories.RoomImageRepository;

@Service
public class RoomImageService {
    private final RoomImageRepository roomImageRepository;

    @Autowired
    public RoomImageService(RoomImageRepository roomImageRepository) {
        this.roomImageRepository = roomImageRepository;
    }

    public RoomImage addImageToRoom(Room room, String imageUrl) {
        RoomImage image = new RoomImage();
        image.setRoom(room);
        image.setImageUrl(imageUrl);
        return roomImageRepository.save(image);
    }

    // Мой комментарий: Сервис позволяет добавлять изображения к комнате через RoomImageRepository.
}
