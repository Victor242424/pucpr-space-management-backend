package dev.victor_rivas.space_management.controller;

import dev.victor_rivas.space_management.model.dto.ApiResponse;
import dev.victor_rivas.space_management.model.dto.SpaceDTO;
import dev.victor_rivas.space_management.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpaceDTO>>> getAllSpaces() {
        List<SpaceDTO> spaces = spaceService.getAllSpaces();
        return ResponseEntity.ok(ApiResponse.success(spaces));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpaceDTO>> getSpaceById(@PathVariable Long id) {
        SpaceDTO space = spaceService.getSpaceById(id);
        return ResponseEntity.ok(ApiResponse.success(space));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> createSpace(@Valid @RequestBody SpaceDTO spaceDTO) {
        SpaceDTO created = spaceService.createSpace(spaceDTO);
        return ResponseEntity.ok(ApiResponse.success("Space created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SpaceDTO>> updateSpace(
            @PathVariable Long id,
            @Valid @RequestBody SpaceDTO spaceDTO) {
        SpaceDTO updated = spaceService.updateSpace(id, spaceDTO);
        return ResponseEntity.ok(ApiResponse.success("Space updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.ok(ApiResponse.success("Space deleted successfully", null));
    }
}