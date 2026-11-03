-- Test Users
-- User 1: local@example.com (Multiple listings)
INSERT INTO users (id, first_name, last_name, email, created_at, updated_at) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Local', 'User', 'local@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 2: seller1@example.com (One listing)
INSERT INTO users (id, first_name, last_name, email, created_at, updated_at) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Amina', 'Sellers', 'seller1@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 3: seller2@example.com (None)
INSERT INTO users (id, first_name, last_name, email, created_at, updated_at) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Youssef', 'Buyers', 'seller2@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 4: expert@example.com (Many listings, all data filled)
INSERT INTO users (id, first_name, last_name, email, created_at, updated_at) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'Sara', 'Expert', 'expert@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User 5: casual@example.com (Mixed listings)
INSERT INTO users (id, first_name, last_name, email, created_at, updated_at) 
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'Mehdi', 'Casual', 'casual@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- Test Listings (20 total)

-- User 1 Listings (5)
INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Vintage Denim Jacket', 'A classic vintage denim jacket in great condition.', 45.00, 1, 'VERY_GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd103cc35-670f-5520-b0b8-846cb4950a02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'DELIVERY');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Blue');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Summer Floral Dress', 'Beautiful floral dress perfect for summer outings.', 29.99, 1, 'NEW_WITH_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd0b99ed7-fac8-521e-b512-2832dbe9f174', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'ab1e8325-f121-5dcf-b221-559c8176cd5f');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'MEETUP');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Red');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Yellow');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'Leather Boots', 'Sturdy leather boots, lightly used.', 89.00, 1, 'GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'deeef5b3-1228-51a7-85e5-0f6cc5406354', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', '5a8cab81-4b6c-5458-a780-2256900eb417');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'PARCEL');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23', 'Brown');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'Wool Scarf', 'Warm wool scarf for winter.', 15.00, 1, 'NEW_WITHOUT_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '3d8f77e5-f449-5822-98ac-6a46f2b5bb19', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'a340d79b-4677-5058-a4d5-53dca0d094d0');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'Cotton T-Shirt', NULL, 10.00, 3, 'GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25', 'MEETUP');


-- User 2 Listings (1)
INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'Silk Blouse', 'Elegant silk blouse.', 55.00, 1, 'NEW_WITH_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'd60d476e-acde-5af8-87d3-2c6d48eb4996', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'ab1e8325-f121-5dcf-b221-559c8176cd5f');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a26', 'DELIVERY');


-- User 4 Listings (10 - Expert User)
INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'Running Shoes', 'Professional running shoes, worn twice.', 120.00, 1, 'VERY_GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '643090d8-7434-5c37-92a8-29372ae7340a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'c8fe57a9-6261-5401-a00e-291f5c64447a');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'PARCEL');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'Black');
INSERT INTO listing_colors (listing_id, color) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a27', 'Neon Green');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'Winter Parka', 'Heavy duty parka for extreme cold.', 250.00, 1, 'NEW_WITHOUT_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'd103cc35-670f-5520-b0b8-846cb4950a02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', '4ac3edcb-285f-51d6-9718-825505a77e3a');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a28', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'Gym Leggings', 'High-waisted gym leggings.', 35.00, 2, 'NEW_WITH_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'd60d476e-acde-5af8-87d3-2c6d48eb4996', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a29', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'Designer Handbag', 'Limited edition leather handbag.', 450.00, 1, 'GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'a340d79b-4677-5058-a4d5-53dca0d094d0');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'MEETUP');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'Cargo Pants', 'Trendy cargo pants with many pockets.', 40.00, 1, 'SATISFACTORY', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '77bc4938-c572-56f8-9797-ec9bb3240b80', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', '5a8cab81-4b6c-5458-a780-2256900eb417');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'Graphic Hoodie', 'Hoodie with cool art.', 30.00, 1, 'GOOD', 'SOLD', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '280b284f-d0b7-54bd-be5c-25bbe8771265', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a32', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'Formal Shirt', 'White formal shirt for office.', 25.00, 1, 'VERY_GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '593e57cc-611c-53b8-98ab-6ce146a18643', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'PARCEL');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'Sundress', 'Light yellow sundress.', 20.00, 1, 'SATISFACTORY', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'd0b99ed7-fac8-521e-b512-2832dbe9f174', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'e4608262-a817-50bd-8a63-6858a0cc7f03');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'ab1e8325-f121-5dcf-b221-559c8176cd5f');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a34', 'MEETUP');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'Belt', 'Black leather belt.', 12.00, 1, 'GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a4bb4124-87c0-5790-b79b-afd3bf4d3cf9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'a340d79b-4677-5058-a4d5-53dca0d094d0');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a35', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 'Sneakers', 'Classic white sneakers.', 60.00, 1, 'GOOD', 'RESERVED', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'deeef5b3-1228-51a7-85e5-0f6cc5406354', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a36', 'DELIVERY');


-- User 5 Listings (4)
INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'Polo Shirt', 'Classic navy polo.', 18.00, 1, 'GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a37', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'Shorts', 'Denim shorts.', 15.00, 1, 'SATISFACTORY', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '785d358e-f083-51a2-8957-a5e8c84bc933', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'e4608262-a817-50bd-8a63-6858a0cc7f03');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'df4148ba-6e17-5124-96ff-83c0b0548233');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a38', 'MEETUP');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'Beanie', 'Grey wool beanie.', 8.00, 1, 'VERY_GOOD', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '3d8f77e5-f449-5822-98ac-6a46f2b5bb19', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_sizes (listing_id, size_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'a340d79b-4677-5058-a4d5-53dca0d094d0');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a39', 'DELIVERY');

INSERT INTO listings (id, title, description, price, quantity, condition, status, user_id, category_id, created_at, updated_at) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'Socks', 'Pack of 3 wool socks.', 5.00, 1, 'NEW_WITH_TAGS', 'AVAILABLE', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', '75aa697f-4e6f-543e-a566-ec856f75d702', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO listing_cities (listing_id, city_id) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', '13464db4-c346-5288-b2ae-97df62bc79ca');
INSERT INTO listing_contact_types (listing_id, contact_type) VALUES ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'DELIVERY');

-- User Preferences
-- User 1 Preferences (Tops, Bottoms)
INSERT INTO user_preferences (id, user_id, category_id)
VALUES (RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
INSERT INTO user_preferences (id, user_id, category_id)
VALUES (RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '77bc4938-c572-56f8-9797-ec9bb3240b80');

-- User 4 Preferences (Expert - Outerwear, Footwear, Accessories)
INSERT INTO user_preferences (id, user_id, category_id)
VALUES (RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'd103cc35-670f-5520-b0b8-846cb4950a02');
INSERT INTO user_preferences (id, user_id, category_id)
VALUES (RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
INSERT INTO user_preferences (id, user_id, category_id)
VALUES (RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
