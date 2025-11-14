package com.copperleaf.asset_invesment_planner.controller;


import com.copperleaf.asset_invesment_planner.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    @GetMapping
    public ResponseEntity<StandardResponse> me(@AuthenticationPrincipal UserDetails user){
        return new ResponseEntity<>(
                new StandardResponse(
                        200, "User details has been fetched", user.getUsername()
                ), HttpStatus.OK
        );
    }
}
