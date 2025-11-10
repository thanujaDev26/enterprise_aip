package com.copperleaf.asset_invesment_planner.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min=8,max=100)
    private String password;

    @NotBlank
    @Size(max=160)
    private String fullName;
}
