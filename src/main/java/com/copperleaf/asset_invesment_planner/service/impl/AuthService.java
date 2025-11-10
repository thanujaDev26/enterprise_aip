package com.copperleaf.asset_invesment_planner.service.impl;


import com.copperleaf.asset_invesment_planner.dto.UserAuthResponse;
import com.copperleaf.asset_invesment_planner.dto.UserLoginRequest;
import com.copperleaf.asset_invesment_planner.dto.UserRegisterRequest;
import com.copperleaf.asset_invesment_planner.entity.User;
import com.copperleaf.asset_invesment_planner.repository.UserRepository;
import com.copperleaf.asset_invesment_planner.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt){
        this.userRepository = users;
        this.passwordEncoder = encoder;
        this.authenticationManager = authManager;
        this.jwtService = jwt;
    }

    @Transactional
    public User register(UserRegisterRequest dto){
        if(userRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("This Email already registered " + dto.getEmail());

        var user = User.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(Set.of("ANALYST"))
                .organizationId(1L)
                .enabled(true).build();
        userRepository.save(user);

        return user;
    }

    public UserAuthResponse login(UserLoginRequest dto){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        var principal = auth.getName();
        var users = userRepository.findByEmail(principal).orElseThrow();
        var token = jwtService.generateToken(dto.getEmail(), Map.of("roles", users.getRoles(),
                "organizationId", users.getOrganizationId(), "name" , users.getEmail()));

        var response =  new UserAuthResponse();
        response.setAccessToken(token);
        response.setEmail(users.getEmail());
        response.setFullName(users.getFullName());
        response.setRoles(users.getRoles().toArray(String[]::new));
        return response;

    }

//    public UserAuthResponse login(UserLoginRequest dto){
//        try {
//            Authentication auth = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
//            );
//            var principal = auth.getName();
//            var users = userRepository.findByEmail(principal).orElseThrow();
//            var token = jwtService.generateToken(dto.getEmail(),
//                    Map.of("roles", users.getRoles(),
//                            "organizationId", users.getOrganizationId(),
//                            "name", users.getEmail()));
//
//            var response = new UserAuthResponse();
//            response.setAccessToken(token);
//            response.setEmail(users.getEmail());
//            response.setFullName(users.getFullName());
//            response.setRoles(users.getRoles().toArray(String[]::new));
//            return response;
//
//        } catch (AuthenticationException ex) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
//        }
//    }
}
