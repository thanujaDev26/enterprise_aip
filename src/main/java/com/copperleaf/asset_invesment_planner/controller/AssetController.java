package com.copperleaf.asset_invesment_planner.controller;

import com.copperleaf.asset_invesment_planner.dto.*;
import com.copperleaf.asset_invesment_planner.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<AssetResponse> create(@RequestBody AssetCreateRequest dto){
        return ResponseEntity.ok(assetService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetUpdateRequest dto
            ){
        return ResponseEntity.ok(assetService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(assetService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/by-project/{projectCode}", produces = "application/json")
    public ResponseEntity<PageResponse<AssetResponse>> listByProject(
            @PathVariable String projectCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ){
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));
        return ResponseEntity.ok(assetService.listByProject(projectCode, pageable));
    }

    @GetMapping("/summary/{projectCode}")
    public ResponseEntity<ProjectAssetsSummaryResponse> listByProject(@PathVariable String projectCode){
        return ResponseEntity.ok(assetService.summarize(projectCode));
    }
}
