package com.copperleaf.asset_invesment_planner.controller;

import com.copperleaf.asset_invesment_planner.dto.*;
import com.copperleaf.asset_invesment_planner.service.AssetService;
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
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    @Autowired
    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping
    public ResponseEntity<StandardResponse> create(@RequestBody AssetCreateRequest dto){
//        return ResponseEntity.ok(assetService.create(dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        201, "Asset has been created", assetService.create(dto)
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssetUpdateRequest dto
            ){
//        return ResponseEntity.ok(assetService.update(id, dto));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Asset has been updated", assetService.update(id,dto)
                ),HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> get(@PathVariable Long id){
//        return ResponseEntity.ok(assetService.get(id));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Asset data has been fetched", assetService.get(id)
                ), HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse> delete(@PathVariable Long id){
        assetService.delete(id);
//        return ResponseEntity.noContent().build();
        return new ResponseEntity<>(
                new StandardResponse(
                        204, "Asset data has been deleted", null
                ), HttpStatus.NO_CONTENT
        );
    }

    @GetMapping(value = "/by-project/{projectCode}", produces = "application/json")
    public ResponseEntity<StandardResponse> listByProject(
            @PathVariable String projectCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ){
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && "desc".equalsIgnoreCase(s[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));
//        return ResponseEntity.ok(assetService.listByProject(projectCode, pageable));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Asset data has been fetched by project", assetService.listByProject(projectCode,pageable)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/summary/{projectCode}")
    public ResponseEntity<StandardResponse> listByProject(@PathVariable String projectCode){
//        return ResponseEntity.ok(assetService.summarize(projectCode));
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "Asset data has been fetched as a summary", assetService.summarize(projectCode)
                ), HttpStatus.OK
        );
    }
}
