package com.ordermymeal.auth.dto;

import com.ordermymeal.auth.model.Role;

import java.util.List;

public record LoginResponse(
                String message,
                Long membershipId,
                Long organizationId,
                List<Role> roles) {
}
