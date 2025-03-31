package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        role.setId(id);
        return ResponseEntity.ok(roleService.updateRole(role));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }
}
