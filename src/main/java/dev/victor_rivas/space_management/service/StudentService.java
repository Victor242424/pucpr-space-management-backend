package dev.victor_rivas.space_management.service;

import dev.victor_rivas.space_management.constant.ExceptionMessagesConstants;
import dev.victor_rivas.space_management.enums.Role;
import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.exception.BusinessException;
import dev.victor_rivas.space_management.exception.ResourceNotFoundException;
import dev.victor_rivas.space_management.model.dto.CreateStudentRequest;
import dev.victor_rivas.space_management.model.dto.StudentDTO;
import dev.victor_rivas.space_management.model.entity.Student;
import dev.victor_rivas.space_management.model.entity.User;
import dev.victor_rivas.space_management.repository.AccessRecordRepository;
import dev.victor_rivas.space_management.repository.StudentRepository;
import dev.victor_rivas.space_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessRecordRepository accessRecordRepository;

    @Transactional
    public StudentDTO createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BusinessException("Número de matrícula já existe");
        }

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já existe");
        }

        Student student = Student.builder()
                .registrationNumber(request.getRegistrationNumber())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .status(StudentStatus.ACTIVE)
                .build();

        student = studentRepository.save(student);

        // Create user account for authentication
        User user = User.builder()
                .username(request.getRegistrationNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .student(student)
                .enabled(true)
                .build();

        userRepository.save(user);

        return convertToDTO(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessagesConstants.STUDENT_NOT_FOUND + id)
                );
        return convertToDTO(student);
    }

    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessagesConstants.STUDENT_NOT_FOUND + id)
                );

        if (!student.getEmail().equals(studentDTO.getEmail()) &&
                studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new BusinessException("Email já existe");
        }

        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setPhoneNumber(studentDTO.getPhoneNumber());

        if (studentDTO.getStatus() != null) {
            student.setStatus(studentDTO.getStatus());
        }

        student = studentRepository.save(student);
        return convertToDTO(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado com id: " + id));
        if (accessRecordRepository.existsByStudentId(id)){
            student.setStatus(StudentStatus.INACTIVE);
            studentRepository.save(student);
        } else {
            userRepository.deleteByStudentId(id);
            if (!userRepository.existsByStudentId(id)){
                studentRepository.deleteById(id);
            } else {
                throw new ResourceNotFoundException("Usuário não encontrado com id de estudante: " + id);
            }
        }
    }

    private StudentDTO convertToDTO(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .registrationNumber(student.getRegistrationNumber())
                .name(student.getName())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt().toString())
                .updatedAt(student.getUpdatedAt().toString())
                .build();
    }
}