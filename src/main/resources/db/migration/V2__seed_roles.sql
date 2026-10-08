INSERT INTO roles (role_name, description)
VALUES
    ('ADMIN', 'Administrator'),
    ('SALES_MANAGER', 'Sales Manager'),
    ('SALES_EXECUTIVE', 'Sales Executive')
    ON CONFLICT (role_name) DO NOTHING;