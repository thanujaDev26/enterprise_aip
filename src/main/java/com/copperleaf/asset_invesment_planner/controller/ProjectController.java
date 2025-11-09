package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.dto.PageResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.Project;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import com.copperleaf.asset_invesment_planner.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProjectCreateRequest dto) {
        return ResponseEntity.ok(projectService.create(dto));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ProjectResponse> update(@PathVariable String code, @Valid @RequestBody ProjectUpdateRequest dto) {
        return ResponseEntity.ok(projectService.update(code, dto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProjectResponse> get(@PathVariable String code) {
        return ResponseEntity.ok(projectService.getByCode(code));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void>  delete(@PathVariable String code) {
        projectService.deleteByCOde(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProjectResponse>> search(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name, asc") String sort
            ){
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));
        return ResponseEntity.ok(projectService.search(status, pageable));
    }
}
