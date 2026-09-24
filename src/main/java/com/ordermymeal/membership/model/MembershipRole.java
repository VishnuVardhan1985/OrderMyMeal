package com.ordermymeal.membership.model;

import com.ordermymeal.auth.model.Role;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "membership_roles", uniqueConstraints = @UniqueConstraint(name = "uq_membership_role", columnNames = {
        "membership_id", "role" }))
public class MembershipRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "membership_id", nullable = false)
    private Long membershipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MembershipRole() {
    }

    public Long getId() {
        return id;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}