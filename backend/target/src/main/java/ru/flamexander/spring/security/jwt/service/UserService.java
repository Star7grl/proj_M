package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.configs.CustomUserDetails;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Добавляем EmailService

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.role}")
    private String adminRole;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostConstruct
    public void initAdmin() {
        if (!userRepository.findByUsername(adminUsername).isPresent()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword); // Пароль уже захеширован в properties

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

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь '%s' не найден", username)));
        return new CustomUserDetails(user);
    }

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
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();
            userToUpdate.setUsername(userDetails.getUsername());
            userToUpdate.setEmail(userDetails.getEmail());
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

    // Новые методы для восстановления пароля
    public boolean sendResetCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String code = generateRandomCode();
            user.setResetCode(code);
            userRepository.save(user);
            emailService.sendResetCode(email, code);
            return true;
        }
        return false;
    }

    public boolean verifyResetCode(String email, String code) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent() && code.equals(optionalUser.get().getResetCode());
    }

    public void resetPassword(String email, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetCode(null); // Очищаем код после использования
            userRepository.save(user);
        }
    }

    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}