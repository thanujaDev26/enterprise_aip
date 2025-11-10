package com.copperleaf.asset_invesment_planner.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginRequest {

    @Email
    @NotBlank
    private String email;


    @NotBlank
    private String password;

}
