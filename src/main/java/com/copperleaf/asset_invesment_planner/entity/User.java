package com.copperleaf.asset_invesment_planner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="users", indexes = {
        @Index(name="ix_user_email", columnList="email", unique=true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=160)
    private String email;

    @Column(nullable=false, length=200)
    private String password;

    @Column(nullable=false, length=160)
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column(name="role")
    private Set<String> roles;


    @Column(nullable=false)
    private Long organizationId = 1L;

    @Column(nullable=false)
    private boolean enabled = true;

}
