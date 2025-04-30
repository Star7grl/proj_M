package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.flamexander.spring.security.jwt.configs.CustomUserDetails;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.BookingRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final BookingRepository bookingRepository;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.role}")
    private String adminRole;

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, EmailService emailService, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void initAdmin() {
        if (!userRepository.findByUsername(adminUsername).isPresent()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword);

            Role adminRoleEntity = roleService.findByName(adminRole)
                    .orElseThrow(() -> new RuntimeException("Роль администратора не найдена"));
            admin.setRole(adminRoleEntity);

            userRepository.save(admin);
            System.out.println("Администратор успешно создан");
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь '%s' не найден", username)));
        return new CustomUserDetails(user);
    }

    @Transactional
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        Role userRole = roleService.getUserRole();
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public boolean deleteById(Long id) {
        if (bookingRepository.countActiveBookingsByUserId(id) > 0) {
            throw new IllegalStateException("Нельзя удалить пользователя с активными бронированиями");
        }
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            userToUpdate.setUsername(userDetails.getUsername());
            userToUpdate.setEmail(userDetails.getEmail());
            userToUpdate.setFirstName(userDetails.getFirstName());
            userToUpdate.setLastName(userDetails.getLastName());
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public User updateUserProfile(Long id, String firstName, String lastName) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            userToUpdate.setFirstName(firstName);
            userToUpdate.setLastName(lastName);
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public User uploadPhoto(Long id, MultipartFile photo) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String fileName = generateUniqueFileName(photo.getOriginalFilename());
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            if (!Files.isWritable(uploadDir)) {
                throw new IOException("Директория не доступна для записи: " + uploadPath);
            }
            Path filePath = uploadDir.resolve(fileName);
            try {
                logger.info("Сохранение файла: " + filePath.toString());
                Files.write(filePath, photo.getBytes());
                user.setPhotoPath(fileName);
                return userRepository.save(user);
            } catch (IOException e) {
                logger.error("Ошибка сохранения фото для пользователя с ID: {}", id, e);
                throw new IOException("Не удалось сохранить фото", e);
            }
        }
        return null;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i + 1);
        }
        return UUID.randomUUID().toString() + "." + extension;
    }

    public boolean sendResetToken(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            emailService.sendResetLink(email, token);
            return true;
        }
        return false;
    }

    public boolean validateResetToken(String token) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> optionalUser = userRepository.findByResetToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getResetTokenExpiry() != null && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetToken(null);
                user.setResetTokenExpiry(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Токен просрочен");
            }
        } else {
            throw new RuntimeException("Недействительный токен");
        }
    }
}