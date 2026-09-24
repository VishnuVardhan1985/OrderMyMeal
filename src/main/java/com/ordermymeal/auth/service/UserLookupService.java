package com.ordermymeal.auth.service;

import com.ordermymeal.auth.model.User;
import com.ordermymeal.auth.repository.UserRepository;
import com.ordermymeal.membership.model.OrganizationMember;
import com.ordermymeal.membership.repository.OrganizationMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLookupService {

    private final UserRepository userRepository;
    private final OrganizationMemberRepository membershipRepository;

    public UserLookupService(
            UserRepository userRepository,
            OrganizationMemberRepository membershipRepository) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<OrganizationMember> findMemberships(Long userId) {
        return membershipRepository.findByUserUserId(userId);
    }
}