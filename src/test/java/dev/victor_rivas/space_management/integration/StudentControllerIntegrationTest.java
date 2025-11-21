package dev.victor_rivas.space_management.integration;

import dev.victor_rivas.space_management.enums.Role;
import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.model.dto.StudentDTO;
import dev.victor_rivas.space_management.model.entity.Student;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.StudentRepository;
import dev.victor_rivas.space_management.repository.UserRepository;
import dev.victor_rivas.space_management.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String studentToken;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        studentRepository.deleteAll();

        // Create admin user
        User adminUser = User.builder()
                .username("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        adminUser = userRepository.save(adminUser);

        // Create test student
        testStudent = Student.builder()
                .registrationNumber("STU001")
                .name("Test Student")
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("1234567890")
                .status(StudentStatus.ACTIVE)
                .build();
        testStudent = studentRepository.save(testStudent);

        User studentUser = User.builder()
                .username("STU001")
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .student(testStudent)
                .enabled(true)
                .build();
        studentUser = userRepository.save(studentUser);

        // Generate tokens
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication studentAuth = new UsernamePasswordAuthenticationToken(studentUser, null, studentUser.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);
    }

    @Test
    void getAllStudents_WithAdminRole_ReturnsStudentsList() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].registrationNumber").value("STU001"))
                .andExpect(jsonPath("$.data[0].name").value("Test Student"));
    }

    @Test
    void getAllStudents_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllStudents_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getStudentById_WithValidId_ReturnsStudent() throws Exception {
        mockMvc.perform(get("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.registrationNumber").value("STU001"))
                .andExpect(jsonPath("$.data.name").value("Test Student"))
                .andExpect(jsonPath("$.data.email").value("student@test.com"));
    }

    @Test
    void getStudentById_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/students/99999")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student not found with id: 99999"));
    }

    @Test
    void updateStudent_WithValidData_ReturnsUpdatedStudent() throws Exception {
        StudentDTO updateData = StudentDTO.builder()
                .registrationNumber("STU001")
                .name("Updated Student Name")
                .email("updated@test.com")
                .phoneNumber("9876543210")
                .status(StudentStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Student updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Student Name"))
                .andExpect(jsonPath("$.data.email").value("updated@test.com"));
    }

    @Test
    void updateStudent_WithDuplicateEmail_ReturnsBadRequest() throws Exception {
        // Create another student
        Student anotherStudent = Student.builder()
                .registrationNumber("STU002")
                .name("Another Student")
                .email("another@test.com")
                .password(passwordEncoder.encode("password123"))
                .status(StudentStatus.ACTIVE)
                .build();
        anotherStudent = studentRepository.save(anotherStudent);

        // Try to update with duplicate email
        StudentDTO updateData = StudentDTO.builder()
                .registrationNumber("STU001")
                .name("Test Student")
                .email("another@test.com") // Already exists
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(put("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void updateStudent_WithInvalidId_ReturnsNotFound() throws Exception {
        StudentDTO updateData = StudentDTO.builder()
                .registrationNumber("STU999")
                .name("Non-existent Student")
                .email("nonexistent@test.com")
                .build();

        mockMvc.perform(put("/api/students/99999")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student not found with id: 99999"));
    }

    @Test
    void updateStudent_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        StudentDTO updateData = StudentDTO.builder()
                .registrationNumber("STU001")
                .name("Test Student")
                .email("invalid-email") // Invalid email format
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(put("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    void deleteStudent_WithAdminRole_ReturnsSuccess() throws Exception {
        Student studentToDelete = Student.builder()
                .registrationNumber("STU999")
                .name("Temporary Student")
                .email("temp@test.com")
                .password(passwordEncoder.encode("password123"))
                .status(StudentStatus.ACTIVE)
                .build();
        studentToDelete = studentRepository.save(studentToDelete);

        User tempUser = User.builder()
                .username("STU999")
                .email("temp@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .student(studentToDelete)
                .enabled(true)
                .build();
        userRepository.save(tempUser);

        mockMvc.perform(delete("/api/students/" + studentToDelete.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Student deleted successfully"));
    }

    @Test
    void deleteStudent_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteStudent_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_WithBlankName_ReturnsBadRequest() throws Exception {
        StudentDTO updateData = StudentDTO.builder()
                .registrationNumber("STU001")
                .name("") // Blank name
                .email("student@test.com")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(put("/api/students/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }
}
