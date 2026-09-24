-- OrderMyMeal authentication extension:
-- 1. Keep the existing users.password_hash column for email + password login.
-- 2. Move membership roles into a separate table so one membership can have multiple roles.
-- 3. Existing single roles are copied into the new table.

CREATE TABLE membership_roles (
    id BIGSERIAL PRIMARY KEY,
    membership_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_membership_roles_membership
        FOREIGN KEY (membership_id)
        REFERENCES organization_members(membership_id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_membership_role
        UNIQUE (membership_id, role)
);

INSERT INTO membership_roles (membership_id, role)
SELECT membership_id, role
FROM organization_members
WHERE role IS NOT NULL;

CREATE INDEX idx_membership_roles_membership_id
    ON membership_roles(membership_id);

ALTER TABLE organization_members
    DROP COLUMN role;
