package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.exceptions.ResourceNotFoundException;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service // Аннотация, обозначающая, что это сервис Spring
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        if (!roleRepository.existsByName("ROLE_USER")) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
    }

    // Добавляем метод getUserRole()
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль пользователя по умолчанию не найдена"));
    }

    public Role getAdminRole() {
        return roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Роль админа не найдена"));
    }



    // Остальные методы остаются без изменений
    public Iterable<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }



    public Role createRole(Role role) { // Создание новой роли
        return roleRepository.save(role); // Использование метода save() из JpaRepository для сохранения
    }

    public Role updateRole(Role role) { // Обновление роли
        return roleRepository.save(role); // Использование метода save() для обновления (ID должен быть установлен)
    }

    public void deleteRole(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Роль не найдена"));
        roleRepository.delete(role);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


}

//@Service
//@RequiredArgsConstructor
//public class RoleService {
//    private final RoleRepository roleRepository;
//
//    public Role getUserRole() {
//        return roleRepository.findByName("ROLE_USER").get();
//    }
//}
