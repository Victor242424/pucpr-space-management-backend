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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

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
    void login_WithValidCredentials_ReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("STU001", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.type").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("STU001"))
                .andExpect(jsonPath("$.data.email").value("test@student.com"));
    }

    @Test
    void login_WithInvalidUsername_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("NONEXISTENT", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void login_WithInvalidPassword_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("STU001", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void login_WithNullUsername_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest(null, "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    void login_WithNullPassword_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("STU001", null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    void register_WithValidData_ReturnsCreated() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU002");
        request.setName("New Student");
        request.setEmail("newstudent@test.com");
        request.setPassword("password123");
        request.setPhoneNumber("9876543210");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Student registered successfully"))
                .andExpect(jsonPath("$.data.registrationNumber").value("STU002"))
                .andExpect(jsonPath("$.data.email").value("newstudent@test.com"));
    }

    @Test
    void register_WithDuplicateRegistrationNumber_ReturnsBadRequest() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU001"); // Already exists
        request.setName("Another Student");
        request.setEmail("another@test.com");
        request.setPassword("password123");
        request.setPhoneNumber("5555555555");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registration number already exists"));
    }

    @Test
    void register_WithDuplicateEmail_ReturnsBadRequest() throws Exception {
        CreateStudentRequest request = new CreateStudentRequest();
        request.setRegistrationNumber("STU003");
        request.setName("Another Student");
        request.setEmail("test@student.com"); // Already exists
        request.setPassword("password123");
        request.setPhoneNumber("5555555555");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }
}