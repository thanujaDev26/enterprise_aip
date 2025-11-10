package com.copperleaf.asset_invesment_planner.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private String[] roles;
}
