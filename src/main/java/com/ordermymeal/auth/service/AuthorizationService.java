package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.Permission;
import com.ordermymeal.auth.model.Role;
import com.ordermymeal.auth.model.RolePermissions;
import com.ordermymeal.membership.model.MembershipRole;
import com.ordermymeal.membership.repository.MembershipRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthorizationService {

    private final MembershipRoleRepository membershipRoleRepository;

    public AuthorizationService(
            MembershipRoleRepository membershipRoleRepository) {
        this.membershipRoleRepository = membershipRoleRepository;
    }

    public Set<Role> getRoles(Long membershipId) {

        return membershipRoleRepository
                .findByMembershipId(membershipId)
                .stream()
                .map(MembershipRole::getRole)
                .collect(java.util.stream.Collectors.toSet());
    }

    public Set<Permission> getPermissions(Long membershipId) {

        Set<Role> roles = getRoles(membershipId);

        return roles.stream()
                .flatMap(role -> RolePermissions.getPermissions(role).stream())
                .collect(java.util.stream.Collectors.toSet());
    }

    public boolean hasPermission(
            Long membershipId,
            Permission permission) {

        Set<Role> roles = getRoles(membershipId);

        return RolePermissions.hasPermission(
                roles,
                permission);
    }

    public void requirePermission(
            Long membershipId,
            Permission permission) {

        if (!hasPermission(membershipId, permission)) {
            throw new AuthorizationException(
                    "You do not have permission to perform this action.");
        }
    }
}