-- Optional demo data so the API is immediately explorable after a fresh setup.
-- All demo accounts share the password: Demo123!
-- This is local/demo-only data, not a production secret.

INSERT INTO users (name, email, password, phone_number, role_id, created_at, updated_at) VALUES
    ('Admin User', 'admin@utown.dev', '$2a$10$jsevDtxp4s8Wn5gvp/R9gO2J6ksBqYujSnSXIXyFupj19NIWr2sYy', '0900000001', (SELECT id FROM roles WHERE name = 'ADMIN'), NOW(6), NOW(6)),
    ('Seoul BBQ Owner', 'owner@utown.dev', '$2a$10$jsevDtxp4s8Wn5gvp/R9gO2J6ksBqYujSnSXIXyFupj19NIWr2sYy', '0900000002', (SELECT id FROM roles WHERE name = 'RESTAURANT_ADMIN'), NOW(6), NOW(6)),
    ('Jane Student', 'student@utown.dev', '$2a$10$jsevDtxp4s8Wn5gvp/R9gO2J6ksBqYujSnSXIXyFupj19NIWr2sYy', '0900000003', (SELECT id FROM roles WHERE name = 'CLIENT'), NOW(6), NOW(6));

INSERT INTO restaurant_categories (name, image_url, priority, created_at, updated_at) VALUES
    ('Korean Food', 'https://example.com/categories/korean.jpg', 1, NOW(6), NOW(6)),
    ('Fast Food', 'https://example.com/categories/fastfood.jpg', 2, NOW(6), NOW(6));

INSERT INTO dish_categories (name, image_url, priority, created_at, updated_at) VALUES
    ('Main Course', 'https://example.com/categories/main.jpg', 1, NOW(6), NOW(6)),
    ('Side Dish', 'https://example.com/categories/side.jpg', 2, NOW(6), NOW(6));

INSERT INTO restaurants (user_id, restaurant_category_id, name, description, image_url, minimum_order, status, created_at, updated_at) VALUES
    ((SELECT id FROM users WHERE email = 'owner@utown.dev'),
     (SELECT id FROM restaurant_categories WHERE name = 'Korean Food'),
     'Seoul BBQ', 'Authentic Korean BBQ near campus', 'https://example.com/restaurants/seoul-bbq.jpg',
     10.00, 'OPEN', NOW(6), NOW(6));

INSERT INTO dishes (restaurant_id, dish_category_id, name, description, price, image, status, priority, created_at, updated_at) VALUES
    ((SELECT id FROM restaurants WHERE name = 'Seoul BBQ'),
     (SELECT id FROM dish_categories WHERE name = 'Main Course'),
     'Bulgogi Bowl', 'Marinated beef bulgogi over rice', 12.99, 'https://example.com/dishes/bulgogi.jpg', 'AVAILABLE', 1, NOW(6), NOW(6)),
    ((SELECT id FROM restaurants WHERE name = 'Seoul BBQ'),
     (SELECT id FROM dish_categories WHERE name = 'Side Dish'),
     'Kimchi', 'Traditional fermented cabbage', 3.50, 'https://example.com/dishes/kimchi.jpg', 'AVAILABLE', 1, NOW(6), NOW(6));

INSERT INTO delivery_areas (restaurant_id, city, name, created_at, updated_at) VALUES
    ((SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'Seoul', 'Campus Zone A', NOW(6), NOW(6));

INSERT INTO work_schedules (restaurant_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
    ((SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'MONDAY', '09:00:00', '21:00:00', NOW(6), NOW(6)),
    ((SELECT id FROM restaurants WHERE name = 'Seoul BBQ'), 'TUESDAY', '09:00:00', '21:00:00', NOW(6), NOW(6));

INSERT INTO addresses (street, city, state, postal_code, user_id, created_at, updated_at) VALUES
    ('123 Gangnam-daero', 'Seoul', 'Seoul', '06018', (SELECT id FROM users WHERE email = 'student@utown.dev'), NOW(6), NOW(6));
