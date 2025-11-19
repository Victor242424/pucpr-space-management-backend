package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.AccessRecordDTO;
import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.EntryRequest;
import dev.victor_rivas.space_management.model.dto.ExitRequest;
import dev.victor_rivas.space_management.service.AccessRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessRecordController {

    private final AccessRecordService accessRecordService;

    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerEntry(
            @Valid @RequestBody EntryRequest request) {
        AccessRecordDTO accessRecord = accessRecordService.registerEntry(request);
        return ResponseEntity.ok(ApiResponse.success("Entry registered successfully", accessRecord));
    }

    @PostMapping("/exit")
    public ResponseEntity<ApiResponse<AccessRecordDTO>> registerExit(
            @Valid @RequestBody ExitRequest request) {
        AccessRecordDTO accessRecord = accessRecordService.registerExit(request);
        return ResponseEntity.ok(ApiResponse.success("Exit registered successfully", accessRecord));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAllAccessRecords() {
        List<AccessRecordDTO> records = accessRecordService.getAllAccessRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsByStudent(
            @PathVariable Long studentId) {
        List<AccessRecordDTO> records = accessRecordService.getAccessRecordsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getAccessRecordsBySpace(
            @PathVariable Long spaceId) {
        List<AccessRecordDTO> records = accessRecordService.getAccessRecordsBySpace(spaceId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AccessRecordDTO>>> getActiveAccessRecords() {
        List<AccessRecordDTO> records = accessRecordService.getActiveAccessRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}

