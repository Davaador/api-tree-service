-- Performance optimization indexes
-- This migration adds indexes for better query performance

-- Customer table indexes
CREATE INDEX IF NOT EXISTS idx_customer_register ON family.customer(register);
CREATE INDEX IF NOT EXISTS idx_customer_email ON family.customer(email);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON family.customer("phoneNumber");
CREATE INDEX IF NOT EXISTS idx_customer_gender ON family.customer(gender);
CREATE INDEX IF NOT EXISTS idx_customer_is_parent ON family.customer("isParent");
CREATE INDEX IF NOT EXISTS idx_customer_modified_date ON family.customer("modifiedDate");
CREATE INDEX IF NOT EXISTS idx_customer_deceased ON family.customer("isDeceased");

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_user_phone_number ON family."user"("phoneNumber");
CREATE INDEX IF NOT EXISTS idx_user_account_non_expired ON family."user"("accountNonExpired");
CREATE INDEX IF NOT EXISTS idx_user_account_non_locked ON family."user"("accountNonLocked");
CREATE INDEX IF NOT EXISTS idx_user_credentials_non_expired ON family."user"("credentialsNonExpired");
CREATE INDEX IF NOT EXISTS idx_user_enabled ON family."user"(enabled);

-- Role table indexes
CREATE INDEX IF NOT EXISTS idx_role_name ON family.role(name);

-- RoleUsers table indexes
CREATE INDEX IF NOT EXISTS idx_role_users_phone_number ON family.role_users("phoneNumber");
CREATE INDEX IF NOT EXISTS idx_role_users_role_id ON family.role_users(role_id);

-- CustomerOtp table indexes
CREATE INDEX IF NOT EXISTS idx_customer_otp_phone_number ON family.customer_otp("phoneNumber");
CREATE INDEX IF NOT EXISTS idx_customer_otp_created_date ON family.customer_otp("createdDate");

-- Image table indexes
CREATE INDEX IF NOT EXISTS idx_image_customer_id ON family.image(customer_id);

-- AdminCustomer table indexes
CREATE INDEX IF NOT EXISTS idx_admin_customer_customer_id ON family.admin_customer(customer_id);
CREATE INDEX IF NOT EXISTS idx_admin_customer_created_date ON family.admin_customer("createdDate");

-- Biography table indexes
CREATE INDEX IF NOT EXISTS idx_biography_customer_id ON family.biography(customer_id);
CREATE INDEX IF NOT EXISTS idx_biography_created_date ON family.biography("createdDate");
