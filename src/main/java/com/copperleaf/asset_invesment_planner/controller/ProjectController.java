package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.dto.PageResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectCreateRequest;
import com.copperleaf.asset_invesment_planner.dto.ProjectResponse;
import com.copperleaf.asset_invesment_planner.dto.ProjectUpdateRequest;
import com.copperleaf.asset_invesment_planner.entity.enums.ProjectStatus;
import com.copperleaf.asset_invesment_planner.service.ProjectService;
import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse> create(@Valid @RequestBody ProjectCreateRequest dto) {
//        return ResponseEntity.ok(projectService.create(dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        201, "Project has been created", projectService.create(dto)
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/{code}")
    public ResponseEntity<StandardResponse> update(@PathVariable String code, @Valid @RequestBody ProjectUpdateRequest dto) {
//        return ResponseEntity.ok(projectService.update(code, dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Project has been updated", projectService.update(code, dto)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<StandardResponse> get(@PathVariable String code) {
//        return ResponseEntity.ok(projectService.getByCode(code));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Project has been fetched", projectService.getByCode(code)
                ), HttpStatus.OK
        );
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<StandardResponse>  delete(@PathVariable String code) {
        projectService.deleteByCOde(code);
//        return ResponseEntity.noContent().build();
        return new ResponseEntity<>(
                new StandardResponse(
                        204, "Project has been removed", null
                ),HttpStatus.NO_CONTENT
        );
    }

    @GetMapping
    public ResponseEntity<StandardResponse> search(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name, asc") String sort
            ){
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));
//        return ResponseEntity.ok(projectService.search(status, pageable));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Search result has been fetched", projectService.search(status, pageable)
                ), HttpStatus.OK
        );
    }
}
