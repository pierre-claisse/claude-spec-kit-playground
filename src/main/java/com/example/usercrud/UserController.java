package com.example.usercrud;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updateRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (updateRequest.getName() != null) {
                        user.setName(updateRequest.getName());
                    }
                    if (updateRequest.getEmail() != null) {
                        user.setEmail(updateRequest.getEmail());
                    }
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/roles")
    @Transactional(readOnly = true)
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Set<Role> roles = new HashSet<>(user.getRoles());
                    return ResponseEntity.ok(roles);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/roles/{roleId}")
    @Transactional
    public ResponseEntity<Role> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Role role = roleOpt.get();
        if (user.getRoles().contains(role)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    @Transactional
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Role role = roleOpt.get();
        if (!user.getRoles().contains(role)) {
            return ResponseEntity.notFound().build();
        }
        user.getRoles().remove(role);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}
