package dev.victor_rivas.space_management.integration;

import dev.victor_rivas.space_management.enums.Role;
import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.enums.SpaceType;
import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.model.dto.SpaceDTO;
import dev.victor_rivas.space_management.model.entity.Space;
import dev.victor_rivas.space_management.model.entity.Student;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.SpaceRepository;
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
class SpaceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AccessRecordRepository accessRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String studentToken;
    private Space testSpace;

    @BeforeEach
    void setUp() {
        accessRecordRepository.deleteAll(); // or whatever your access record repository is called
        spaceRepository.deleteAll();
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

        // Create student user
        Student student = Student.builder()
                .registrationNumber("STU001")
                .name("Test Student")
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .status(StudentStatus.ACTIVE)
                .build();
        student = studentRepository.save(student);

        User studentUser = User.builder()
                .username("STU001")
                .email("student@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .student(student)
                .enabled(true)
                .build();
        studentUser = userRepository.save(studentUser);

        // Generate tokens
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication studentAuth = new UsernamePasswordAuthenticationToken(studentUser, null, studentUser.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);

        // Create test space
        testSpace = Space.builder()
                .code("LAB-001")
                .name("Computer Laboratory")
                .type(SpaceType.LABORATORY)
                .capacity(30)
                .building("Building A")
                .floor("1st Floor")
                .description("Computer lab with 30 workstations")
                .status(SpaceStatus.AVAILABLE)
                .build();
        testSpace = spaceRepository.save(testSpace);
    }

    @Test
    void getAllSpaces_WithValidToken_ReturnsSpacesList() throws Exception {
        mockMvc.perform(get("/api/spaces")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].code").value("LAB-001"))
                .andExpect(jsonPath("$.data[0].name").value("Computer Laboratory"));
    }

    @Test
    void getAllSpaces_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/spaces"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getSpaceById_WithValidId_ReturnsSpace() throws Exception {
        mockMvc.perform(get("/api/spaces/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("LAB-001"))
                .andExpect(jsonPath("$.data.name").value("Computer Laboratory"))
                .andExpect(jsonPath("$.data.type").value("LABORATORY"));
    }

    @Test
    void getSpaceById_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/spaces/99999")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Space not found with id: 99999"));
    }

    @Test
    void createSpace_WithAdminRole_ReturnsCreatedSpace() throws Exception {
        SpaceDTO newSpace = SpaceDTO.builder()
                .code("CLASS-001")
                .name("Classroom 101")
                .type(SpaceType.CLASSROOM)
                .capacity(40)
                .building("Building B")
                .floor("2nd Floor")
                .description("Standard classroom")
                .build();

        mockMvc.perform(post("/api/spaces")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSpace)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Space created successfully"))
                .andExpect(jsonPath("$.data.code").value("CLASS-001"))
                .andExpect(jsonPath("$.data.name").value("Classroom 101"));
    }

    @Test
    void createSpace_WithStudentRole_ReturnsForbidden() throws Exception {
        SpaceDTO newSpace = SpaceDTO.builder()
                .code("CLASS-002")
                .name("Classroom 102")
                .type(SpaceType.CLASSROOM)
                .capacity(40)
                .build();

        mockMvc.perform(post("/api/spaces")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSpace)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSpace_WithDuplicateCode_ReturnsBadRequest() throws Exception {
        SpaceDTO duplicateSpace = SpaceDTO.builder()
                .code("LAB-001") // Already exists
                .name("Another Lab")
                .type(SpaceType.LABORATORY)
                .capacity(25)
                .build();

        mockMvc.perform(post("/api/spaces")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateSpace)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Space code already exists"));
    }

    @Test
    void createSpace_WithInvalidData_ReturnsBadRequest() throws Exception {
        SpaceDTO invalidSpace = SpaceDTO.builder()
                .code("") // Empty code
                .name("Test Space")
                .type(SpaceType.CLASSROOM)
                .capacity(0) // Invalid capacity
                .build();

        mockMvc.perform(post("/api/spaces")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSpace)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    void updateSpace_WithAdminRole_ReturnsUpdatedSpace() throws Exception {
        SpaceDTO updateData = SpaceDTO.builder()
                .code("LAB-001")
                .name("Updated Computer Laboratory")
                .type(SpaceType.LABORATORY)
                .capacity(35)
                .building("Building A")
                .floor("1st Floor")
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/spaces/" + testSpace.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Space updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Computer Laboratory"))
                .andExpect(jsonPath("$.data.capacity").value(35));
    }

    @Test
    void updateSpace_WithStudentRole_ReturnsForbidden() throws Exception {
        SpaceDTO updateData = SpaceDTO.builder()
                .code("LAB-001")
                .name("Updated Lab")
                .type(SpaceType.LABORATORY)
                .capacity(30)
                .build();

        mockMvc.perform(put("/api/spaces/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateSpace_WithInvalidId_ReturnsNotFound() throws Exception {
        SpaceDTO updateData = SpaceDTO.builder()
                .code("LAB-999")
                .name("Non-existent Lab")
                .type(SpaceType.LABORATORY)
                .capacity(30)
                .build();

        mockMvc.perform(put("/api/spaces/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Space not found with id: 99999"));
    }

    @Test
    void deleteSpace_WithAdminRole_ReturnsSuccess() throws Exception {
        Space spaceToDelete = Space.builder()
                .code("TEMP-001")
                .name("Temporary Space")
                .type(SpaceType.STUDY_ROOM)
                .capacity(10)
                .status(SpaceStatus.AVAILABLE)
                .build();
        spaceToDelete = spaceRepository.save(spaceToDelete);

        mockMvc.perform(delete("/api/spaces/" + spaceToDelete.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Space deleted successfully"));
    }

    @Test
    void deleteSpace_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/spaces/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteSpace_WithInvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/spaces/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
