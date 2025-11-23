package dev.victor_rivas.space_management.integration;

import dev.victor_rivas.space_management.enums.*;
import dev.victor_rivas.space_management.model.dto.EntryRequest;
import dev.victor_rivas.space_management.model.dto.ExitRequest;
import dev.victor_rivas.space_management.model.entity.AccessRecord;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AccessRecordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessRecordRepository accessRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SpaceRepository spaceRepository;

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
    private Space testSpace;
    private AccessRecord testAccessRecord;

    @BeforeEach
    void setUp() {
        accessRecordRepository.deleteAll();
        userRepository.deleteAll();
        studentRepository.deleteAll();
        spaceRepository.deleteAll();

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

        // Create test space
        testSpace = Space.builder()
                .code("LAB-001")
                .name("Computer Laboratory")
                .type(SpaceType.LABORATORY)
                .capacity(30)
                .building("Building A")
                .floor("1st Floor")
                .status(SpaceStatus.AVAILABLE)
                .build();
        testSpace = spaceRepository.save(testSpace);

        // Generate tokens
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        adminToken = jwtTokenProvider.generateToken(adminAuth);

        Authentication studentAuth = new UsernamePasswordAuthenticationToken(studentUser, null, studentUser.getAuthorities());
        studentToken = jwtTokenProvider.generateToken(studentAuth);

        // Create a completed access record for testing
        testAccessRecord = AccessRecord.builder()
                .student(testStudent)
                .space(testSpace)
                .entryTime(LocalDateTime.now().minusHours(2))
                .exitTime(LocalDateTime.now().minusHours(1))
                .status(AccessStatus.COMPLETED)
                .notes("Test access record")
                .build();
        testAccessRecord = accessRecordRepository.save(testAccessRecord);
    }

    @Test
    void registerEntry_WithValidData_ReturnsAccessRecord() throws Exception {
        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(testSpace.getId());
        request.setNotes("Studying for exam");

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Entry registered successfully"))
                .andExpect(jsonPath("$.data.studentName").value("Test Student"))
                .andExpect(jsonPath("$.data.spaceName").value("Computer Laboratory"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void registerEntry_WithInactiveStudent_ReturnsBadRequest() throws Exception {
        Student inactiveStudent = Student.builder()
                .registrationNumber("STU002")
                .name("Inactive Student")
                .email("inactive@test.com")
                .password(passwordEncoder.encode("password123"))
                .status(StudentStatus.INACTIVE)
                .build();
        inactiveStudent = studentRepository.save(inactiveStudent);

        EntryRequest request = new EntryRequest();
        request.setStudentId(inactiveStudent.getId());
        request.setSpaceId(testSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Estudante não está ativo"));
    }

    @Test
    void registerEntry_WithUnavailableSpace_ReturnsBadRequest() throws Exception {
        Space unavailableSpace = Space.builder()
                .code("MAINT-001")
                .name("Maintenance Space")
                .type(SpaceType.CLASSROOM)
                .capacity(20)
                .status(SpaceStatus.MAINTENANCE)
                .build();
        unavailableSpace = spaceRepository.save(unavailableSpace);

        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(unavailableSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Espaço não está disponível"));
    }

    @Test
    void registerEntry_WithDuplicateActiveAccess_ReturnsBadRequest() throws Exception {
        // Create an active access record
        AccessRecord activeAccess = AccessRecord.builder()
                .student(testStudent)
                .space(testSpace)
                .entryTime(LocalDateTime.now())
                .status(AccessStatus.ACTIVE)
                .build();
        accessRecordRepository.save(activeAccess);

        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(testSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Estudante já possui um acesso ativo em um espaço"));
    }

    @Test
    void registerEntry_WithStudentActiveInAnotherSpace_ReturnsBadRequest() throws Exception {
        Space anotherSpace = Space.builder()
                .code("CLASS-001")
                .name("Classroom 101")
                .type(SpaceType.CLASSROOM)
                .capacity(40)
                .building("Building B")
                .floor("2nd Floor")
                .status(SpaceStatus.AVAILABLE)
                .build();
        anotherSpace = spaceRepository.save(anotherSpace);

        AccessRecord activeAccess = AccessRecord.builder()
                .student(testStudent)
                .space(anotherSpace)
                .entryTime(LocalDateTime.now())
                .status(AccessStatus.ACTIVE)
                .build();
        accessRecordRepository.save(activeAccess);

        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(testSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Estudante já possui um acesso ativo em um espaço"));
    }

    @Test
    void registerEntry_WithInvalidStudentId_ReturnsNotFound() throws Exception {
        EntryRequest request = new EntryRequest();
        request.setStudentId(99999L);
        request.setSpaceId(testSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Estudante não encontrado"));
    }

    @Test
    void registerEntry_WithInvalidSpaceId_ReturnsNotFound() throws Exception {
        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(99999L);

        mockMvc.perform(post("/api/access/entry")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Espaço não encontrado"));
    }

    @Test
    void registerExit_WithValidData_ReturnsCompletedAccessRecord() throws Exception {
        // Create an active access record
        AccessRecord activeAccess = AccessRecord.builder()
                .student(testStudent)
                .space(testSpace)
                .entryTime(LocalDateTime.now().minusHours(1))
                .status(AccessStatus.ACTIVE)
                .build();
        activeAccess = accessRecordRepository.save(activeAccess);

        ExitRequest request = new ExitRequest();
        request.setAccessRecordId(activeAccess.getId());
        request.setNotes("Study session completed");

        mockMvc.perform(post("/api/access/exit")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Exit registered successfully"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.exitTime").isNotEmpty())
                .andExpect(jsonPath("$.data.durationInMinutes").isNumber());
    }

    @Test
    void registerExit_WithInactiveAccessRecord_ReturnsBadRequest() throws Exception {
        ExitRequest request = new ExitRequest();
        request.setAccessRecordId(testAccessRecord.getId()); // Already completed

        mockMvc.perform(post("/api/access/exit")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Registro de acesso não está ativo"));
    }

    @Test
    void registerExit_WithInvalidAccessRecordId_ReturnsNotFound() throws Exception {
        ExitRequest request = new ExitRequest();
        request.setAccessRecordId(99999L);

        mockMvc.perform(post("/api/access/exit")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registro de acesso não encontrado"));
    }

    @Test
    void getAllAccessRecords_WithAdminRole_ReturnsAccessRecordsList() throws Exception {
        mockMvc.perform(get("/api/access")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getAllAccessRecords_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/access")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAccessRecordsByStudent_WithValidStudentId_ReturnsAccessRecords() throws Exception {
        mockMvc.perform(get("/api/access/student/" + testStudent.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].studentId").value(testStudent.getId()));
    }

    @Test
    void getAccessRecordsByStudent_WithInvalidStudentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/access/student/99999")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student not found"));
    }

    @Test
    void getAccessRecordsBySpace_WithValidSpaceId_ReturnsAccessRecords() throws Exception {
        mockMvc.perform(get("/api/access/space/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].spaceId").value(testSpace.getId()));
    }

    @Test
    void getAccessRecordsBySpace_WithInvalidSpaceId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/access/space/99999")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Space not found"));
    }

    @Test
    void getActiveAccessRecords_ReturnsOnlyActiveRecords() throws Exception {
        // Create an active access record
        AccessRecord activeAccess = AccessRecord.builder()
                .student(testStudent)
                .space(testSpace)
                .entryTime(LocalDateTime.now())
                .status(AccessStatus.ACTIVE)
                .build();
        accessRecordRepository.save(activeAccess);

        mockMvc.perform(get("/api/access/active")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[?(@.status=='ACTIVE')]").isNotEmpty());
    }

    @Test
    void registerEntry_WithoutToken_ReturnsUnauthorized() throws Exception {
        EntryRequest request = new EntryRequest();
        request.setStudentId(testStudent.getId());
        request.setSpaceId(testSpace.getId());

        mockMvc.perform(post("/api/access/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
