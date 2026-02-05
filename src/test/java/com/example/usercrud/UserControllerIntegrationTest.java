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
class UserControllerIntegrationTest {

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
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/users";
        userRepository.deleteAll();
    }

    @Test
    void createUser_withValidData_returns201WithGeneratedId() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");

        ResponseEntity<User> response = restTemplate.postForEntity(baseUrl, user, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John Doe");
        assertThat(response.getBody().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void createUser_withMissingName_returns400() {
        User user = new User();
        user.setEmail("john@example.com");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createUser_withMissingEmail_returns400() {
        User user = new User();
        user.setName("John Doe");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, user, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllUsers_returnsListOfUsers() {
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        userRepository.save(user2);

        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getAllUsers_onEmptyDatabase_returnsEmptyArray() {
        ResponseEntity<User[]> response = restTemplate.getForEntity(baseUrl, User[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getUserById_withExistingId_returns200WithUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<User> response = restTemplate.getForEntity(baseUrl + "/" + savedUser.getId(), User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John");
    }

    @Test
    void getUserById_withNonExistentId_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateUser_withNewName_returns200WithUpdatedUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        User updateRequest = new User();
        updateRequest.setName("Jane");

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Jane");
        assertThat(response.getBody().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void updateUser_withNewEmail_returns200WithUpdatedUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        User updateRequest = new User();
        updateRequest.setEmail("newemail@example.com");

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("John");
        assertThat(response.getBody().getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void updateUser_withNonExistentId_returns404() {
        User updateRequest = new User();
        updateRequest.setName("Jane");

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteUser_withExistingId_returns204AndRemovesUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User savedUser = userRepository.save(user);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    void deleteUser_withNonExistentId_returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
