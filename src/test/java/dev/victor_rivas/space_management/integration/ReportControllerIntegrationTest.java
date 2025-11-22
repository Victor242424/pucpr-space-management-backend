package dev.victor_rivas.space_management.integration;

import dev.victor_rivas.space_management.enums.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReportControllerIntegrationTest {

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

    private String adminToken;
    private String studentToken;
    private Student testStudent;
    private Space testSpace;

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

        // Create access records for testing reports
        createAccessRecordsForTesting();
    }

    private void createAccessRecordsForTesting() {
        // Create active access record
        AccessRecord activeAccess = AccessRecord.builder()
                .student(testStudent)
                .space(testSpace)
                .entryTime(LocalDateTime.now().minusMinutes(30))
                .status(AccessStatus.ACTIVE)
                .build();
        accessRecordRepository.save(activeAccess);

        // Create completed access records - today
        for (int i = 0; i < 3; i++) {
            AccessRecord completedToday = AccessRecord.builder()
                    .student(testStudent)
                    .space(testSpace)
                    .entryTime(LocalDateTime.now().minusHours(i + 2))
                    .exitTime(LocalDateTime.now().minusHours(i + 1))
                    .status(AccessStatus.COMPLETED)
                    .build();
            accessRecordRepository.save(completedToday);
        }

        // Create completed access records - this week
        for (int i = 0; i < 2; i++) {
            AccessRecord completedWeek = AccessRecord.builder()
                    .student(testStudent)
                    .space(testSpace)
                    .entryTime(LocalDateTime.now().minusDays(i + 2).minusHours(2))
                    .exitTime(LocalDateTime.now().minusDays(i + 2).minusHours(1))
                    .status(AccessStatus.COMPLETED)
                    .build();
            accessRecordRepository.save(completedWeek);
        }

        // Create completed access records - this month
        for (int i = 0; i < 2; i++) {
            AccessRecord completedMonth = AccessRecord.builder()
                    .student(testStudent)
                    .space(testSpace)
                    .entryTime(LocalDateTime.now().minusDays(i + 10).minusHours(3))
                    .exitTime(LocalDateTime.now().minusDays(i + 10).minusHours(2))
                    .status(AccessStatus.COMPLETED)
                    .build();
            accessRecordRepository.save(completedMonth);
        }
    }

    @Test
    void getOccupancyReport_WithValidToken_ReturnsReportForAllSpaces() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].spaceId").value(testSpace.getId()))
                .andExpect(jsonPath("$.data[0].spaceName").value("Computer Laboratory"))
                .andExpect(jsonPath("$.data[0].spaceCode").value("LAB-001"))
                .andExpect(jsonPath("$.data[0].capacity").value(30))
                .andExpect(jsonPath("$.data[0].currentOccupancy").isNumber())
                .andExpect(jsonPath("$.data[0].occupancyRate").isNumber())
                .andExpect(jsonPath("$.data[0].totalAccessesToday").isNumber())
                .andExpect(jsonPath("$.data[0].totalAccessesThisWeek").isNumber())
                .andExpect(jsonPath("$.data[0].totalAccessesThisMonth").isNumber())
                .andExpect(jsonPath("$.data[0].averageDurationInMinutes").isNumber());
    }

    @Test
    void getOccupancyReport_WithAdminToken_ReturnsReportForAllSpaces() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getOccupancyReport_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getOccupancyReportBySpace_WithValidSpaceId_ReturnsSpaceReport() throws Exception {
        // Set explicit timezone for test
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        mockMvc.perform(get("/api/reports/occupancy/space/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.spaceId").value(testSpace.getId()))
                .andExpect(jsonPath("$.data.spaceName").value("Computer Laboratory"))
                .andExpect(jsonPath("$.data.spaceCode").value("LAB-001"))
                .andExpect(jsonPath("$.data.capacity").value(30))
                .andExpect(jsonPath("$.data.currentOccupancy").isNumber())
                .andExpect(jsonPath("$.data.occupancyRate").isNumber())
                .andExpect(jsonPath("$.data.totalAccessesToday").value(4)) // 1 active + 3 completed today
                .andExpect(jsonPath("$.data.totalAccessesThisWeek").value(6)) // 1 active + 3 today + 2 this week
                .andExpect(jsonPath("$.data.totalAccessesThisMonth").value(8)) // All records
                .andExpect(jsonPath("$.data.averageDurationInMinutes").isNumber());
    }

    @Test
    void getOccupancyReportBySpace_WithInvalidSpaceId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy/space/99999")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Space not found"));
    }

    @Test
    void getOccupancyReportBySpace_WithAdminToken_ReturnsSpaceReport() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy/space/" + testSpace.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.spaceId").value(testSpace.getId()));
    }

    @Test
    void getOccupancyReport_WithMultipleSpaces_ReturnsAllSpacesReports() throws Exception {
        // Create another space
        Space anotherSpace = Space.builder()
                .code("CLASS-001")
                .name("Classroom 101")
                .type(SpaceType.CLASSROOM)
                .capacity(40)
                .building("Building B")
                .floor("2nd Floor")
                .status(SpaceStatus.AVAILABLE)
                .build();
        spaceRepository.save(anotherSpace);

        mockMvc.perform(get("/api/reports/occupancy")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getOccupancyReportBySpace_WithNoAccessRecords_ReturnsZeroStats() throws Exception {
        // Create a space without access records
        Space emptySpace = Space.builder()
                .code("EMPTY-001")
                .name("Empty Room")
                .type(SpaceType.STUDY_ROOM)
                .capacity(10)
                .status(SpaceStatus.AVAILABLE)
                .build();
        emptySpace = spaceRepository.save(emptySpace);

        mockMvc.perform(get("/api/reports/occupancy/space/" + emptySpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.spaceId").value(emptySpace.getId()))
                .andExpect(jsonPath("$.data.currentOccupancy").value(0))
                .andExpect(jsonPath("$.data.occupancyRate").value(0.0))
                .andExpect(jsonPath("$.data.totalAccessesToday").value(0))
                .andExpect(jsonPath("$.data.totalAccessesThisWeek").value(0))
                .andExpect(jsonPath("$.data.totalAccessesThisMonth").value(0))
                .andExpect(jsonPath("$.data.averageDurationInMinutes").value(0.0));
    }

    @Test
    void getOccupancyReportBySpace_CalculatesOccupancyRateCorrectly() throws Exception {
        // Current occupancy is 1 (one active record), capacity is 30
        // Expected occupancy rate: (1/30) * 100 = 3.33%
        mockMvc.perform(get("/api/reports/occupancy/space/" + testSpace.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentOccupancy").value(1))
                .andExpect(jsonPath("$.data.occupancyRate").value(3.33));
    }

    @Test
    void getOccupancyReportBySpace_WithoutToken_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/reports/occupancy/space/" + testSpace.getId()))
                .andExpect(status().isUnauthorized());
    }
}
