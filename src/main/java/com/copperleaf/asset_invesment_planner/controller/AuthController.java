package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.dto.UserLoginRequest;
import com.copperleaf.asset_invesment_planner.dto.UserRegisterRequest;
import com.copperleaf.asset_invesment_planner.service.impl.AuthService;
import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<StandardResponse> userRegister(@Valid @RequestBody UserRegisterRequest dto){
        return new ResponseEntity<>(
                new StandardResponse(
                        201, "User has been created", authService.register(dto)
                ), HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponse> userLogin(@Valid @RequestBody UserLoginRequest dto){

        return new ResponseEntity<>(
                new StandardResponse(
                        200, "User has been logged", authService.login(dto)
                ), HttpStatus.OK
        );
    }
}
