
CREATE TABLE roles (
                       role_id BIGSERIAL PRIMARY KEY,
                       role_name VARCHAR(255) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE permissions (
                             permission_id BIGSERIAL PRIMARY KEY,
                             permission_name VARCHAR(255) NOT NULL UNIQUE,
                             description VARCHAR(255)
);

CREATE TABLE role_permissions (
                                  role_permission_id BIGSERIAL PRIMARY KEY,
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

CREATE TABLE users (
                       uid BIGINT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP,
                       role_id BIGINT NOT NULL
);

CREATE TABLE "Accounts" (
                            acc_id SERIAL PRIMARY KEY,
                            account_name VARCHAR(255) NOT NULL,
                            industry VARCHAR(255) NOT NULL,
                            phone_number VARCHAR(255),
                            "Website" VARCHAR(255),
                            created_at TIMESTAMP,
                            updated_at TIMESTAMP,
                            acc_owner_id BIGINT NOT NULL
);

CREATE TABLE "Contact" (
                           contact_id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL UNIQUE,
                           "LifecycleStatus" VARCHAR(255) NOT NULL,
                           phone_number VARCHAR(255),
                           "Job Title" VARCHAR(255),
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP,
                           contact_owner_id BIGINT NOT NULL,
                           account_id INTEGER NOT NULL
);

CREATE TABLE "Leads" (
                         lead_id SERIAL PRIMARY KEY,
                         "Salutation" VARCHAR(255),
                         name VARCHAR(255) NOT NULL,
                         status VARCHAR(255) NOT NULL DEFAULT 'New',
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone_number VARCHAR(255),
                         company_name VARCHAR(255),
                         source VARCHAR(255) DEFAULT 'Website',
                         notes VARCHAR(255),
                         lead_rating VARCHAR(255),
                         expected_value NUMERIC(19,2),
                         owner_id BIGINT NOT NULL
);

CREATE TABLE "DealStage" (
                             deal_stage_id SERIAL PRIMARY KEY,
                             "Stage" VARCHAR(255) NOT NULL,
                             display_order INTEGER NOT NULL,
                             probability INTEGER NOT NULL,
                             is_active BOOLEAN NOT NULL
);

CREATE TABLE "Deal" (
                        deal_id SERIAL PRIMARY KEY,
                        "Title" VARCHAR(255) NOT NULL,
                        "DealStatus" VARCHAR(255) NOT NULL DEFAULT 'open',
                        expected_close_date TIMESTAMP,
                        "Value" NUMERIC(19,2),
                        "Closing Note" VARCHAR(255),
                        created_at TIMESTAMP,
                        updated_at TIMESTAMP,
                        deal_owner_id BIGINT NOT NULL,
                        deal_stage_id INTEGER NOT NULL,
                        account_id INTEGER NOT NULL,
                        contact_id INTEGER NOT NULL
);

CREATE TABLE "Activity" (
                            id SERIAL PRIMARY KEY,
                            "ActivityType" VARCHAR(255) NOT NULL,
                            "Subject" VARCHAR(255) NOT NULL,
                            "Status" VARCHAR(255) NOT NULL,
                            due_at TIMESTAMP,
                            completed_at TIMESTAMP,
                            created_at TIMESTAMP,
                            deal_id INTEGER,
                            lead_id INTEGER,
                            created_by BIGINT NOT NULL
);

CREATE TABLE "DealStage_History" (
                                     history_id SERIAL PRIMARY KEY,
                                     from_stage VARCHAR(255) NOT NULL,
                                     to_stage VARCHAR(255) NOT NULL,
                                     changed_at TIMESTAMP,
                                     "Changed_By" BIGINT NOT NULL,
                                     deal_id INTEGER NOT NULL
);

CREATE TABLE "Notification" (
                                notification_id SERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                "Notification_Type" VARCHAR(255),
                                message VARCHAR(500) NOT NULL,
                                "NotificationStatus" VARCHAR(255) NOT NULL,
                                "created at" TIMESTAMP NOT NULL,
                                "sent at" TIMESTAMP NOT NULL
);

CREATE TABLE audit_logs (
                            audit_id SERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            action VARCHAR(50) NOT NULL,
                            entity_type VARCHAR(50) NOT NULL,
                            entity_id VARCHAR(50),
                            created_at TIMESTAMP NOT NULL
);




ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
            REFERENCES roles(role_id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
            REFERENCES permissions(permission_id);

ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
            REFERENCES roles(role_id);

ALTER TABLE "Accounts"
    ADD CONSTRAINT fk_accounts_owner
        FOREIGN KEY (acc_owner_id)
            REFERENCES users(uid);

ALTER TABLE "Contact"
    ADD CONSTRAINT fk_contact_owner
        FOREIGN KEY (contact_owner_id)
            REFERENCES users(uid);

ALTER TABLE "Contact"
    ADD CONSTRAINT fk_contact_account
        FOREIGN KEY (account_id)
            REFERENCES "Accounts"(acc_id);

ALTER TABLE "Leads"
    ADD CONSTRAINT fk_leads_owner
        FOREIGN KEY (owner_id)
            REFERENCES users(uid);

ALTER TABLE "Deal"
    ADD CONSTRAINT fk_deal_owner
        FOREIGN KEY (deal_owner_id)
            REFERENCES users(uid);

ALTER TABLE "Deal"
    ADD CONSTRAINT fk_deal_stage
        FOREIGN KEY (deal_stage_id)
            REFERENCES "DealStage"(deal_stage_id);

ALTER TABLE "Deal"
    ADD CONSTRAINT fk_deal_account
        FOREIGN KEY (account_id)
            REFERENCES "Accounts"(acc_id);

ALTER TABLE "Deal"
    ADD CONSTRAINT fk_deal_contact
        FOREIGN KEY (contact_id)
            REFERENCES "Contact"(contact_id);

ALTER TABLE "Activity"
    ADD CONSTRAINT fk_activity_deal
        FOREIGN KEY (deal_id)
            REFERENCES "Deal"(deal_id);

ALTER TABLE "Activity"
    ADD CONSTRAINT fk_activity_lead
        FOREIGN KEY (lead_id)
            REFERENCES "Leads"(lead_id);

ALTER TABLE "Activity"
    ADD CONSTRAINT fk_activity_user
        FOREIGN KEY (created_by)
            REFERENCES users(uid);

ALTER TABLE "DealStage_History"
    ADD CONSTRAINT fk_stage_history_user
        FOREIGN KEY ("Changed_By")
            REFERENCES users(uid);

ALTER TABLE "DealStage_History"
    ADD CONSTRAINT fk_stage_history_deal
        FOREIGN KEY (deal_id)
            REFERENCES "Deal"(deal_id);

ALTER TABLE "Notification"
    ADD CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users(uid);

ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
            REFERENCES users(uid);



CREATE INDEX idx_role_permissions_role_id
    ON role_permissions(role_id);

CREATE INDEX idx_role_permissions_permission_id
    ON role_permissions(permission_id);

CREATE INDEX idx_users_role_id
    ON users(role_id);

CREATE INDEX idx_accounts_owner_id
    ON "Accounts"(acc_owner_id);

CREATE INDEX idx_contacts_owner_id
    ON "Contact"(contact_owner_id);

CREATE INDEX idx_contacts_account_id
    ON "Contact"(account_id);

CREATE INDEX idx_leads_owner_id
    ON "Leads"(owner_id);

CREATE INDEX idx_deals_owner_id
    ON "Deal"(deal_owner_id);

CREATE INDEX idx_deals_stage_id
    ON "Deal"(deal_stage_id);

CREATE INDEX idx_deals_account_id
    ON "Deal"(account_id);

CREATE INDEX idx_deals_contact_id
    ON "Deal"(contact_id);

CREATE INDEX idx_activities_deal_id
    ON "Activity"(deal_id);

CREATE INDEX idx_activities_lead_id
    ON "Activity"(lead_id);

CREATE INDEX idx_activities_created_by
    ON "Activity"(created_by);

CREATE INDEX idx_deal_stage_history_changed_by
    ON "DealStage_History"("Changed_By");

CREATE INDEX idx_deal_stage_history_deal_id
    ON "DealStage_History"(deal_id);

CREATE INDEX idx_notifications_user_id
    ON "Notification"(user_id);

CREATE INDEX idx_audit_logs_user_id
    ON audit_logs(user_id);




