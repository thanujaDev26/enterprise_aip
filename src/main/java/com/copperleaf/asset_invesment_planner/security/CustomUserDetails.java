package com.copperleaf.asset_invesment_planner.security;


import com.copperleaf.asset_invesment_planner.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String fullName;
    private final Long organizationId;
    private final boolean enabled;
    private final Set<String> roles;

    public CustomUserDetails(User u){
        this.id = u.getId();
        this.email = u.getEmail();
        this.password = u.getPassword();
        this.fullName = u.getFullName();
        this.organizationId = u.getOrganizationId();
        this.enabled = u.isEnabled();
        this.roles = u.getRoles();
    }

//    public Long getId(){
//        return id;
//    }
//
//    public String getFullName(){
//        return fullName;
//    }
//
//    public Long getOrganizationId(){
//        return organizationId;
//    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
        return true;
    }
}
