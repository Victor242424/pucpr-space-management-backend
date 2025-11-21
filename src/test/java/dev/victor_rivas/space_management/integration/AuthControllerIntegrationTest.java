package dev.victor_rivas.space_management.integration;

import dev.victor_rivas.space_management.enums.Role;
import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.model.dto.CreateStudentRequest;
import dev.victor_rivas.space_management.model.dto.LoginRequest;
import dev.victor_rivas.space_management.model.entity.Student;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.StudentRepository;
import dev.victor_rivas.space_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        studentRepository.deleteAll();

        // Create a test student
        Student student = Student.builder()
                .registrationNumber("STU001")
                .name("Test Student")
                .email("test@student.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("1234567890")
                .status(StudentStatus.ACTIVE)
                .build();
        student = studentRepository.save(student);

        // Create a test user
        User user = User.builder()
                .username("STU001")
                .email("test@student.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .student(student)
                .enabled(true)
                .build();
        userRepository.save(user);
    }

    @Test
    void login_WithValidCredentials_ReturnsToken() {
        LoginRequest request = new LoginRequest("STU001", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("\"token\":");
        assertThat(response.getBody()).contains("\"type\":\"Bearer\"");
        assertThat(response.getBody()).contains("\"username\":\"STU001\"");
        assertThat(response.getBody()).contains("\"email\":\"test@student.com\"");
    }

    @Test
    void login_WithInvalidPassword_ReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("STU001", "wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid username or password");
    }

    @Test
    void login_WithInvalidUsername_ReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("NONEXISTENT", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("Invalid username or password");
    }

    @Test
    void login_WithNullUsername_ReturnsBadRequest() {
        LoginRequest request = new LoginRequest(null, "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Username is required");
    }

    @Test
    void login_WithNullPassword_ReturnsBadRequest() {
        LoginRequest request = new LoginRequest("STU001", null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Password is required");
    }

    @Test
    void login_WithEmptyUsername_ReturnsBadRequest() {
        LoginRequest request = new LoginRequest("", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Username is required");
    }

    @Test
    void register_WithValidData_ReturnsCreated() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU002");
        request.setName("New Student");
        request.setEmail("newstudent@test.com");
        request.setPassword("password123");
        request.setPhoneNumber("9876543210");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Student registered successfully");
        assertThat(response.getBody()).contains("\"registrationNumber\":\"STU002\"");
        assertThat(response.getBody()).contains("\"email\":\"newstudent@test.com\"");
    }

    @Test
    void register_WithDuplicateRegistrationNumber_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU001"); // Already exists
        request.setName("Another Student");
        request.setEmail("another@test.com");
        request.setPassword("password123");
        request.setPhoneNumber("5555555555");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Registration number already exists");
    }

    @Test
    void register_WithDuplicateEmail_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU003");
        request.setName("Another Student");
        request.setEmail("test@student.com"); // Already exists
        request.setPassword("password123");
        request.setPhoneNumber("5555555555");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Email already exists");
    }

    @Test
    void register_WithInvalidEmail_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU004");
        request.setName("Test Student");
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Email must be valid");
    }

    @Test
    void register_WithShortPassword_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU005");
        request.setName("Test Student");
        request.setEmail("test5@test.com");
        request.setPassword("123"); // Too short
        request.setPhoneNumber("1234567890");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Password must be at least 6 characters");
    }

    @Test
    void register_WithNullRequiredFields_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber(null);
        request.setName(null);
        request.setEmail(null);
        request.setPassword(null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed");
    }

    @Test
    void register_WithEmptyRequiredFields_ReturnsBadRequest() {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("");
        request.setName("");
        request.setEmail("");
        request.setPassword("");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation Failed");
    }
}