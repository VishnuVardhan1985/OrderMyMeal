CREATE TABLE organizations (
    org_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(150),
    password_hash VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE organization_members (
    membership_id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    org_id BIGINT,

    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_membership_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    CONSTRAINT fk_membership_organization
        FOREIGN KEY (org_id)
        REFERENCES organizations(org_id),

    CONSTRAINT uq_user_organization
        UNIQUE (user_id, org_id)
);


CREATE TABLE one_time_codes (
    id UUID PRIMARY KEY,

    email VARCHAR(255) NOT NULL,

    user_id BIGINT NULL,

    purpose VARCHAR(30) NOT NULL,

    code_hash BYTEA NOT NULL,

    attempts INTEGER NOT NULL DEFAULT 0,

    expires_at TIMESTAMPTZ NOT NULL,

    consumed_at TIMESTAMPTZ NULL,

    requested_ip TEXT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_one_time_code_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    CONSTRAINT chk_one_time_codes_purpose
        CHECK (purpose IN ('SIGN_IN', 'EMAIL_CHANGE')),

    CONSTRAINT chk_one_time_codes_attempts
        CHECK (attempts >= 0)
);


CREATE INDEX idx_one_time_codes_email_purpose
    ON one_time_codes(email, purpose);

CREATE INDEX idx_one_time_codes_expires_at
    ON one_time_codes(expires_at);

CREATE INDEX idx_one_time_codes_requested_ip
    ON one_time_codes(requested_ip);


CREATE TABLE sessions (
    id UUID PRIMARY KEY,

    membership_id BIGINT NOT NULL,

    token_hash BYTEA NOT NULL UNIQUE,

    last_used_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    expires_at TIMESTAMPTZ NOT NULL,

    revoked_at TIMESTAMPTZ NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_session_membership
        FOREIGN KEY (membership_id)
        REFERENCES organization_members(membership_id)
);


CREATE INDEX idx_sessions_membership_id
    ON sessions(membership_id);

CREATE INDEX idx_sessions_expires_at
    ON sessions(expires_at);
