package com.ordermymeal.membership.repository;

import com.ordermymeal.membership.model.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository
        extends JpaRepository<OrganizationMember, Long> {

    List<OrganizationMember> findByUserUserId(Long userId);

    List<OrganizationMember> findByUserUserIdAndStatusOrderByMembershipIdAsc(
            Long userId,
            String status);

    Optional<OrganizationMember> findByUserUserIdAndStatus(
            Long userId,
            String status);
}
