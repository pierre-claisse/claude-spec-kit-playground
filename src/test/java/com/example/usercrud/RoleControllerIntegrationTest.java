package com.example.usercrud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class RoleControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private String rolesUrl;
    private String usersUrl;

    @BeforeEach
    void setUp() {
        rolesUrl = "http://localhost:" + port + "/roles";
        usersUrl = "http://localhost:" + port + "/users";
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    // US1: Create Role tests

    @Test
    void createRole_withValidName_returns201WithGeneratedId() {
        Role role = new Role();
        role.setName("admin");

        ResponseEntity<Role> response = restTemplate.postForEntity(rolesUrl, role, Role.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("admin");
    }

    @Test
    void createRole_withDuplicateName_returns409() {
        Role role1 = new Role();
        role1.setName("admin");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("admin");

        ResponseEntity<String> response = restTemplate.postForEntity(rolesUrl, role2, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createRole_withMissingName_returns400() {
        Role role = new Role();

        ResponseEntity<String> response = restTemplate.postForEntity(rolesUrl, role, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createRole_withEmptyName_returns400() {
        Role role = new Role();
        role.setName("");

        ResponseEntity<String> response = restTemplate.postForEntity(rolesUrl, role, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // US2: Read Roles tests

    @Test
    void getAllRoles_returnsListOfRoles() {
        Role role1 = new Role();
        role1.setName("admin");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("user");
        roleRepository.save(role2);

        ResponseEntity<Role[]> response = restTemplate.getForEntity(rolesUrl, Role[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getAllRoles_onEmptyDatabase_returnsEmptyArray() {
        ResponseEntity<Role[]> response = restTemplate.getForEntity(rolesUrl, Role[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getRoleById_withExistingId_returns200WithRole() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        ResponseEntity<Role> response = restTemplate.getForEntity(rolesUrl + "/" + savedRole.getId(), Role.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("admin");
    }

    @Test
    void getRoleById_withNonExistentId_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(rolesUrl + "/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // US3: Update Role tests

    @Test
    void updateRole_withNewName_returns200WithUpdatedRole() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        Role updateRequest = new Role();
        updateRequest.setName("administrator");

        ResponseEntity<Role> response = restTemplate.exchange(
                rolesUrl + "/" + savedRole.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Role.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("administrator");
    }

    @Test
    void updateRole_withNonExistentId_returns404() {
        Role updateRequest = new Role();
        updateRequest.setName("administrator");

        ResponseEntity<String> response = restTemplate.exchange(
                rolesUrl + "/999",
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateRole_withDuplicateName_returns409() {
        Role role1 = new Role();
        role1.setName("admin");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("user");
        Role savedRole2 = roleRepository.save(role2);

        Role updateRequest = new Role();
        updateRequest.setName("admin");

        ResponseEntity<String> response = restTemplate.exchange(
                rolesUrl + "/" + savedRole2.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void updateRole_withMissingName_returns400() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        Role updateRequest = new Role();

        ResponseEntity<String> response = restTemplate.exchange(
                rolesUrl + "/" + savedRole.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // US4: Delete Role tests

    @Test
    void deleteRole_withUnassignedRole_returns204() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        ResponseEntity<Void> response = restTemplate.exchange(
                rolesUrl + "/" + savedRole.getId(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(roleRepository.findById(savedRole.getId())).isEmpty();
    }

    @Test
    void deleteRole_withNonExistentId_returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                rolesUrl + "/999",
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteRole_withAssignedRole_returns409() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.getRoles().add(savedRole);
        userRepository.save(user);

        ResponseEntity<String> response = restTemplate.exchange(
                rolesUrl + "/" + savedRole.getId(),
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    // US5: Assign Role to User tests

    @Test
    void addRoleToUser_withValidIds_returns200WithRole() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        ResponseEntity<Role> response = restTemplate.postForEntity(
                usersUrl + "/" + savedUser.getId() + "/roles/" + savedRole.getId(),
                null,
                Role.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("admin");
    }

    @Test
    void addRoleToUser_withNonExistentUser_returns404() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        ResponseEntity<String> response = restTemplate.postForEntity(
                usersUrl + "/999/roles/" + savedRole.getId(),
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addRoleToUser_withNonExistentRole_returns404() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<String> response = restTemplate.postForEntity(
                usersUrl + "/" + savedUser.getId() + "/roles/999",
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void addRoleToUser_withAlreadyAssignedRole_returns409() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.getRoles().add(savedRole);
        User savedUser = userRepository.save(user);

        ResponseEntity<String> response = restTemplate.postForEntity(
                usersUrl + "/" + savedUser.getId() + "/roles/" + savedRole.getId(),
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    // US6: View User's Roles tests

    @Test
    void getUserRoles_withAssignedRoles_returnsRoleArray() {
        Role role1 = new Role();
        role1.setName("admin");
        Role savedRole1 = roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("user");
        Role savedRole2 = roleRepository.save(role2);

        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.getRoles().add(savedRole1);
        user.getRoles().add(savedRole2);
        User savedUser = userRepository.save(user);

        ResponseEntity<Role[]> response = restTemplate.getForEntity(
                usersUrl + "/" + savedUser.getId() + "/roles",
                Role[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getUserRoles_withNoRoles_returnsEmptyArray() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<Role[]> response = restTemplate.getForEntity(
                usersUrl + "/" + savedUser.getId() + "/roles",
                Role[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getUserRoles_withNonExistentUser_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                usersUrl + "/999/roles",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // US7: Remove Role from User tests

    @Test
    void removeRoleFromUser_withExistingAssignment_returns204() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.getRoles().add(savedRole);
        User savedUser = userRepository.save(user);

        ResponseEntity<Void> response = restTemplate.exchange(
                usersUrl + "/" + savedUser.getId() + "/roles/" + savedRole.getId(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void removeRoleFromUser_withNonExistentUser_returns404() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        ResponseEntity<String> response = restTemplate.exchange(
                usersUrl + "/999/roles/" + savedRole.getId(),
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void removeRoleFromUser_withNonExistentRole_returns404() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<String> response = restTemplate.exchange(
                usersUrl + "/" + savedUser.getId() + "/roles/999",
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void removeRoleFromUser_withNonAssignedRole_returns404() {
        Role role = new Role();
        role.setName("admin");
        Role savedRole = roleRepository.save(role);

        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<String> response = restTemplate.exchange(
                usersUrl + "/" + savedUser.getId() + "/roles/" + savedRole.getId(),
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
