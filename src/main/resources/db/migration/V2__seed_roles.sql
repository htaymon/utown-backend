-- Reference roles required by registration and role-based access control.
INSERT INTO roles (name, created_at, updated_at) VALUES
    ('CLIENT', NOW(6), NOW(6)),
    ('RESTAURANT_ADMIN', NOW(6), NOW(6)),
    ('ADMIN', NOW(6), NOW(6));
