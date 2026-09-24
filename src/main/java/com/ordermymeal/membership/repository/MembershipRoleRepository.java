package com.ordermymeal.membership.repository;

import com.ordermymeal.membership.model.MembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRoleRepository extends JpaRepository<MembershipRole, Long> {

    List<MembershipRole> findByMembershipId(Long membershipId);
}
