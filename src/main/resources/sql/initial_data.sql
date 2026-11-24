-- Repeat-safe reference data for the existing schema. Existing IDs and category customizations are preserved.
-- size_groups
INSERT INTO size_groups (id, name) SELECT '439daea4-0ef8-5395-bf7a-8f9941c816b3', 'Vêtements · tailles internationales et EU' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '439daea4-0ef8-5395-bf7a-8f9941c816b3');
UPDATE size_groups SET name = 'Vêtements · tailles internationales et EU' WHERE id = '439daea4-0ef8-5395-bf7a-8f9941c816b3';
INSERT INTO size_groups (id, name) SELECT 'b77d5d2e-085c-53a0-a81b-83523baf001d', 'Chaussures adultes · EU' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = 'b77d5d2e-085c-53a0-a81b-83523baf001d');
UPDATE size_groups SET name = 'Chaussures adultes · EU' WHERE id = 'b77d5d2e-085c-53a0-a81b-83523baf001d';
INSERT INTO size_groups (id, name) SELECT '96464d98-492e-580b-942c-52d2e900ea71', 'Vêtements bébé · âge' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '96464d98-492e-580b-942c-52d2e900ea71');
UPDATE size_groups SET name = 'Vêtements bébé · âge' WHERE id = '96464d98-492e-580b-942c-52d2e900ea71';
INSERT INTO size_groups (id, name) SELECT '7f0371e6-b070-57c1-af49-811a0f2c8078', 'Vêtements enfant · âge' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '7f0371e6-b070-57c1-af49-811a0f2c8078');
UPDATE size_groups SET name = 'Vêtements enfant · âge' WHERE id = '7f0371e6-b070-57c1-af49-811a0f2c8078';
INSERT INTO size_groups (id, name) SELECT 'a4a6213c-b072-5cdf-b1ba-d25971b705c4', 'Chaussures enfant · EU' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4');
UPDATE size_groups SET name = 'Chaussures enfant · EU' WHERE id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4';
INSERT INTO size_groups (id, name) SELECT 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', 'Taille unique' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622');
UPDATE size_groups SET name = 'Taille unique' WHERE id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622';
INSERT INTO size_groups (id, name) SELECT 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c', 'Jeans · tour de taille (pouces)' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c');
UPDATE size_groups SET name = 'Jeans · tour de taille (pouces)' WHERE id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c';
INSERT INTO size_groups (id, name) SELECT '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250', 'Ceintures · longueur (cm)' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250');
UPDATE size_groups SET name = 'Ceintures · longueur (cm)' WHERE id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250';
INSERT INTO size_groups (id, name) SELECT '705c5a80-1e19-5487-8eac-19459949de00', 'Chapeaux et bonnets' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '705c5a80-1e19-5487-8eac-19459949de00');
UPDATE size_groups SET name = 'Chapeaux et bonnets' WHERE id = '705c5a80-1e19-5487-8eac-19459949de00';
INSERT INTO size_groups (id, name) SELECT '7007afe3-61cd-5a14-88b0-2f057dceb9e8', 'Gants' WHERE NOT EXISTS (SELECT 1 FROM size_groups WHERE id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8');
UPDATE size_groups SET name = 'Gants' WHERE id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8';

-- sizes
INSERT INTO sizes (id, name, size_group_id) SELECT '12fad27f-f77e-5351-b49a-b3143e802d99', 'XXS', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '12fad27f-f77e-5351-b49a-b3143e802d99');
UPDATE sizes SET name = 'XXS', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '12fad27f-f77e-5351-b49a-b3143e802d99';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ab1e8325-f121-5dcf-b221-559c8176cd5f', 'XS', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ab1e8325-f121-5dcf-b221-559c8176cd5f');
UPDATE sizes SET name = 'XS', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'ab1e8325-f121-5dcf-b221-559c8176cd5f';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b8f39627-d16a-5431-b860-3bc8d24ea806', 'S', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b8f39627-d16a-5431-b860-3bc8d24ea806');
UPDATE sizes SET name = 'S', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'b8f39627-d16a-5431-b860-3bc8d24ea806';
INSERT INTO sizes (id, name, size_group_id) SELECT 'df4148ba-6e17-5124-96ff-83c0b0548233', 'M', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'df4148ba-6e17-5124-96ff-83c0b0548233');
UPDATE sizes SET name = 'M', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'df4148ba-6e17-5124-96ff-83c0b0548233';
INSERT INTO sizes (id, name, size_group_id) SELECT '76956b30-a29a-50b5-a504-6d250d7f20f9', 'L', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '76956b30-a29a-50b5-a504-6d250d7f20f9');
UPDATE sizes SET name = 'L', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '76956b30-a29a-50b5-a504-6d250d7f20f9';
INSERT INTO sizes (id, name, size_group_id) SELECT '2e7bc21e-58f2-59d4-ab18-d072cb8285d9', 'XL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2e7bc21e-58f2-59d4-ab18-d072cb8285d9');
UPDATE sizes SET name = 'XL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '2e7bc21e-58f2-59d4-ab18-d072cb8285d9';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b8bfd24d-31af-5208-b722-123f1eb4f8a3', 'XXL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b8bfd24d-31af-5208-b722-123f1eb4f8a3');
UPDATE sizes SET name = 'XXL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'b8bfd24d-31af-5208-b722-123f1eb4f8a3';
INSERT INTO sizes (id, name, size_group_id) SELECT '127f57ab-373b-51ed-9a52-6207b8b45e53', 'XXXL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '127f57ab-373b-51ed-9a52-6207b8b45e53');
UPDATE sizes SET name = 'XXXL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '127f57ab-373b-51ed-9a52-6207b8b45e53';
INSERT INTO sizes (id, name, size_group_id) SELECT '4a7799b7-c027-561f-bf0c-45666d2be588', 'EU 35', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '4a7799b7-c027-561f-bf0c-45666d2be588');
UPDATE sizes SET name = 'EU 35', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '4a7799b7-c027-561f-bf0c-45666d2be588';
INSERT INTO sizes (id, name, size_group_id) SELECT '6035c916-83c9-5a9c-bdf7-30ab6f114b6b', 'EU 36', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6035c916-83c9-5a9c-bdf7-30ab6f114b6b');
UPDATE sizes SET name = 'EU 36', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '6035c916-83c9-5a9c-bdf7-30ab6f114b6b';
INSERT INTO sizes (id, name, size_group_id) SELECT '1d6d1b3b-0d97-57b9-bdef-0adbbfe2a033', 'EU 37', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1d6d1b3b-0d97-57b9-bdef-0adbbfe2a033');
UPDATE sizes SET name = 'EU 37', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '1d6d1b3b-0d97-57b9-bdef-0adbbfe2a033';
INSERT INTO sizes (id, name, size_group_id) SELECT '6ca5452f-7f0a-5b78-946d-b71010bfba7b', 'EU 38', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6ca5452f-7f0a-5b78-946d-b71010bfba7b');
UPDATE sizes SET name = 'EU 38', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '6ca5452f-7f0a-5b78-946d-b71010bfba7b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f7159d94-329d-5049-aca5-85dda7e8e9a4', 'EU 39', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f7159d94-329d-5049-aca5-85dda7e8e9a4');
UPDATE sizes SET name = 'EU 39', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'f7159d94-329d-5049-aca5-85dda7e8e9a4';
INSERT INTO sizes (id, name, size_group_id) SELECT 'c601df5f-ff49-5d36-832a-dab375481b43', 'EU 40', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'c601df5f-ff49-5d36-832a-dab375481b43');
UPDATE sizes SET name = 'EU 40', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'c601df5f-ff49-5d36-832a-dab375481b43';
INSERT INTO sizes (id, name, size_group_id) SELECT '48d5aebc-9f3d-5013-9b85-b1d7b50bce1e', 'EU 41', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '48d5aebc-9f3d-5013-9b85-b1d7b50bce1e');
UPDATE sizes SET name = 'EU 41', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '48d5aebc-9f3d-5013-9b85-b1d7b50bce1e';
INSERT INTO sizes (id, name, size_group_id) SELECT '2a9ca417-461d-550a-96ae-5d56fbd7229b', 'EU 42', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2a9ca417-461d-550a-96ae-5d56fbd7229b');
UPDATE sizes SET name = 'EU 42', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '2a9ca417-461d-550a-96ae-5d56fbd7229b';
INSERT INTO sizes (id, name, size_group_id) SELECT '3cc412aa-7718-547f-85ac-7c91d4d6258a', 'EU 43', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3cc412aa-7718-547f-85ac-7c91d4d6258a');
UPDATE sizes SET name = 'EU 43', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '3cc412aa-7718-547f-85ac-7c91d4d6258a';
INSERT INTO sizes (id, name, size_group_id) SELECT '6357887a-1b17-5692-829e-72cdf9930c66', 'EU 44', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6357887a-1b17-5692-829e-72cdf9930c66');
UPDATE sizes SET name = 'EU 44', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '6357887a-1b17-5692-829e-72cdf9930c66';
INSERT INTO sizes (id, name, size_group_id) SELECT '9a0a84e8-f9b1-5f80-a0ba-6d03967766d8', 'EU 45', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '9a0a84e8-f9b1-5f80-a0ba-6d03967766d8');
UPDATE sizes SET name = 'EU 45', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '9a0a84e8-f9b1-5f80-a0ba-6d03967766d8';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b1d9a443-62b8-576e-b313-359b6ec10f65', 'EU 46', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b1d9a443-62b8-576e-b313-359b6ec10f65');
UPDATE sizes SET name = 'EU 46', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'b1d9a443-62b8-576e-b313-359b6ec10f65';
INSERT INTO sizes (id, name, size_group_id) SELECT '74790064-188e-5921-852a-724b2ee5affd', '0-1 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '74790064-188e-5921-852a-724b2ee5affd');
UPDATE sizes SET name = '0-1 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '74790064-188e-5921-852a-724b2ee5affd';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd49ee4c8-d605-5e3a-8cca-35825fa4bb20', '3 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd49ee4c8-d605-5e3a-8cca-35825fa4bb20');
UPDATE sizes SET name = '3 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = 'd49ee4c8-d605-5e3a-8cca-35825fa4bb20';
INSERT INTO sizes (id, name, size_group_id) SELECT '5154ec07-1c49-5597-94c7-0ed473f1c812', '6 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5154ec07-1c49-5597-94c7-0ed473f1c812');
UPDATE sizes SET name = '6 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '5154ec07-1c49-5597-94c7-0ed473f1c812';
INSERT INTO sizes (id, name, size_group_id) SELECT '10a5740f-236a-5129-8ec4-5745063ad915', '9 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '10a5740f-236a-5129-8ec4-5745063ad915');
UPDATE sizes SET name = '9 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '10a5740f-236a-5129-8ec4-5745063ad915';
INSERT INTO sizes (id, name, size_group_id) SELECT '5541ecd6-7ea9-5748-8323-f581fc52a4ae', '12 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5541ecd6-7ea9-5748-8323-f581fc52a4ae');
UPDATE sizes SET name = '12 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '5541ecd6-7ea9-5748-8323-f581fc52a4ae';
INSERT INTO sizes (id, name, size_group_id) SELECT '5896812d-87d5-5986-a36f-6560347cbe0a', '18 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5896812d-87d5-5986-a36f-6560347cbe0a');
UPDATE sizes SET name = '18 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '5896812d-87d5-5986-a36f-6560347cbe0a';
INSERT INTO sizes (id, name, size_group_id) SELECT '1c163f08-c2f8-583f-a4a2-24224b086498', '24 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1c163f08-c2f8-583f-a4a2-24224b086498');
UPDATE sizes SET name = '24 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '1c163f08-c2f8-583f-a4a2-24224b086498';
INSERT INTO sizes (id, name, size_group_id) SELECT '67599ef8-a015-58c7-8dd1-9b17b749b366', '36 mois', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '67599ef8-a015-58c7-8dd1-9b17b749b366');
UPDATE sizes SET name = '36 mois', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '67599ef8-a015-58c7-8dd1-9b17b749b366';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd053eba9-03bb-556f-8057-37c9404ff632', '2 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd053eba9-03bb-556f-8057-37c9404ff632');
UPDATE sizes SET name = '2 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'd053eba9-03bb-556f-8057-37c9404ff632';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd5862895-1aeb-575f-b3e1-554570332119', '3 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd5862895-1aeb-575f-b3e1-554570332119');
UPDATE sizes SET name = '3 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'd5862895-1aeb-575f-b3e1-554570332119';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ded88ac6-a779-5d61-8ebd-f7b9c96d671a', '4 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ded88ac6-a779-5d61-8ebd-f7b9c96d671a');
UPDATE sizes SET name = '4 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'ded88ac6-a779-5d61-8ebd-f7b9c96d671a';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd5c0e3f9-152a-5921-9934-c1e5fa89d4e7', '5 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd5c0e3f9-152a-5921-9934-c1e5fa89d4e7');
UPDATE sizes SET name = '5 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'd5c0e3f9-152a-5921-9934-c1e5fa89d4e7';
INSERT INTO sizes (id, name, size_group_id) SELECT '5afcf632-1e40-588b-906b-4dbb28e87c53', '6 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5afcf632-1e40-588b-906b-4dbb28e87c53');
UPDATE sizes SET name = '6 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '5afcf632-1e40-588b-906b-4dbb28e87c53';
INSERT INTO sizes (id, name, size_group_id) SELECT 'a3a4da30-bdb8-565d-a1cc-3911b18e1593', '7 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'a3a4da30-bdb8-565d-a1cc-3911b18e1593');
UPDATE sizes SET name = '7 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'a3a4da30-bdb8-565d-a1cc-3911b18e1593';
INSERT INTO sizes (id, name, size_group_id) SELECT '722c7fb0-061c-536c-ac9d-803488c3a6b5', '8 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '722c7fb0-061c-536c-ac9d-803488c3a6b5');
UPDATE sizes SET name = '8 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '722c7fb0-061c-536c-ac9d-803488c3a6b5';
INSERT INTO sizes (id, name, size_group_id) SELECT 'da5fcc15-d6f8-553b-b819-cf81bb80c86b', '9 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'da5fcc15-d6f8-553b-b819-cf81bb80c86b');
UPDATE sizes SET name = '9 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'da5fcc15-d6f8-553b-b819-cf81bb80c86b';
INSERT INTO sizes (id, name, size_group_id) SELECT '9f85028e-b443-5a0c-b2f3-f722b842d3f4', '10 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '9f85028e-b443-5a0c-b2f3-f722b842d3f4');
UPDATE sizes SET name = '10 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '9f85028e-b443-5a0c-b2f3-f722b842d3f4';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b1499623-8d1e-5c85-8966-b5ecd8cd8a0b', '11 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b1499623-8d1e-5c85-8966-b5ecd8cd8a0b');
UPDATE sizes SET name = '11 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'b1499623-8d1e-5c85-8966-b5ecd8cd8a0b';
INSERT INTO sizes (id, name, size_group_id) SELECT '777186ab-ead6-51f0-84d8-77245ef11435', '12 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '777186ab-ead6-51f0-84d8-77245ef11435');
UPDATE sizes SET name = '12 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '777186ab-ead6-51f0-84d8-77245ef11435';
INSERT INTO sizes (id, name, size_group_id) SELECT '27cd35fe-a403-52ca-994a-be7109dc4309', '13 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '27cd35fe-a403-52ca-994a-be7109dc4309');
UPDATE sizes SET name = '13 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '27cd35fe-a403-52ca-994a-be7109dc4309';
INSERT INTO sizes (id, name, size_group_id) SELECT 'cc256dcf-7160-509d-9687-f3755d6c3986', '14 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'cc256dcf-7160-509d-9687-f3755d6c3986');
UPDATE sizes SET name = '14 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'cc256dcf-7160-509d-9687-f3755d6c3986';
INSERT INTO sizes (id, name, size_group_id) SELECT '09f4ccd5-3d04-51dd-a3b8-6c31ea144fee', '15 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '09f4ccd5-3d04-51dd-a3b8-6c31ea144fee');
UPDATE sizes SET name = '15 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '09f4ccd5-3d04-51dd-a3b8-6c31ea144fee';
INSERT INTO sizes (id, name, size_group_id) SELECT '5eade72c-3ae8-536c-bd75-2469620eeb77', '16 ans', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5eade72c-3ae8-536c-bd75-2469620eeb77');
UPDATE sizes SET name = '16 ans', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '5eade72c-3ae8-536c-bd75-2469620eeb77';
INSERT INTO sizes (id, name, size_group_id) SELECT 'bdf19675-bbc9-5a4b-8a27-2a38ddd33db3', 'EU 18', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'bdf19675-bbc9-5a4b-8a27-2a38ddd33db3');
UPDATE sizes SET name = 'EU 18', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'bdf19675-bbc9-5a4b-8a27-2a38ddd33db3';
INSERT INTO sizes (id, name, size_group_id) SELECT '43c7decd-9cac-5d09-9dee-f152c451499d', 'EU 19', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '43c7decd-9cac-5d09-9dee-f152c451499d');
UPDATE sizes SET name = 'EU 19', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '43c7decd-9cac-5d09-9dee-f152c451499d';
INSERT INTO sizes (id, name, size_group_id) SELECT 'cd2812e0-865d-55fe-9df4-076affdf2787', 'EU 20', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'cd2812e0-865d-55fe-9df4-076affdf2787');
UPDATE sizes SET name = 'EU 20', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'cd2812e0-865d-55fe-9df4-076affdf2787';
INSERT INTO sizes (id, name, size_group_id) SELECT '037c5545-d5b6-51c0-bc8c-43ee71644b41', 'EU 21', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '037c5545-d5b6-51c0-bc8c-43ee71644b41');
UPDATE sizes SET name = 'EU 21', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '037c5545-d5b6-51c0-bc8c-43ee71644b41';
INSERT INTO sizes (id, name, size_group_id) SELECT '460f5d0a-1d02-5602-abf4-357dc5fa1b70', 'EU 22', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '460f5d0a-1d02-5602-abf4-357dc5fa1b70');
UPDATE sizes SET name = 'EU 22', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '460f5d0a-1d02-5602-abf4-357dc5fa1b70';
INSERT INTO sizes (id, name, size_group_id) SELECT 'eb57a252-9133-5636-b7c5-764a1a690222', 'EU 23', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'eb57a252-9133-5636-b7c5-764a1a690222');
UPDATE sizes SET name = 'EU 23', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'eb57a252-9133-5636-b7c5-764a1a690222';
INSERT INTO sizes (id, name, size_group_id) SELECT '63352cc1-470c-5600-aad1-9e1ccba9493c', 'EU 24', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '63352cc1-470c-5600-aad1-9e1ccba9493c');
UPDATE sizes SET name = 'EU 24', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '63352cc1-470c-5600-aad1-9e1ccba9493c';
INSERT INTO sizes (id, name, size_group_id) SELECT '2a6e640b-4e74-56c0-a03a-85a4ba5972ef', 'EU 25', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2a6e640b-4e74-56c0-a03a-85a4ba5972ef');
UPDATE sizes SET name = 'EU 25', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '2a6e640b-4e74-56c0-a03a-85a4ba5972ef';
INSERT INTO sizes (id, name, size_group_id) SELECT 'e7d9e64f-61c4-549f-ac75-5e18cd2c8bdd', 'EU 26', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'e7d9e64f-61c4-549f-ac75-5e18cd2c8bdd');
UPDATE sizes SET name = 'EU 26', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'e7d9e64f-61c4-549f-ac75-5e18cd2c8bdd';
INSERT INTO sizes (id, name, size_group_id) SELECT '848f648a-20ac-5baf-a835-845fbd703c1f', 'EU 27', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '848f648a-20ac-5baf-a835-845fbd703c1f');
UPDATE sizes SET name = 'EU 27', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '848f648a-20ac-5baf-a835-845fbd703c1f';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ec29d6a9-da13-5eb8-bca6-6882c2303d8b', 'EU 28', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ec29d6a9-da13-5eb8-bca6-6882c2303d8b');
UPDATE sizes SET name = 'EU 28', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'ec29d6a9-da13-5eb8-bca6-6882c2303d8b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'fe35c267-a93e-518d-9087-e91d0e1c6331', 'EU 29', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'fe35c267-a93e-518d-9087-e91d0e1c6331');
UPDATE sizes SET name = 'EU 29', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'fe35c267-a93e-518d-9087-e91d0e1c6331';
INSERT INTO sizes (id, name, size_group_id) SELECT 'e28be6e7-1d7e-5ea6-a99e-476116e63496', 'EU 30', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'e28be6e7-1d7e-5ea6-a99e-476116e63496');
UPDATE sizes SET name = 'EU 30', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'e28be6e7-1d7e-5ea6-a99e-476116e63496';
INSERT INTO sizes (id, name, size_group_id) SELECT 'fef0a260-ac5d-5974-9c90-613cdfe0ba8b', 'EU 31', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'fef0a260-ac5d-5974-9c90-613cdfe0ba8b');
UPDATE sizes SET name = 'EU 31', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'fef0a260-ac5d-5974-9c90-613cdfe0ba8b';
INSERT INTO sizes (id, name, size_group_id) SELECT '8ec82332-54f9-5b3d-af19-868660f14945', 'EU 32', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '8ec82332-54f9-5b3d-af19-868660f14945');
UPDATE sizes SET name = 'EU 32', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '8ec82332-54f9-5b3d-af19-868660f14945';
INSERT INTO sizes (id, name, size_group_id) SELECT '60ef0a82-e9c1-51c2-aeba-32f38e067858', 'EU 33', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '60ef0a82-e9c1-51c2-aeba-32f38e067858');
UPDATE sizes SET name = 'EU 33', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '60ef0a82-e9c1-51c2-aeba-32f38e067858';
INSERT INTO sizes (id, name, size_group_id) SELECT '48b6604c-b393-5144-a976-44451277561b', 'EU 34', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '48b6604c-b393-5144-a976-44451277561b');
UPDATE sizes SET name = 'EU 34', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '48b6604c-b393-5144-a976-44451277561b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'a340d79b-4677-5058-a4d5-53dca0d094d0', 'Taille unique', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'a340d79b-4677-5058-a4d5-53dca0d094d0');
UPDATE sizes SET name = 'Taille unique', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'a340d79b-4677-5058-a4d5-53dca0d094d0';
INSERT INTO sizes (id, name, size_group_id) SELECT '122ff458-6a6a-52c9-99a4-e4e97b7cdfee', 'XXXS', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '122ff458-6a6a-52c9-99a4-e4e97b7cdfee');
UPDATE sizes SET name = 'XXXS', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '122ff458-6a6a-52c9-99a4-e4e97b7cdfee';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd2798132-b8f8-5dab-963f-c6d6a7225a22', '4XL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd2798132-b8f8-5dab-963f-c6d6a7225a22');
UPDATE sizes SET name = '4XL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd2798132-b8f8-5dab-963f-c6d6a7225a22';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd1955d72-075b-5a70-87a9-e9b4c3961a7f', '5XL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd1955d72-075b-5a70-87a9-e9b4c3961a7f');
UPDATE sizes SET name = '5XL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd1955d72-075b-5a70-87a9-e9b4c3961a7f';
INSERT INTO sizes (id, name, size_group_id) SELECT '3d63bff5-b3cc-5a17-9ce6-000d82fadf59', '6XL', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3d63bff5-b3cc-5a17-9ce6-000d82fadf59');
UPDATE sizes SET name = '6XL', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '3d63bff5-b3cc-5a17-9ce6-000d82fadf59';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd0960544-b028-5aeb-a591-951217ee605e', 'Taille unique', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd0960544-b028-5aeb-a591-951217ee605e');
UPDATE sizes SET name = 'Taille unique', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd0960544-b028-5aeb-a591-951217ee605e';
INSERT INTO sizes (id, name, size_group_id) SELECT 'aa1a37d3-1019-516f-8a5b-46df426df320', 'Autre taille', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'aa1a37d3-1019-516f-8a5b-46df426df320');
UPDATE sizes SET name = 'Autre taille', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'aa1a37d3-1019-516f-8a5b-46df426df320';
INSERT INTO sizes (id, name, size_group_id) SELECT '16160047-11a7-5c52-8bec-2d1d5a295917', 'EU 32', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '16160047-11a7-5c52-8bec-2d1d5a295917');
UPDATE sizes SET name = 'EU 32', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '16160047-11a7-5c52-8bec-2d1d5a295917';
INSERT INTO sizes (id, name, size_group_id) SELECT '8b35f6ab-0066-5897-9a32-f0011d2e1d01', 'EU 34', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '8b35f6ab-0066-5897-9a32-f0011d2e1d01');
UPDATE sizes SET name = 'EU 34', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '8b35f6ab-0066-5897-9a32-f0011d2e1d01';
INSERT INTO sizes (id, name, size_group_id) SELECT '5db79293-e7a2-5394-ab5e-40f11911b6a1', 'EU 36', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5db79293-e7a2-5394-ab5e-40f11911b6a1');
UPDATE sizes SET name = 'EU 36', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '5db79293-e7a2-5394-ab5e-40f11911b6a1';
INSERT INTO sizes (id, name, size_group_id) SELECT '14f96dd1-f119-5ed8-ad8c-63730e134e4e', 'EU 38', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '14f96dd1-f119-5ed8-ad8c-63730e134e4e');
UPDATE sizes SET name = 'EU 38', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '14f96dd1-f119-5ed8-ad8c-63730e134e4e';
INSERT INTO sizes (id, name, size_group_id) SELECT '2f3d8395-6af9-54c5-81a3-2bcd14be9c50', 'EU 40', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2f3d8395-6af9-54c5-81a3-2bcd14be9c50');
UPDATE sizes SET name = 'EU 40', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '2f3d8395-6af9-54c5-81a3-2bcd14be9c50';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ddc66741-bcf1-50b1-ac20-2301b68fa780', 'EU 42', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ddc66741-bcf1-50b1-ac20-2301b68fa780');
UPDATE sizes SET name = 'EU 42', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'ddc66741-bcf1-50b1-ac20-2301b68fa780';
INSERT INTO sizes (id, name, size_group_id) SELECT '681dc9fc-ffcd-5282-8d52-d7e30caf0f70', 'EU 44', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '681dc9fc-ffcd-5282-8d52-d7e30caf0f70');
UPDATE sizes SET name = 'EU 44', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '681dc9fc-ffcd-5282-8d52-d7e30caf0f70';
INSERT INTO sizes (id, name, size_group_id) SELECT '633b8544-11eb-5798-ac8a-28ac9df3bef9', 'EU 46', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '633b8544-11eb-5798-ac8a-28ac9df3bef9');
UPDATE sizes SET name = 'EU 46', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '633b8544-11eb-5798-ac8a-28ac9df3bef9';
INSERT INTO sizes (id, name, size_group_id) SELECT '50f05713-a94f-5bbf-86d5-15a7a5608722', 'EU 48', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '50f05713-a94f-5bbf-86d5-15a7a5608722');
UPDATE sizes SET name = 'EU 48', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '50f05713-a94f-5bbf-86d5-15a7a5608722';
INSERT INTO sizes (id, name, size_group_id) SELECT '5e013ba7-8a31-5fb4-a178-4bb50cd56818', 'EU 50', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5e013ba7-8a31-5fb4-a178-4bb50cd56818');
UPDATE sizes SET name = 'EU 50', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '5e013ba7-8a31-5fb4-a178-4bb50cd56818';
INSERT INTO sizes (id, name, size_group_id) SELECT 'df75126e-b561-50b0-b50f-b3e411e43f52', 'EU 52', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'df75126e-b561-50b0-b50f-b3e411e43f52');
UPDATE sizes SET name = 'EU 52', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'df75126e-b561-50b0-b50f-b3e411e43f52';
INSERT INTO sizes (id, name, size_group_id) SELECT 'bd392eff-9253-546a-81ae-0177a5bf9a50', 'EU 54', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'bd392eff-9253-546a-81ae-0177a5bf9a50');
UPDATE sizes SET name = 'EU 54', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'bd392eff-9253-546a-81ae-0177a5bf9a50';
INSERT INTO sizes (id, name, size_group_id) SELECT '770e2461-a133-5ee5-8c69-68ecf9aba343', 'EU 56', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '770e2461-a133-5ee5-8c69-68ecf9aba343');
UPDATE sizes SET name = 'EU 56', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '770e2461-a133-5ee5-8c69-68ecf9aba343';
INSERT INTO sizes (id, name, size_group_id) SELECT '72d96d96-d640-5c57-8421-b00f2ecf4e27', 'EU 58', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '72d96d96-d640-5c57-8421-b00f2ecf4e27');
UPDATE sizes SET name = 'EU 58', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '72d96d96-d640-5c57-8421-b00f2ecf4e27';
INSERT INTO sizes (id, name, size_group_id) SELECT '5e78de2e-8f7c-5ea0-8ccf-6688e7f36d2b', 'EU 60', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5e78de2e-8f7c-5ea0-8ccf-6688e7f36d2b');
UPDATE sizes SET name = 'EU 60', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '5e78de2e-8f7c-5ea0-8ccf-6688e7f36d2b';
INSERT INTO sizes (id, name, size_group_id) SELECT '5045ffb5-54e3-52df-b2ee-4fc06affd0f8', 'EU 62', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5045ffb5-54e3-52df-b2ee-4fc06affd0f8');
UPDATE sizes SET name = 'EU 62', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '5045ffb5-54e3-52df-b2ee-4fc06affd0f8';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f7c6591e-a567-5bd9-9795-9c1955d38aff', 'EU 64', '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f7c6591e-a567-5bd9-9795-9c1955d38aff');
UPDATE sizes SET name = 'EU 64', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'f7c6591e-a567-5bd9-9795-9c1955d38aff';
INSERT INTO sizes (id, name, size_group_id) SELECT '3716065f-3938-52cc-b7d6-eefdaeff37f7', 'EU 34', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3716065f-3938-52cc-b7d6-eefdaeff37f7');
UPDATE sizes SET name = 'EU 34', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '3716065f-3938-52cc-b7d6-eefdaeff37f7';
INSERT INTO sizes (id, name, size_group_id) SELECT '7f43a76f-1b46-523a-bd4c-9bd9f92a6670', 'EU 47', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7f43a76f-1b46-523a-bd4c-9bd9f92a6670');
UPDATE sizes SET name = 'EU 47', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '7f43a76f-1b46-523a-bd4c-9bd9f92a6670';
INSERT INTO sizes (id, name, size_group_id) SELECT '90db3855-cb91-5813-a37a-ed4302f65b3e', 'EU 48', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '90db3855-cb91-5813-a37a-ed4302f65b3e');
UPDATE sizes SET name = 'EU 48', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '90db3855-cb91-5813-a37a-ed4302f65b3e';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f291506d-5fdf-5492-87f5-107e9031b3b5', 'EU 49', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f291506d-5fdf-5492-87f5-107e9031b3b5');
UPDATE sizes SET name = 'EU 49', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'f291506d-5fdf-5492-87f5-107e9031b3b5';
INSERT INTO sizes (id, name, size_group_id) SELECT '3023b535-13b5-5b93-94a4-c457b6536711', 'EU 50', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3023b535-13b5-5b93-94a4-c457b6536711');
UPDATE sizes SET name = 'EU 50', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '3023b535-13b5-5b93-94a4-c457b6536711';
INSERT INTO sizes (id, name, size_group_id) SELECT '49906c8c-80bc-53d6-8893-eed4da62cadc', 'EU 35.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '49906c8c-80bc-53d6-8893-eed4da62cadc');
UPDATE sizes SET name = 'EU 35.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '49906c8c-80bc-53d6-8893-eed4da62cadc';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f1819b06-1bc8-5b0b-b3d6-f379f4bd860f', 'EU 36.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f1819b06-1bc8-5b0b-b3d6-f379f4bd860f');
UPDATE sizes SET name = 'EU 36.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'f1819b06-1bc8-5b0b-b3d6-f379f4bd860f';
INSERT INTO sizes (id, name, size_group_id) SELECT '65eb587f-a05c-5ad4-934b-cadd56e3a2a3', 'EU 37.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '65eb587f-a05c-5ad4-934b-cadd56e3a2a3');
UPDATE sizes SET name = 'EU 37.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '65eb587f-a05c-5ad4-934b-cadd56e3a2a3';
INSERT INTO sizes (id, name, size_group_id) SELECT '47de2a63-a5eb-51be-9728-161703c3bb2b', 'EU 38.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '47de2a63-a5eb-51be-9728-161703c3bb2b');
UPDATE sizes SET name = 'EU 38.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '47de2a63-a5eb-51be-9728-161703c3bb2b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b1f15609-3426-5559-8425-660658f80cdd', 'EU 39.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b1f15609-3426-5559-8425-660658f80cdd');
UPDATE sizes SET name = 'EU 39.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'b1f15609-3426-5559-8425-660658f80cdd';
INSERT INTO sizes (id, name, size_group_id) SELECT '8edaacc8-c66f-5a86-b93b-e059d472022e', 'EU 40.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '8edaacc8-c66f-5a86-b93b-e059d472022e');
UPDATE sizes SET name = 'EU 40.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '8edaacc8-c66f-5a86-b93b-e059d472022e';
INSERT INTO sizes (id, name, size_group_id) SELECT '86ccdcc5-14fc-52ad-9b64-3ecf1f98c6f6', 'EU 41.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '86ccdcc5-14fc-52ad-9b64-3ecf1f98c6f6');
UPDATE sizes SET name = 'EU 41.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '86ccdcc5-14fc-52ad-9b64-3ecf1f98c6f6';
INSERT INTO sizes (id, name, size_group_id) SELECT '78e34590-7865-599e-b0cf-725538437123', 'EU 42.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '78e34590-7865-599e-b0cf-725538437123');
UPDATE sizes SET name = 'EU 42.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '78e34590-7865-599e-b0cf-725538437123';
INSERT INTO sizes (id, name, size_group_id) SELECT '194cbb49-22b5-5d27-bf4f-ce7b2f8dea88', 'EU 43.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '194cbb49-22b5-5d27-bf4f-ce7b2f8dea88');
UPDATE sizes SET name = 'EU 43.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '194cbb49-22b5-5d27-bf4f-ce7b2f8dea88';
INSERT INTO sizes (id, name, size_group_id) SELECT '771391ab-d987-558f-a780-34f0916cd781', 'EU 44.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '771391ab-d987-558f-a780-34f0916cd781');
UPDATE sizes SET name = 'EU 44.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '771391ab-d987-558f-a780-34f0916cd781';
INSERT INTO sizes (id, name, size_group_id) SELECT '23e06e05-bf56-58ea-bd44-f6e8d9db6024', 'EU 45.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '23e06e05-bf56-58ea-bd44-f6e8d9db6024');
UPDATE sizes SET name = 'EU 45.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '23e06e05-bf56-58ea-bd44-f6e8d9db6024';
INSERT INTO sizes (id, name, size_group_id) SELECT '6de5d1e2-d49a-5b29-804d-ecf43412c721', 'EU 46.5', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6de5d1e2-d49a-5b29-804d-ecf43412c721');
UPDATE sizes SET name = 'EU 46.5', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '6de5d1e2-d49a-5b29-804d-ecf43412c721';
INSERT INTO sizes (id, name, size_group_id) SELECT '3a48094d-04ea-5095-9178-b0d3d6c98c92', 'Autre pointure', 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3a48094d-04ea-5095-9178-b0d3d6c98c92');
UPDATE sizes SET name = 'Autre pointure', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '3a48094d-04ea-5095-9178-b0d3d6c98c92';
INSERT INTO sizes (id, name, size_group_id) SELECT '1f3064fd-3b97-5523-bf77-2cc5f466f5bb', 'EU 16', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1f3064fd-3b97-5523-bf77-2cc5f466f5bb');
UPDATE sizes SET name = 'EU 16', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '1f3064fd-3b97-5523-bf77-2cc5f466f5bb';
INSERT INTO sizes (id, name, size_group_id) SELECT '87bfb80f-dbce-564f-8799-7b5ffcbb0f0f', 'EU 17', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '87bfb80f-dbce-564f-8799-7b5ffcbb0f0f');
UPDATE sizes SET name = 'EU 17', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '87bfb80f-dbce-564f-8799-7b5ffcbb0f0f';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ddf7e732-865f-528b-aef0-07959668f9fb', 'EU 35', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ddf7e732-865f-528b-aef0-07959668f9fb');
UPDATE sizes SET name = 'EU 35', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'ddf7e732-865f-528b-aef0-07959668f9fb';
INSERT INTO sizes (id, name, size_group_id) SELECT 'e9cd0a68-5477-5c53-abdb-6acbdc26cff6', 'EU 36', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'e9cd0a68-5477-5c53-abdb-6acbdc26cff6');
UPDATE sizes SET name = 'EU 36', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'e9cd0a68-5477-5c53-abdb-6acbdc26cff6';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f2ffb80d-fe12-52ac-9012-746db89fe82c', 'EU 37', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f2ffb80d-fe12-52ac-9012-746db89fe82c');
UPDATE sizes SET name = 'EU 37', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'f2ffb80d-fe12-52ac-9012-746db89fe82c';
INSERT INTO sizes (id, name, size_group_id) SELECT '2ee91643-88ff-5510-b11b-27c7a82fec2d', 'EU 38', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2ee91643-88ff-5510-b11b-27c7a82fec2d');
UPDATE sizes SET name = 'EU 38', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '2ee91643-88ff-5510-b11b-27c7a82fec2d';
INSERT INTO sizes (id, name, size_group_id) SELECT '579748f4-f141-50ad-a2fe-060911a5fe7f', 'EU 39', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '579748f4-f141-50ad-a2fe-060911a5fe7f');
UPDATE sizes SET name = 'EU 39', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '579748f4-f141-50ad-a2fe-060911a5fe7f';
INSERT INTO sizes (id, name, size_group_id) SELECT '9681af43-0fb0-5369-aeff-81191d50a541', 'EU 40', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '9681af43-0fb0-5369-aeff-81191d50a541');
UPDATE sizes SET name = 'EU 40', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '9681af43-0fb0-5369-aeff-81191d50a541';
INSERT INTO sizes (id, name, size_group_id) SELECT '6217cd5a-ed14-5c2f-aa01-1244c67d2ece', 'Autre pointure', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6217cd5a-ed14-5c2f-aa01-1244c67d2ece');
UPDATE sizes SET name = 'Autre pointure', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = '6217cd5a-ed14-5c2f-aa01-1244c67d2ece';
INSERT INTO sizes (id, name, size_group_id) SELECT 'e28b4ad9-93c6-599e-9c91-3eb6f768bbe4', 'W23', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'e28b4ad9-93c6-599e-9c91-3eb6f768bbe4');
UPDATE sizes SET name = 'W23', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'e28b4ad9-93c6-599e-9c91-3eb6f768bbe4';
INSERT INTO sizes (id, name, size_group_id) SELECT '3c2e4095-6844-5deb-8883-a16ca6e0afd5', 'W24', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3c2e4095-6844-5deb-8883-a16ca6e0afd5');
UPDATE sizes SET name = 'W24', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '3c2e4095-6844-5deb-8883-a16ca6e0afd5';
INSERT INTO sizes (id, name, size_group_id) SELECT '12c42257-f3d2-5580-9f36-b40eaa906ba1', 'W25', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '12c42257-f3d2-5580-9f36-b40eaa906ba1');
UPDATE sizes SET name = 'W25', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '12c42257-f3d2-5580-9f36-b40eaa906ba1';
INSERT INTO sizes (id, name, size_group_id) SELECT '719b8bd7-40ca-5ced-af2c-f5f404985a26', 'W26', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '719b8bd7-40ca-5ced-af2c-f5f404985a26');
UPDATE sizes SET name = 'W26', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '719b8bd7-40ca-5ced-af2c-f5f404985a26';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ee6679dc-ea72-52f5-bbd4-d20430dafb28', 'W27', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ee6679dc-ea72-52f5-bbd4-d20430dafb28');
UPDATE sizes SET name = 'W27', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'ee6679dc-ea72-52f5-bbd4-d20430dafb28';
INSERT INTO sizes (id, name, size_group_id) SELECT '1cf0804c-9114-5474-83ab-5199f1c5d37c', 'W28', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1cf0804c-9114-5474-83ab-5199f1c5d37c');
UPDATE sizes SET name = 'W28', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '1cf0804c-9114-5474-83ab-5199f1c5d37c';
INSERT INTO sizes (id, name, size_group_id) SELECT 'a5fada3e-a11b-5119-9913-05f978c0c3f9', 'W29', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'a5fada3e-a11b-5119-9913-05f978c0c3f9');
UPDATE sizes SET name = 'W29', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'a5fada3e-a11b-5119-9913-05f978c0c3f9';
INSERT INTO sizes (id, name, size_group_id) SELECT '00275517-eaca-5c3e-a418-5c19110d7754', 'W30', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '00275517-eaca-5c3e-a418-5c19110d7754');
UPDATE sizes SET name = 'W30', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '00275517-eaca-5c3e-a418-5c19110d7754';
INSERT INTO sizes (id, name, size_group_id) SELECT '3bc133d9-8d15-577c-8486-6ac2e4efa6e3', 'W31', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3bc133d9-8d15-577c-8486-6ac2e4efa6e3');
UPDATE sizes SET name = 'W31', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '3bc133d9-8d15-577c-8486-6ac2e4efa6e3';
INSERT INTO sizes (id, name, size_group_id) SELECT 'aa3c1f6c-7e71-54eb-9030-1a0c9486436e', 'W32', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'aa3c1f6c-7e71-54eb-9030-1a0c9486436e');
UPDATE sizes SET name = 'W32', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'aa3c1f6c-7e71-54eb-9030-1a0c9486436e';
INSERT INTO sizes (id, name, size_group_id) SELECT '564a1f0f-4111-5ab5-86bb-28553cd6d36e', 'W33', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '564a1f0f-4111-5ab5-86bb-28553cd6d36e');
UPDATE sizes SET name = 'W33', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '564a1f0f-4111-5ab5-86bb-28553cd6d36e';
INSERT INTO sizes (id, name, size_group_id) SELECT '31c1cfd0-ce00-5f10-bfaa-c0800652887d', 'W34', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '31c1cfd0-ce00-5f10-bfaa-c0800652887d');
UPDATE sizes SET name = 'W34', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '31c1cfd0-ce00-5f10-bfaa-c0800652887d';
INSERT INTO sizes (id, name, size_group_id) SELECT '85ad89a7-7d75-540c-8add-3159b47c86bc', 'W35', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '85ad89a7-7d75-540c-8add-3159b47c86bc');
UPDATE sizes SET name = 'W35', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '85ad89a7-7d75-540c-8add-3159b47c86bc';
INSERT INTO sizes (id, name, size_group_id) SELECT '66b8c910-0776-5e14-bc7e-6b92e5ed5c65', 'W36', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '66b8c910-0776-5e14-bc7e-6b92e5ed5c65');
UPDATE sizes SET name = 'W36', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '66b8c910-0776-5e14-bc7e-6b92e5ed5c65';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f1309c58-68d3-5837-a976-2faabbcb4c0f', 'W37', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f1309c58-68d3-5837-a976-2faabbcb4c0f');
UPDATE sizes SET name = 'W37', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'f1309c58-68d3-5837-a976-2faabbcb4c0f';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f862ffd0-f984-55dc-820a-d58aacd4c8f6', 'W38', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f862ffd0-f984-55dc-820a-d58aacd4c8f6');
UPDATE sizes SET name = 'W38', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'f862ffd0-f984-55dc-820a-d58aacd4c8f6';
INSERT INTO sizes (id, name, size_group_id) SELECT 'fed89b68-652b-57f1-a179-790774ad5391', 'W39', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'fed89b68-652b-57f1-a179-790774ad5391');
UPDATE sizes SET name = 'W39', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'fed89b68-652b-57f1-a179-790774ad5391';
INSERT INTO sizes (id, name, size_group_id) SELECT '8aa26b5d-3d59-5b25-a35b-ba6ef1c8cf45', 'W40', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '8aa26b5d-3d59-5b25-a35b-ba6ef1c8cf45');
UPDATE sizes SET name = 'W40', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '8aa26b5d-3d59-5b25-a35b-ba6ef1c8cf45';
INSERT INTO sizes (id, name, size_group_id) SELECT '00cc521c-f797-542b-ab8e-562ac185cb4c', 'W41', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '00cc521c-f797-542b-ab8e-562ac185cb4c');
UPDATE sizes SET name = 'W41', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '00cc521c-f797-542b-ab8e-562ac185cb4c';
INSERT INTO sizes (id, name, size_group_id) SELECT '7e76c764-632d-518f-87a0-9095b7e8a232', 'W42', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7e76c764-632d-518f-87a0-9095b7e8a232');
UPDATE sizes SET name = 'W42', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '7e76c764-632d-518f-87a0-9095b7e8a232';
INSERT INTO sizes (id, name, size_group_id) SELECT '25fbd2d1-8c1c-5865-8ac4-4b3d930cca3b', 'W43', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '25fbd2d1-8c1c-5865-8ac4-4b3d930cca3b');
UPDATE sizes SET name = 'W43', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '25fbd2d1-8c1c-5865-8ac4-4b3d930cca3b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'fa1895bb-56c2-5d8f-90bb-eb5427bdb55a', 'W44', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'fa1895bb-56c2-5d8f-90bb-eb5427bdb55a');
UPDATE sizes SET name = 'W44', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'fa1895bb-56c2-5d8f-90bb-eb5427bdb55a';
INSERT INTO sizes (id, name, size_group_id) SELECT '7951af38-7691-5106-ad9f-1e7acd4b9575', 'W45', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7951af38-7691-5106-ad9f-1e7acd4b9575');
UPDATE sizes SET name = 'W45', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '7951af38-7691-5106-ad9f-1e7acd4b9575';
INSERT INTO sizes (id, name, size_group_id) SELECT '13ba6d80-a087-5459-a08c-8cd7bd65d62b', 'W46', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '13ba6d80-a087-5459-a08c-8cd7bd65d62b');
UPDATE sizes SET name = 'W46', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '13ba6d80-a087-5459-a08c-8cd7bd65d62b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'fc4c6b2a-a39d-539e-9655-47f6e44d9322', 'W47', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'fc4c6b2a-a39d-539e-9655-47f6e44d9322');
UPDATE sizes SET name = 'W47', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'fc4c6b2a-a39d-539e-9655-47f6e44d9322';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b75a8be9-3b3a-59ef-863d-9d13710148a0', 'W48', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b75a8be9-3b3a-59ef-863d-9d13710148a0');
UPDATE sizes SET name = 'W48', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'b75a8be9-3b3a-59ef-863d-9d13710148a0';
INSERT INTO sizes (id, name, size_group_id) SELECT 'ba1aec68-338e-5ec2-bea7-927d214b1af2', 'W49', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'ba1aec68-338e-5ec2-bea7-927d214b1af2');
UPDATE sizes SET name = 'W49', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'ba1aec68-338e-5ec2-bea7-927d214b1af2';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f3e79a8e-19f0-5d2a-94be-3668e438653d', 'W50', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f3e79a8e-19f0-5d2a-94be-3668e438653d');
UPDATE sizes SET name = 'W50', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'f3e79a8e-19f0-5d2a-94be-3668e438653d';
INSERT INTO sizes (id, name, size_group_id) SELECT '7cf5c980-3058-576e-9cf2-199f6cad5a21', 'Autre taille', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7cf5c980-3058-576e-9cf2-199f6cad5a21');
UPDATE sizes SET name = 'Autre taille', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = '7cf5c980-3058-576e-9cf2-199f6cad5a21';
INSERT INTO sizes (id, name, size_group_id) SELECT '2251e8c7-29e0-5000-8d39-8f4bb69154a6', 'Autre taille', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2251e8c7-29e0-5000-8d39-8f4bb69154a6');
UPDATE sizes SET name = 'Autre taille', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '2251e8c7-29e0-5000-8d39-8f4bb69154a6';
INSERT INTO sizes (id, name, size_group_id) SELECT '2b1dcd45-8513-5fac-acb4-b197c80f9f1a', 'Autre taille', '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '2b1dcd45-8513-5fac-acb4-b197c80f9f1a');
UPDATE sizes SET name = 'Autre taille', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '2b1dcd45-8513-5fac-acb4-b197c80f9f1a';
INSERT INTO sizes (id, name, size_group_id) SELECT '7f6298c6-801c-53b4-80e8-ed8815ae742a', 'Prématuré', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7f6298c6-801c-53b4-80e8-ed8815ae742a');
UPDATE sizes SET name = 'Prématuré', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '7f6298c6-801c-53b4-80e8-ed8815ae742a';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f0ac506b-71ea-5b2a-80ea-20ad2762840e', 'Naissance', '96464d98-492e-580b-942c-52d2e900ea71' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f0ac506b-71ea-5b2a-80ea-20ad2762840e');
UPDATE sizes SET name = 'Naissance', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = 'f0ac506b-71ea-5b2a-80ea-20ad2762840e';
INSERT INTO sizes (id, name, size_group_id) SELECT '4abc238e-e353-55af-8b7b-0cf9017b90bb', '60 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '4abc238e-e353-55af-8b7b-0cf9017b90bb');
UPDATE sizes SET name = '60 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '4abc238e-e353-55af-8b7b-0cf9017b90bb';
INSERT INTO sizes (id, name, size_group_id) SELECT '0390a897-4080-59b2-92ea-129c561d8131', '65 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '0390a897-4080-59b2-92ea-129c561d8131');
UPDATE sizes SET name = '65 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '0390a897-4080-59b2-92ea-129c561d8131';
INSERT INTO sizes (id, name, size_group_id) SELECT '0e2a6b1c-03f6-5d48-a1fb-52c2863602de', '70 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '0e2a6b1c-03f6-5d48-a1fb-52c2863602de');
UPDATE sizes SET name = '70 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '0e2a6b1c-03f6-5d48-a1fb-52c2863602de';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f4dbbb71-169d-54c4-9c3d-b20194a0675b', '75 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f4dbbb71-169d-54c4-9c3d-b20194a0675b');
UPDATE sizes SET name = '75 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'f4dbbb71-169d-54c4-9c3d-b20194a0675b';
INSERT INTO sizes (id, name, size_group_id) SELECT '46d3ec84-9065-524d-9210-a32353c8aeb8', '80 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '46d3ec84-9065-524d-9210-a32353c8aeb8');
UPDATE sizes SET name = '80 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '46d3ec84-9065-524d-9210-a32353c8aeb8';
INSERT INTO sizes (id, name, size_group_id) SELECT 'da32c096-7bf5-5d78-bcf6-619dcd218c47', '85 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'da32c096-7bf5-5d78-bcf6-619dcd218c47');
UPDATE sizes SET name = '85 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'da32c096-7bf5-5d78-bcf6-619dcd218c47';
INSERT INTO sizes (id, name, size_group_id) SELECT '69d9ee74-e8b5-5f3c-b4ee-397566b95e43', '90 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '69d9ee74-e8b5-5f3c-b4ee-397566b95e43');
UPDATE sizes SET name = '90 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '69d9ee74-e8b5-5f3c-b4ee-397566b95e43';
INSERT INTO sizes (id, name, size_group_id) SELECT '40885d96-cb68-55a4-a846-43de01b1b19b', '95 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '40885d96-cb68-55a4-a846-43de01b1b19b');
UPDATE sizes SET name = '95 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '40885d96-cb68-55a4-a846-43de01b1b19b';
INSERT INTO sizes (id, name, size_group_id) SELECT 'c98d73a4-b468-5b30-aeb7-d67b952fbfdf', '100 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'c98d73a4-b468-5b30-aeb7-d67b952fbfdf');
UPDATE sizes SET name = '100 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'c98d73a4-b468-5b30-aeb7-d67b952fbfdf';
INSERT INTO sizes (id, name, size_group_id) SELECT '64dd4ec2-9064-55fe-aa92-7b4db700db7b', '105 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '64dd4ec2-9064-55fe-aa92-7b4db700db7b');
UPDATE sizes SET name = '105 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '64dd4ec2-9064-55fe-aa92-7b4db700db7b';
INSERT INTO sizes (id, name, size_group_id) SELECT '80a667fc-d289-5ee1-ac81-a6320fe4eec7', '110 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '80a667fc-d289-5ee1-ac81-a6320fe4eec7');
UPDATE sizes SET name = '110 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '80a667fc-d289-5ee1-ac81-a6320fe4eec7';
INSERT INTO sizes (id, name, size_group_id) SELECT '9c4b7f7c-fb59-5b59-b05e-8dc6abdc4e9a', '115 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '9c4b7f7c-fb59-5b59-b05e-8dc6abdc4e9a');
UPDATE sizes SET name = '115 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '9c4b7f7c-fb59-5b59-b05e-8dc6abdc4e9a';
INSERT INTO sizes (id, name, size_group_id) SELECT 'e5f4b34e-6692-528b-803e-ede36c206724', '120 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'e5f4b34e-6692-528b-803e-ede36c206724');
UPDATE sizes SET name = '120 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'e5f4b34e-6692-528b-803e-ede36c206724';
INSERT INTO sizes (id, name, size_group_id) SELECT '204b7409-8793-503f-8a67-5b667a4db16b', '125 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '204b7409-8793-503f-8a67-5b667a4db16b');
UPDATE sizes SET name = '125 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '204b7409-8793-503f-8a67-5b667a4db16b';
INSERT INTO sizes (id, name, size_group_id) SELECT '0acad06b-05b8-55e9-b4f8-adb91fcffa54', '130 cm', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '0acad06b-05b8-55e9-b4f8-adb91fcffa54');
UPDATE sizes SET name = '130 cm', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '0acad06b-05b8-55e9-b4f8-adb91fcffa54';
INSERT INTO sizes (id, name, size_group_id) SELECT '68081805-8535-5b33-a66a-821241a953a5', 'XS', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '68081805-8535-5b33-a66a-821241a953a5');
UPDATE sizes SET name = 'XS', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '68081805-8535-5b33-a66a-821241a953a5';
INSERT INTO sizes (id, name, size_group_id) SELECT '1fa3a56f-58f0-5887-a253-aa2a1ac392cb', 'S', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1fa3a56f-58f0-5887-a253-aa2a1ac392cb');
UPDATE sizes SET name = 'S', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '1fa3a56f-58f0-5887-a253-aa2a1ac392cb';
INSERT INTO sizes (id, name, size_group_id) SELECT '07ca51eb-479a-5180-bd62-542a41c035d5', 'M', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '07ca51eb-479a-5180-bd62-542a41c035d5');
UPDATE sizes SET name = 'M', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '07ca51eb-479a-5180-bd62-542a41c035d5';
INSERT INTO sizes (id, name, size_group_id) SELECT 'f0f289c8-aacf-5e72-8f78-c9ba848cbff7', 'L', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'f0f289c8-aacf-5e72-8f78-c9ba848cbff7');
UPDATE sizes SET name = 'L', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'f0f289c8-aacf-5e72-8f78-c9ba848cbff7';
INSERT INTO sizes (id, name, size_group_id) SELECT '24f779e3-5d97-51e1-bb84-0d400ae75db3', 'XL', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '24f779e3-5d97-51e1-bb84-0d400ae75db3');
UPDATE sizes SET name = 'XL', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '24f779e3-5d97-51e1-bb84-0d400ae75db3';
INSERT INTO sizes (id, name, size_group_id) SELECT '3da220fa-17ac-5570-933d-e72c6e22f56a', 'Taille unique', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '3da220fa-17ac-5570-933d-e72c6e22f56a');
UPDATE sizes SET name = 'Taille unique', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '3da220fa-17ac-5570-933d-e72c6e22f56a';
INSERT INTO sizes (id, name, size_group_id) SELECT '22a68c1a-bcd5-5030-9fdb-c5e66c9413ea', 'Autre taille', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '22a68c1a-bcd5-5030-9fdb-c5e66c9413ea');
UPDATE sizes SET name = 'Autre taille', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '22a68c1a-bcd5-5030-9fdb-c5e66c9413ea';
INSERT INTO sizes (id, name, size_group_id) SELECT '9c36c277-9c65-582d-a375-1e0539311305', 'XS', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '9c36c277-9c65-582d-a375-1e0539311305');
UPDATE sizes SET name = 'XS', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '9c36c277-9c65-582d-a375-1e0539311305';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b9692e32-d21f-5028-817f-776608dc6bbb', 'S', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b9692e32-d21f-5028-817f-776608dc6bbb');
UPDATE sizes SET name = 'S', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = 'b9692e32-d21f-5028-817f-776608dc6bbb';
INSERT INTO sizes (id, name, size_group_id) SELECT '7423bdb7-6290-5414-8a72-fe5806d2dd62', 'M', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7423bdb7-6290-5414-8a72-fe5806d2dd62');
UPDATE sizes SET name = 'M', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '7423bdb7-6290-5414-8a72-fe5806d2dd62';
INSERT INTO sizes (id, name, size_group_id) SELECT '6b04636a-ebc4-5344-855b-8921baff8853', 'L', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '6b04636a-ebc4-5344-855b-8921baff8853');
UPDATE sizes SET name = 'L', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '6b04636a-ebc4-5344-855b-8921baff8853';
INSERT INTO sizes (id, name, size_group_id) SELECT '76475bc5-a6be-54ba-819c-0a79ac16fb3f', 'XL', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '76475bc5-a6be-54ba-819c-0a79ac16fb3f');
UPDATE sizes SET name = 'XL', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '76475bc5-a6be-54ba-819c-0a79ac16fb3f';
INSERT INTO sizes (id, name, size_group_id) SELECT '42a2100a-b6ad-507d-a8e7-572d02a9c966', 'Taille unique', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '42a2100a-b6ad-507d-a8e7-572d02a9c966');
UPDATE sizes SET name = 'Taille unique', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '42a2100a-b6ad-507d-a8e7-572d02a9c966';
INSERT INTO sizes (id, name, size_group_id) SELECT '8941e4cd-705d-52df-a7c4-73233df74f7d', 'Autre taille', '705c5a80-1e19-5487-8eac-19459949de00' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '8941e4cd-705d-52df-a7c4-73233df74f7d');
UPDATE sizes SET name = 'Autre taille', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '8941e4cd-705d-52df-a7c4-73233df74f7d';
INSERT INTO sizes (id, name, size_group_id) SELECT '5a0507a2-ac22-54ea-883d-833f8689c5a6', 'XS', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '5a0507a2-ac22-54ea-883d-833f8689c5a6');
UPDATE sizes SET name = 'XS', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '5a0507a2-ac22-54ea-883d-833f8689c5a6';
INSERT INTO sizes (id, name, size_group_id) SELECT '7bdd4489-a7af-58bb-a672-499452b33207', 'S', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '7bdd4489-a7af-58bb-a672-499452b33207');
UPDATE sizes SET name = 'S', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '7bdd4489-a7af-58bb-a672-499452b33207';
INSERT INTO sizes (id, name, size_group_id) SELECT 'b2dabded-3033-5a7a-a5b2-2b04ea8bf56c', 'M', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'b2dabded-3033-5a7a-a5b2-2b04ea8bf56c');
UPDATE sizes SET name = 'M', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = 'b2dabded-3033-5a7a-a5b2-2b04ea8bf56c';
INSERT INTO sizes (id, name, size_group_id) SELECT '64917578-17cc-54d0-8731-8c96b441f825', 'L', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '64917578-17cc-54d0-8731-8c96b441f825');
UPDATE sizes SET name = 'L', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '64917578-17cc-54d0-8731-8c96b441f825';
INSERT INTO sizes (id, name, size_group_id) SELECT '63da3b67-8117-5701-aeec-7a7074cc5fc8', 'XL', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '63da3b67-8117-5701-aeec-7a7074cc5fc8');
UPDATE sizes SET name = 'XL', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '63da3b67-8117-5701-aeec-7a7074cc5fc8';
INSERT INTO sizes (id, name, size_group_id) SELECT 'd448506e-fe0c-57cf-90a8-c3ede21d2179', 'Taille unique', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = 'd448506e-fe0c-57cf-90a8-c3ede21d2179');
UPDATE sizes SET name = 'Taille unique', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = 'd448506e-fe0c-57cf-90a8-c3ede21d2179';
INSERT INTO sizes (id, name, size_group_id) SELECT '1b2d4739-a6ce-5a6d-8f98-2513f986e976', 'Autre taille', '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE NOT EXISTS (SELECT 1 FROM sizes WHERE id = '1b2d4739-a6ce-5a6d-8f98-2513f986e976');
UPDATE sizes SET name = 'Autre taille', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '1b2d4739-a6ce-5a6d-8f98-2513f986e976';

-- cities
INSERT INTO cities (id, name) SELECT '13464db4-c346-5288-b2ae-97df62bc79ca', 'Casablanca' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '13464db4-c346-5288-b2ae-97df62bc79ca');
UPDATE cities SET name = 'Casablanca' WHERE id = '13464db4-c346-5288-b2ae-97df62bc79ca';
INSERT INTO cities (id, name) SELECT '5a8cab81-4b6c-5458-a780-2256900eb417', 'Rabat' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '5a8cab81-4b6c-5458-a780-2256900eb417');
UPDATE cities SET name = 'Rabat' WHERE id = '5a8cab81-4b6c-5458-a780-2256900eb417';
INSERT INTO cities (id, name) SELECT 'e4608262-a817-50bd-8a63-6858a0cc7f03', 'Marrakech' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = 'e4608262-a817-50bd-8a63-6858a0cc7f03');
UPDATE cities SET name = 'Marrakech' WHERE id = 'e4608262-a817-50bd-8a63-6858a0cc7f03';
INSERT INTO cities (id, name) SELECT '44363803-db7e-5fc2-abf8-a7f87c649b2d', 'Tanger' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '44363803-db7e-5fc2-abf8-a7f87c649b2d');
UPDATE cities SET name = 'Tanger' WHERE id = '44363803-db7e-5fc2-abf8-a7f87c649b2d';
INSERT INTO cities (id, name) SELECT '39d4346f-bb66-5a49-a328-1fd3814c3b53', 'Fès' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '39d4346f-bb66-5a49-a328-1fd3814c3b53');
UPDATE cities SET name = 'Fès' WHERE id = '39d4346f-bb66-5a49-a328-1fd3814c3b53';
INSERT INTO cities (id, name) SELECT 'c8fe57a9-6261-5401-a00e-291f5c64447a', 'Agadir' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = 'c8fe57a9-6261-5401-a00e-291f5c64447a');
UPDATE cities SET name = 'Agadir' WHERE id = 'c8fe57a9-6261-5401-a00e-291f5c64447a';
INSERT INTO cities (id, name) SELECT '4ac3edcb-285f-51d6-9718-825505a77e3a', 'Meknès' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '4ac3edcb-285f-51d6-9718-825505a77e3a');
UPDATE cities SET name = 'Meknès' WHERE id = '4ac3edcb-285f-51d6-9718-825505a77e3a';
INSERT INTO cities (id, name) SELECT '90aca3cc-7694-5004-b3c0-1786ddc6b88b', 'Oujda' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '90aca3cc-7694-5004-b3c0-1786ddc6b88b');
UPDATE cities SET name = 'Oujda' WHERE id = '90aca3cc-7694-5004-b3c0-1786ddc6b88b';
INSERT INTO cities (id, name) SELECT '48cdd6b1-6126-5cef-8b56-c5d93d38b16c', 'Kenitra' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '48cdd6b1-6126-5cef-8b56-c5d93d38b16c');
UPDATE cities SET name = 'Kenitra' WHERE id = '48cdd6b1-6126-5cef-8b56-c5d93d38b16c';
INSERT INTO cities (id, name) SELECT 'ec185caf-78fc-53b1-9e5e-8d8f99b1f21f', 'Tétouan' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = 'ec185caf-78fc-53b1-9e5e-8d8f99b1f21f');
UPDATE cities SET name = 'Tétouan' WHERE id = 'ec185caf-78fc-53b1-9e5e-8d8f99b1f21f';
INSERT INTO cities (id, name) SELECT '435f0b89-a58e-561a-954c-5e6c65a1e406', 'Safi' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '435f0b89-a58e-561a-954c-5e6c65a1e406');
UPDATE cities SET name = 'Safi' WHERE id = '435f0b89-a58e-561a-954c-5e6c65a1e406';
INSERT INTO cities (id, name) SELECT '902454d1-4b29-51af-acbd-6d233314d67b', 'Mohammédia' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE id = '902454d1-4b29-51af-acbd-6d233314d67b');
UPDATE cities SET name = 'Mohammédia' WHERE id = '902454d1-4b29-51af-acbd-6d233314d67b';

-- Broad marketplace categories. Existing category IDs are retained where possible.
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '65aeaf07-a302-5175-be2d-950d4b5142b3', 'Femme', 'https://images.unsplash.com/photo-1761896898277-5141377f2a12?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=700', NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '65aeaf07-a302-5175-be2d-950d4b5142b3');
UPDATE categories SET name = 'Femme', size_group_id = NULL WHERE id = '65aeaf07-a302-5175-be2d-950d4b5142b3';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd0b99ed7-fac8-521e-b512-2832dbe9f174', 'Robes', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174');
UPDATE categories SET name = 'Robes', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd60d476e-acde-5af8-87d3-2c6d48eb4996', 'T-shirts, tops et polos', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE categories SET name = 'T-shirts, tops et polos', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '77bc4938-c572-56f8-9797-ec9bb3240b80', 'Pantalons et shorts', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '77bc4938-c572-56f8-9797-ec9bb3240b80');
UPDATE categories SET name = 'Pantalons et shorts', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '77bc4938-c572-56f8-9797-ec9bb3240b80';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd103cc35-670f-5520-b0b8-846cb4950a02', 'Vestes, blazers et manteaux', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE categories SET name = 'Vestes, blazers et manteaux', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd103cc35-670f-5520-b0b8-846cb4950a02';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'deeef5b3-1228-51a7-85e5-0f6cc5406354', 'Chaussures', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', 'b77d5d2e-085c-53a0-a81b-83523baf001d', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE categories SET name = 'Chaussures', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1', 'Sacs', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE categories SET name = 'Sacs', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3', 'Pulls, sweats et gilets', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3');
UPDATE categories SET name = 'Pulls, sweats et gilets', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'e1611b2b-87df-59d3-9d21-30cafe2fe9a8', 'Jeans', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'e1611b2b-87df-59d3-9d21-30cafe2fe9a8');
UPDATE categories SET name = 'Jeans', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'e1611b2b-87df-59d3-9d21-30cafe2fe9a8';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd9d457e0-4e94-5081-88fe-319f575d3a98', 'Jupes', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd9d457e0-4e94-5081-88fe-319f575d3a98');
UPDATE categories SET name = 'Jupes', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd9d457e0-4e94-5081-88fe-319f575d3a98';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89', 'Ensembles', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89');
UPDATE categories SET name = 'Ensembles', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '5af2861a-6177-553b-9c30-299e856bd81b', 'Vêtements de sport', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '5af2861a-6177-553b-9c30-299e856bd81b');
UPDATE categories SET name = 'Vêtements de sport', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '5af2861a-6177-553b-9c30-299e856bd81b';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '50bb5384-fa8b-5b56-9345-c610decdfeeb', 'Pyjamas et sous-vêtements', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '50bb5384-fa8b-5b56-9345-c610decdfeeb');
UPDATE categories SET name = 'Pyjamas et sous-vêtements', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '50bb5384-fa8b-5b56-9345-c610decdfeeb';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '6cf54c8e-d29c-555d-8a29-330d75d52b37', 'Accessoires', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE categories SET name = 'Accessoires', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '6cf54c8e-d29c-555d-8a29-330d75d52b37';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '73f7ef55-1b4e-5eb7-a94f-d8ccf962d64c', 'Ceintures', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '73f7ef55-1b4e-5eb7-a94f-d8ccf962d64c');
UPDATE categories SET name = 'Ceintures', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = '73f7ef55-1b4e-5eb7-a94f-d8ccf962d64c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'f559655f-55b8-5198-af15-404c7c585c9c', 'Chapeaux, casquettes et bonnets', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '705c5a80-1e19-5487-8eac-19459949de00', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'f559655f-55b8-5198-af15-404c7c585c9c');
UPDATE categories SET name = 'Chapeaux, casquettes et bonnets', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = 'f559655f-55b8-5198-af15-404c7c585c9c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'c6825ff5-b146-5394-bae2-569f2c3c5e3c', 'Gants', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '7007afe3-61cd-5a14-88b0-2f057dceb9e8', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c');
UPDATE categories SET name = 'Gants', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'Homme', 'https://images.unsplash.com/photo-1761896904104-334cad0f5d3b?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=700', NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'e3b8db94-6af2-5246-96be-56d25820dd1c');
UPDATE categories SET name = 'Homme', size_group_id = NULL WHERE id = 'e3b8db94-6af2-5246-96be-56d25820dd1c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd', 'T-shirts, tops et polos', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd');
UPDATE categories SET name = 'T-shirts, tops et polos', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '593e57cc-611c-53b8-98ab-6ce146a18643', 'Chemises et blouses', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '593e57cc-611c-53b8-98ab-6ce146a18643');
UPDATE categories SET name = 'Chemises et blouses', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '593e57cc-611c-53b8-98ab-6ce146a18643';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '785d358e-f083-51a2-8957-a5e8c84bc933', 'Pantalons et shorts', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '785d358e-f083-51a2-8957-a5e8c84bc933');
UPDATE categories SET name = 'Pantalons et shorts', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '785d358e-f083-51a2-8957-a5e8c84bc933';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '280b284f-d0b7-54bd-be5c-25bbe8771265', 'Pulls, sweats et gilets', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '280b284f-d0b7-54bd-be5c-25bbe8771265');
UPDATE categories SET name = 'Pulls, sweats et gilets', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '280b284f-d0b7-54bd-be5c-25bbe8771265';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '643090d8-7434-5c37-92a8-29372ae7340a', 'Chaussures', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'b77d5d2e-085c-53a0-a81b-83523baf001d', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE categories SET name = 'Chaussures', size_group_id = 'b77d5d2e-085c-53a0-a81b-83523baf001d' WHERE id = '643090d8-7434-5c37-92a8-29372ae7340a';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'b89e2afd-f253-565b-b8c8-e7d9f140bf36', 'Accessoires', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE categories SET name = 'Accessoires', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'abfa8737-ec8f-5851-91d9-442c8d28f3f1', 'Vestes, blazers et manteaux', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE categories SET name = 'Vestes, blazers et manteaux', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd219ab3d-59fd-50f0-b47b-31dbfad8153a', 'Jeans', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd219ab3d-59fd-50f0-b47b-31dbfad8153a');
UPDATE categories SET name = 'Jeans', size_group_id = 'ad8bd718-7c2a-5a0b-b57b-1fa3df2dd14c' WHERE id = 'd219ab3d-59fd-50f0-b47b-31dbfad8153a';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7', 'Costumes', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7');
UPDATE categories SET name = 'Costumes', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'a4ddd3ab-d878-5993-a191-c09bf1839df2', 'Vêtements de sport', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2');
UPDATE categories SET name = 'Vêtements de sport', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd85f47ee-c40b-51ec-b098-e982278439af', 'Pyjamas et sous-vêtements', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd85f47ee-c40b-51ec-b098-e982278439af');
UPDATE categories SET name = 'Pyjamas et sous-vêtements', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'd85f47ee-c40b-51ec-b098-e982278439af';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'ea8796e2-d220-52ff-b962-63afc9a6a25c', 'Ceintures', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'ea8796e2-d220-52ff-b962-63afc9a6a25c');
UPDATE categories SET name = 'Ceintures', size_group_id = '7ef9eff5-d1f7-59b0-a0cf-8467aefcf250' WHERE id = 'ea8796e2-d220-52ff-b962-63afc9a6a25c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '6d31aaac-96b8-5660-93b6-ee7ffc6ce178', 'Chapeaux, casquettes et bonnets', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '705c5a80-1e19-5487-8eac-19459949de00', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178');
UPDATE categories SET name = 'Chapeaux, casquettes et bonnets', size_group_id = '705c5a80-1e19-5487-8eac-19459949de00' WHERE id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '19c23238-eba7-5497-8f0f-b64c5720faf6', 'Gants', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '7007afe3-61cd-5a14-88b0-2f057dceb9e8', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '19c23238-eba7-5497-8f0f-b64c5720faf6');
UPDATE categories SET name = 'Gants', size_group_id = '7007afe3-61cd-5a14-88b0-2f057dceb9e8' WHERE id = '19c23238-eba7-5497-8f0f-b64c5720faf6';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '353aa7ae-e01d-545c-99c2-9fa3264fdb9b', 'Sacs', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE categories SET name = 'Sacs', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', 'Luxe', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=700', NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'f14aa0c7-faaf-5535-be28-109d2b1df2b8');
UPDATE categories SET name = 'Luxe', size_group_id = NULL WHERE id = 'f14aa0c7-faaf-5535-be28-109d2b1df2b8';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '08a47b9a-2408-57ad-957e-684eab8c8c75', 'Sacs de luxe', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '08a47b9a-2408-57ad-957e-684eab8c8c75');
UPDATE categories SET name = 'Sacs de luxe', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '08a47b9a-2408-57ad-957e-684eab8c8c75';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'a4bb4124-87c0-5790-b79b-afd3bf4d3cf9', 'Accessoires', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'a4bb4124-87c0-5790-b79b-afd3bf4d3cf9');
UPDATE categories SET name = 'Accessoires', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'a4bb4124-87c0-5790-b79b-afd3bf4d3cf9';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'cc34507b-e882-597d-aca1-01a7e7ac305c', 'Vintage', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', '439daea4-0ef8-5395-bf7a-8f9941c816b3', 'Collection de vêtements vintage' WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'cc34507b-e882-597d-aca1-01a7e7ac305c');
UPDATE categories SET name = 'Vintage', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'cc34507b-e882-597d-aca1-01a7e7ac305c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '8bc5adc0-be8e-549f-8969-7274fb255aa5', 'Haut de gamme', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '8bc5adc0-be8e-549f-8969-7274fb255aa5');
UPDATE categories SET name = 'Haut de gamme', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '8bc5adc0-be8e-549f-8969-7274fb255aa5';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'a1891dca-c89f-54c4-be8f-747e7f79de78', 'Créateurs', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'a1891dca-c89f-54c4-be8f-747e7f79de78');
UPDATE categories SET name = 'Créateurs', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'a1891dca-c89f-54c4-be8f-747e7f79de78';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '52ad16f8-da34-5c42-93f7-4a644171eb27', 'Archives', NULL, 'f14aa0c7-faaf-5535-be28-109d2b1df2b8', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '52ad16f8-da34-5c42-93f7-4a644171eb27');
UPDATE categories SET name = 'Archives', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '52ad16f8-da34-5c42-93f7-4a644171eb27';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', 'Enfant', 'https://images.unsplash.com/photo-1632469188022-b5db09a70fbc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=700', NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736');
UPDATE categories SET name = 'Enfant', size_group_id = NULL WHERE id = 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '75aa697f-4e6f-543e-a566-ec856f75d702', 'Bébé', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', '96464d98-492e-580b-942c-52d2e900ea71', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE categories SET name = 'Bébé', size_group_id = '96464d98-492e-580b-942c-52d2e900ea71' WHERE id = '75aa697f-4e6f-543e-a566-ec856f75d702';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'f16831eb-3079-58a6-8a61-f5f6003115bf', 'Fille', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', '7f0371e6-b070-57c1-af49-811a0f2c8078', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'f16831eb-3079-58a6-8a61-f5f6003115bf');
UPDATE categories SET name = 'Fille', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = 'f16831eb-3079-58a6-8a61-f5f6003115bf';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '1fa34196-7138-51b0-bf5c-ef1a1b783c42', 'Garçon', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', '7f0371e6-b070-57c1-af49-811a0f2c8078', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '1fa34196-7138-51b0-bf5c-ef1a1b783c42');
UPDATE categories SET name = 'Garçon', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '1fa34196-7138-51b0-bf5c-ef1a1b783c42';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'a5875435-98bf-5825-a654-a1bb44d428cf', 'Chaussures', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', 'a4a6213c-b072-5cdf-b1ba-d25971b705c4', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE categories SET name = 'Chaussures', size_group_id = 'a4a6213c-b072-5cdf-b1ba-d25971b705c4' WHERE id = 'a5875435-98bf-5825-a654-a1bb44d428cf';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '506d68f9-ecc4-5dc0-9355-2e74bca09331', 'Jouets', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE categories SET name = 'Jouets', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '506d68f9-ecc4-5dc0-9355-2e74bca09331';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '2975fe84-ef33-5056-b272-17f5c5c73333', 'Périculture', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE categories SET name = 'Périculture', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '2975fe84-ef33-5056-b272-17f5c5c73333';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '9eff6390-c2fc-5683-a360-93c99c82eb96', 'Accessoires', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '9eff6390-c2fc-5683-a360-93c99c82eb96');
UPDATE categories SET name = 'Accessoires', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '9eff6390-c2fc-5683-a360-93c99c82eb96';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '6b9d8067-b786-526f-99b6-3df2ab9be269', 'Vêtements enfant', NULL, 'd4afc3c3-ce8c-58e4-b5c4-5860ffdbd736', '7f0371e6-b070-57c1-af49-811a0f2c8078', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE categories SET name = 'Vêtements enfant', size_group_id = '7f0371e6-b070-57c1-af49-811a0f2c8078' WHERE id = '6b9d8067-b786-526f-99b6-3df2ab9be269';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'Maison', '', NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3');
UPDATE categories SET name = 'Maison', size_group_id = NULL WHERE id = 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '00ff13fd-a673-5dfc-b4d7-ce135f15385b', 'Tapis', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '00ff13fd-a673-5dfc-b4d7-ce135f15385b');
UPDATE categories SET name = 'Tapis', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '00ff13fd-a673-5dfc-b4d7-ce135f15385b';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd403a08c-1fa8-5e00-9ba6-8342ebad9b83', 'Coussins', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd403a08c-1fa8-5e00-9ba6-8342ebad9b83');
UPDATE categories SET name = 'Coussins', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'd403a08c-1fa8-5e00-9ba6-8342ebad9b83';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a', 'Art de la table', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a');
UPDATE categories SET name = 'Art de la table', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920', 'Déco', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE categories SET name = 'Déco', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'b1cf3c8d-f508-59c3-8531-2cfa0fd6be5e', 'Luminaires', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'b1cf3c8d-f508-59c3-8531-2cfa0fd6be5e');
UPDATE categories SET name = 'Luminaires', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'b1cf3c8d-f508-59c3-8531-2cfa0fd6be5e';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '8f8dd627-ff87-57bd-97fa-6a88d63a2755', 'Artisanat', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '8f8dd627-ff87-57bd-97fa-6a88d63a2755');
UPDATE categories SET name = 'Artisanat', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '8f8dd627-ff87-57bd-97fa-6a88d63a2755';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '059a92f6-58ae-5ae1-bbc8-82b39bec87d2', 'Linge de maison', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2');
UPDATE categories SET name = 'Linge de maison', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '4c934d61-f71b-5d67-a57f-cd2cb0f6b130', 'Rangement', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130');
UPDATE categories SET name = 'Rangement', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd5392ef0-f7af-508b-9b4c-15e1ed582fb1', 'Livres', NULL, 'eee5ceaa-736d-53b4-85cd-6ca878ad3fb3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd5392ef0-f7af-508b-9b4c-15e1ed582fb1');
UPDATE categories SET name = 'Livres', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'd5392ef0-f7af-508b-9b4c-15e1ed582fb1';

-- Additional Homme and Femme categories
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'ffe035e1-949c-5fc7-939c-77226ba931ed', 'Maillots de bain', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'ffe035e1-949c-5fc7-939c-77226ba931ed');
UPDATE categories SET name = 'Maillots de bain', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'ffe035e1-949c-5fc7-939c-77226ba931ed';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd564afe6-338a-5fcb-acdb-5786b3db0648', 'Montres et bijoux', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd564afe6-338a-5fcb-acdb-5786b3db0648');
UPDATE categories SET name = 'Montres et bijoux', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = 'd564afe6-338a-5fcb-acdb-5786b3db0648';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '9e468a12-7ff6-57e7-b235-f693d3657845', 'Chemises et blouses', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '9e468a12-7ff6-57e7-b235-f693d3657845');
UPDATE categories SET name = 'Chemises et blouses', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '9e468a12-7ff6-57e7-b235-f693d3657845';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a', 'Combinaisons', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a');
UPDATE categories SET name = 'Combinaisons', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5', 'Lingerie', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5');
UPDATE categories SET name = 'Lingerie', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'c7588265-314b-5212-99d6-4f7ec483109e', 'Vêtements de maternité', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'c7588265-314b-5212-99d6-4f7ec483109e');
UPDATE categories SET name = 'Vêtements de maternité', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'c7588265-314b-5212-99d6-4f7ec483109e';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '3e478d74-a08e-5731-9707-42867984af83', 'Maillots de bain', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '3e478d74-a08e-5731-9707-42867984af83');
UPDATE categories SET name = 'Maillots de bain', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = '3e478d74-a08e-5731-9707-42867984af83';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '09605735-0280-5add-bd7f-d49ad83746ce', 'Montres et bijoux', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '09605735-0280-5add-bd7f-d49ad83746ce');
UPDATE categories SET name = 'Montres et bijoux', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '09605735-0280-5add-bd7f-d49ad83746ce';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'c3ebd422-772b-5da4-a10d-c28b8a4d5a7a', 'Ensembles', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'c3ebd422-772b-5da4-a10d-c28b8a4d5a7a');
UPDATE categories SET name = 'Ensembles', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'c3ebd422-772b-5da4-a10d-c28b8a4d5a7a';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '4875829f-51e7-59fe-885d-3f4294256486', 'Cravates et nœuds papillon', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '4875829f-51e7-59fe-885d-3f4294256486');
UPDATE categories SET name = 'Cravates et nœuds papillon', size_group_id = 'a35a3d80-8714-54ce-b5d2-fe4d5afe2622' WHERE id = '4875829f-51e7-59fe-885d-3f4294256486';

INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '007f84bb-af39-5fc3-bde5-7d8f3399237d', 'Beauté', NULL, NULL, NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '007f84bb-af39-5fc3-bde5-7d8f3399237d');
UPDATE categories SET name = 'Beauté', size_group_id = NULL WHERE id = '007f84bb-af39-5fc3-bde5-7d8f3399237d';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '7d796c9c-d055-588f-b6c6-01c4b763c374', 'Parfums', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '7d796c9c-d055-588f-b6c6-01c4b763c374');
UPDATE categories SET name = 'Parfums', size_group_id = NULL WHERE id = '7d796c9c-d055-588f-b6c6-01c4b763c374';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'c35d5d8e-a0b9-53dd-8c39-0c71274c47c7', 'Maquillage', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'c35d5d8e-a0b9-53dd-8c39-0c71274c47c7');
UPDATE categories SET name = 'Maquillage', size_group_id = NULL WHERE id = 'c35d5d8e-a0b9-53dd-8c39-0c71274c47c7';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '9301401c-51f8-5df2-9b5f-da040843c1fb', 'Soins du visage', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '9301401c-51f8-5df2-9b5f-da040843c1fb');
UPDATE categories SET name = 'Soins du visage', size_group_id = NULL WHERE id = '9301401c-51f8-5df2-9b5f-da040843c1fb';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '57b2f582-e80e-5ff5-b2a2-5fcb5cbf91b4', 'Soins du corps', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '57b2f582-e80e-5ff5-b2a2-5fcb5cbf91b4');
UPDATE categories SET name = 'Soins du corps', size_group_id = NULL WHERE id = '57b2f582-e80e-5ff5-b2a2-5fcb5cbf91b4';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT '965e0034-e693-59b9-887f-cce68b482c71', 'Soins des cheveux', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = '965e0034-e693-59b9-887f-cce68b482c71');
UPDATE categories SET name = 'Soins des cheveux', size_group_id = NULL WHERE id = '965e0034-e693-59b9-887f-cce68b482c71';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'b2c1dab3-6fef-5c82-a580-96ee210dc75c', 'Appareils de beauté', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'b2c1dab3-6fef-5c82-a580-96ee210dc75c');
UPDATE categories SET name = 'Appareils de beauté', size_group_id = NULL WHERE id = 'b2c1dab3-6fef-5c82-a580-96ee210dc75c';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'd9ed9633-ffa8-5e1d-99b8-8c6a20b92ef3', 'Accessoires de beauté', NULL, '007f84bb-af39-5fc3-bde5-7d8f3399237d', NULL, NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'd9ed9633-ffa8-5e1d-99b8-8c6a20b92ef3');
UPDATE categories SET name = 'Accessoires de beauté', size_group_id = NULL WHERE id = 'd9ed9633-ffa8-5e1d-99b8-8c6a20b92ef3';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b', 'Vêtements traditionnels', NULL, '65aeaf07-a302-5175-be2d-950d4b5142b3', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE categories SET name = 'Vêtements traditionnels', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b';
INSERT INTO categories (id, name, image, parent_id, size_group_id, description) SELECT 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5', 'Vêtements traditionnels', NULL, 'e3b8db94-6af2-5246-96be-56d25820dd1c', '439daea4-0ef8-5395-bf7a-8f9941c816b3', NULL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5');
UPDATE categories SET name = 'Vêtements traditionnels', size_group_id = '439daea4-0ef8-5395-bf7a-8f9941c816b3' WHERE id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5';

-- Merge previously offered detailed item types into their broader category.
-- Move listings and deduplicate preferences before removing each obsolete category.
-- 'Manteaux' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'a320ab9a-5d8b-5942-ab2e-73b9519e605b';
DELETE FROM user_preferences WHERE category_id = 'a320ab9a-5d8b-5942-ab2e-73b9519e605b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'a320ab9a-5d8b-5942-ab2e-73b9519e605b';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = 'a320ab9a-5d8b-5942-ab2e-73b9519e605b';
DELETE FROM categories WHERE id = 'a320ab9a-5d8b-5942-ab2e-73b9519e605b';
-- 'Shorts' -> 'Pantalons et shorts'
UPDATE listings SET category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE category_id = 'fb3d163d-5140-53fc-90a6-9b73f763542b';
DELETE FROM user_preferences WHERE category_id = 'fb3d163d-5140-53fc-90a6-9b73f763542b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80');
UPDATE user_preferences SET category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE category_id = 'fb3d163d-5140-53fc-90a6-9b73f763542b';
UPDATE categories SET parent_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE parent_id = 'fb3d163d-5140-53fc-90a6-9b73f763542b';
DELETE FROM categories WHERE id = 'fb3d163d-5140-53fc-90a6-9b73f763542b';
-- 'Maillots de bain' -> 'Maillots de bain'
UPDATE listings SET category_id = 'ffe035e1-949c-5fc7-939c-77226ba931ed' WHERE category_id = '9e5161ab-b7e2-55d4-a5c3-0aa683aa7fd8';
DELETE FROM user_preferences WHERE category_id = '9e5161ab-b7e2-55d4-a5c3-0aa683aa7fd8' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ffe035e1-949c-5fc7-939c-77226ba931ed');
UPDATE user_preferences SET category_id = 'ffe035e1-949c-5fc7-939c-77226ba931ed' WHERE category_id = '9e5161ab-b7e2-55d4-a5c3-0aa683aa7fd8';
UPDATE categories SET parent_id = 'ffe035e1-949c-5fc7-939c-77226ba931ed' WHERE parent_id = '9e5161ab-b7e2-55d4-a5c3-0aa683aa7fd8';
DELETE FROM categories WHERE id = '9e5161ab-b7e2-55d4-a5c3-0aa683aa7fd8';
-- 'Baskets' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '465a4d96-949a-5ce0-bce9-e8c7d934e40a';
DELETE FROM user_preferences WHERE category_id = '465a4d96-949a-5ce0-bce9-e8c7d934e40a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '465a4d96-949a-5ce0-bce9-e8c7d934e40a';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '465a4d96-949a-5ce0-bce9-e8c7d934e40a';
DELETE FROM categories WHERE id = '465a4d96-949a-5ce0-bce9-e8c7d934e40a';
-- 'Bottes et bottines' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '48c19fae-fcbd-5e7a-ae52-1bb425cf8fd1';
DELETE FROM user_preferences WHERE category_id = '48c19fae-fcbd-5e7a-ae52-1bb425cf8fd1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '48c19fae-fcbd-5e7a-ae52-1bb425cf8fd1';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '48c19fae-fcbd-5e7a-ae52-1bb425cf8fd1';
DELETE FROM categories WHERE id = '48c19fae-fcbd-5e7a-ae52-1bb425cf8fd1';
-- 'Sandales et claquettes' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '231b12f3-eef5-561a-a6d5-012cca1bec91';
DELETE FROM user_preferences WHERE category_id = '231b12f3-eef5-561a-a6d5-012cca1bec91' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '231b12f3-eef5-561a-a6d5-012cca1bec91';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '231b12f3-eef5-561a-a6d5-012cca1bec91';
DELETE FROM categories WHERE id = '231b12f3-eef5-561a-a6d5-012cca1bec91';
-- 'Mocassins et chaussures de ville' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '9a02b896-3fd7-5856-8750-ae2f6dc70819';
DELETE FROM user_preferences WHERE category_id = '9a02b896-3fd7-5856-8750-ae2f6dc70819' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '9a02b896-3fd7-5856-8750-ae2f6dc70819';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '9a02b896-3fd7-5856-8750-ae2f6dc70819';
DELETE FROM categories WHERE id = '9a02b896-3fd7-5856-8750-ae2f6dc70819';
-- 'Sacs à dos' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'a67742d1-f86a-5966-8c4a-6839bd83b002';
DELETE FROM user_preferences WHERE category_id = 'a67742d1-f86a-5966-8c4a-6839bd83b002' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'a67742d1-f86a-5966-8c4a-6839bd83b002';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = 'a67742d1-f86a-5966-8c4a-6839bd83b002';
DELETE FROM categories WHERE id = 'a67742d1-f86a-5966-8c4a-6839bd83b002';
-- 'Bijoux' -> 'Montres et bijoux'
UPDATE listings SET category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE category_id = '6fa12698-b3ba-50e3-9f3e-0ff34c1dcb08';
DELETE FROM user_preferences WHERE category_id = '6fa12698-b3ba-50e3-9f3e-0ff34c1dcb08' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648');
UPDATE user_preferences SET category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE category_id = '6fa12698-b3ba-50e3-9f3e-0ff34c1dcb08';
UPDATE categories SET parent_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE parent_id = '6fa12698-b3ba-50e3-9f3e-0ff34c1dcb08';
DELETE FROM categories WHERE id = '6fa12698-b3ba-50e3-9f3e-0ff34c1dcb08';
-- 'Lunettes de soleil' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '5b43f390-a6a7-59d7-bf67-9f77ddd1f90a';
DELETE FROM user_preferences WHERE category_id = '5b43f390-a6a7-59d7-bf67-9f77ddd1f90a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '5b43f390-a6a7-59d7-bf67-9f77ddd1f90a';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '5b43f390-a6a7-59d7-bf67-9f77ddd1f90a';
DELETE FROM categories WHERE id = '5b43f390-a6a7-59d7-bf67-9f77ddd1f90a';
-- 'Écharpes et foulards' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '807c5760-b458-5979-9cda-a80c085dfc98';
DELETE FROM user_preferences WHERE category_id = '807c5760-b458-5979-9cda-a80c085dfc98' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '807c5760-b458-5979-9cda-a80c085dfc98';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '807c5760-b458-5979-9cda-a80c085dfc98';
DELETE FROM categories WHERE id = '807c5760-b458-5979-9cda-a80c085dfc98';
-- 'Pulls et gilets' -> 'Pulls, sweats et gilets'
UPDATE listings SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = '0846655b-92f6-518e-8a29-4a2c9cb660c4';
DELETE FROM user_preferences WHERE category_id = '0846655b-92f6-518e-8a29-4a2c9cb660c4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265');
UPDATE user_preferences SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = '0846655b-92f6-518e-8a29-4a2c9cb660c4';
UPDATE categories SET parent_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE parent_id = '0846655b-92f6-518e-8a29-4a2c9cb660c4';
DELETE FROM categories WHERE id = '0846655b-92f6-518e-8a29-4a2c9cb660c4';
-- 'Manteaux' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '436ac2c4-abd0-5464-9c83-929d9bbea8b4';
DELETE FROM user_preferences WHERE category_id = '436ac2c4-abd0-5464-9c83-929d9bbea8b4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '436ac2c4-abd0-5464-9c83-929d9bbea8b4';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '436ac2c4-abd0-5464-9c83-929d9bbea8b4';
DELETE FROM categories WHERE id = '436ac2c4-abd0-5464-9c83-929d9bbea8b4';
-- 'Shorts' -> 'Pantalons et shorts'
UPDATE listings SET category_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE category_id = 'a08cb4cb-f8f6-5fb8-96e7-084fe77cbbd8';
DELETE FROM user_preferences WHERE category_id = 'a08cb4cb-f8f6-5fb8-96e7-084fe77cbbd8' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '785d358e-f083-51a2-8957-a5e8c84bc933');
UPDATE user_preferences SET category_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE category_id = 'a08cb4cb-f8f6-5fb8-96e7-084fe77cbbd8';
UPDATE categories SET parent_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE parent_id = 'a08cb4cb-f8f6-5fb8-96e7-084fe77cbbd8';
DELETE FROM categories WHERE id = 'a08cb4cb-f8f6-5fb8-96e7-084fe77cbbd8';
-- 'Ensembles' -> 'Costumes et ensembles'
UPDATE listings SET category_id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7' WHERE category_id = 'e46d22f3-1ee0-5209-8d03-10a26826a327';
DELETE FROM user_preferences WHERE category_id = 'e46d22f3-1ee0-5209-8d03-10a26826a327' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7');
UPDATE user_preferences SET category_id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7' WHERE category_id = 'e46d22f3-1ee0-5209-8d03-10a26826a327';
UPDATE categories SET parent_id = 'c3765cfa-7d9c-5f34-af3d-6512d9f740f7' WHERE parent_id = 'e46d22f3-1ee0-5209-8d03-10a26826a327';
DELETE FROM categories WHERE id = 'e46d22f3-1ee0-5209-8d03-10a26826a327';
-- 'Maillots de bain' -> 'Maillots de bain'
UPDATE listings SET category_id = '3e478d74-a08e-5731-9707-42867984af83' WHERE category_id = '97671f5c-64ea-599a-9e6f-a0697609a012';
DELETE FROM user_preferences WHERE category_id = '97671f5c-64ea-599a-9e6f-a0697609a012' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '3e478d74-a08e-5731-9707-42867984af83');
UPDATE user_preferences SET category_id = '3e478d74-a08e-5731-9707-42867984af83' WHERE category_id = '97671f5c-64ea-599a-9e6f-a0697609a012';
UPDATE categories SET parent_id = '3e478d74-a08e-5731-9707-42867984af83' WHERE parent_id = '97671f5c-64ea-599a-9e6f-a0697609a012';
DELETE FROM categories WHERE id = '97671f5c-64ea-599a-9e6f-a0697609a012';
-- 'Baskets' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '65dbdf01-4cf4-5b3c-82a1-b49658730cc0';
DELETE FROM user_preferences WHERE category_id = '65dbdf01-4cf4-5b3c-82a1-b49658730cc0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '65dbdf01-4cf4-5b3c-82a1-b49658730cc0';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '65dbdf01-4cf4-5b3c-82a1-b49658730cc0';
DELETE FROM categories WHERE id = '65dbdf01-4cf4-5b3c-82a1-b49658730cc0';
-- 'Bottes et bottines' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '37f030d5-6b03-5054-a126-24c3c04b8470';
DELETE FROM user_preferences WHERE category_id = '37f030d5-6b03-5054-a126-24c3c04b8470' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '37f030d5-6b03-5054-a126-24c3c04b8470';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '37f030d5-6b03-5054-a126-24c3c04b8470';
DELETE FROM categories WHERE id = '37f030d5-6b03-5054-a126-24c3c04b8470';
-- 'Sandales et claquettes' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '530ecda7-e2cb-5793-9352-5761514045ba';
DELETE FROM user_preferences WHERE category_id = '530ecda7-e2cb-5793-9352-5761514045ba' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '530ecda7-e2cb-5793-9352-5761514045ba';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '530ecda7-e2cb-5793-9352-5761514045ba';
DELETE FROM categories WHERE id = '530ecda7-e2cb-5793-9352-5761514045ba';
-- 'Mocassins et chaussures de ville' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '75944ea0-cf72-5d30-98f0-3f3107233638';
DELETE FROM user_preferences WHERE category_id = '75944ea0-cf72-5d30-98f0-3f3107233638' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '75944ea0-cf72-5d30-98f0-3f3107233638';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '75944ea0-cf72-5d30-98f0-3f3107233638';
DELETE FROM categories WHERE id = '75944ea0-cf72-5d30-98f0-3f3107233638';
-- 'Sacs à dos' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '2a304ab7-0378-5122-9654-b1c6589b1076';
DELETE FROM user_preferences WHERE category_id = '2a304ab7-0378-5122-9654-b1c6589b1076' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '2a304ab7-0378-5122-9654-b1c6589b1076';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = '2a304ab7-0378-5122-9654-b1c6589b1076';
DELETE FROM categories WHERE id = '2a304ab7-0378-5122-9654-b1c6589b1076';
-- 'Bijoux' -> 'Montres et bijoux'
UPDATE listings SET category_id = '09605735-0280-5add-bd7f-d49ad83746ce' WHERE category_id = 'b8b852b5-d53a-5221-b154-9f46b9caf1f9';
DELETE FROM user_preferences WHERE category_id = 'b8b852b5-d53a-5221-b154-9f46b9caf1f9' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '09605735-0280-5add-bd7f-d49ad83746ce');
UPDATE user_preferences SET category_id = '09605735-0280-5add-bd7f-d49ad83746ce' WHERE category_id = 'b8b852b5-d53a-5221-b154-9f46b9caf1f9';
UPDATE categories SET parent_id = '09605735-0280-5add-bd7f-d49ad83746ce' WHERE parent_id = 'b8b852b5-d53a-5221-b154-9f46b9caf1f9';
DELETE FROM categories WHERE id = 'b8b852b5-d53a-5221-b154-9f46b9caf1f9';
-- 'Lunettes de soleil' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '364a78ce-aa69-54af-aec6-791625e70c7c';
DELETE FROM user_preferences WHERE category_id = '364a78ce-aa69-54af-aec6-791625e70c7c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '364a78ce-aa69-54af-aec6-791625e70c7c';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '364a78ce-aa69-54af-aec6-791625e70c7c';
DELETE FROM categories WHERE id = '364a78ce-aa69-54af-aec6-791625e70c7c';
-- 'Écharpes et foulards' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '8f4aaf02-8f39-5852-aa02-a1470e00c670';
DELETE FROM user_preferences WHERE category_id = '8f4aaf02-8f39-5852-aa02-a1470e00c670' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '8f4aaf02-8f39-5852-aa02-a1470e00c670';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '8f4aaf02-8f39-5852-aa02-a1470e00c670';
DELETE FROM categories WHERE id = '8f4aaf02-8f39-5852-aa02-a1470e00c670';
-- 'Combinaisons' -> 'Combinaisons'
UPDATE listings SET category_id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a' WHERE category_id = '4ac6a0ea-93e2-5fbe-9bba-1d48522fb9b5';
DELETE FROM user_preferences WHERE category_id = '4ac6a0ea-93e2-5fbe-9bba-1d48522fb9b5' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a');
UPDATE user_preferences SET category_id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a' WHERE category_id = '4ac6a0ea-93e2-5fbe-9bba-1d48522fb9b5';
UPDATE categories SET parent_id = 'f6e07a74-2150-53bd-bd27-f7a12eff8b4a' WHERE parent_id = '4ac6a0ea-93e2-5fbe-9bba-1d48522fb9b5';
DELETE FROM categories WHERE id = '4ac6a0ea-93e2-5fbe-9bba-1d48522fb9b5';
-- 'Chemises et blouses' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '9bc16de6-9da1-5143-b1eb-8ecee40cfd8e';
DELETE FROM user_preferences WHERE category_id = '9bc16de6-9da1-5143-b1eb-8ecee40cfd8e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '9bc16de6-9da1-5143-b1eb-8ecee40cfd8e';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = '9bc16de6-9da1-5143-b1eb-8ecee40cfd8e';
DELETE FROM categories WHERE id = '9bc16de6-9da1-5143-b1eb-8ecee40cfd8e';
-- 'Sweats et sweats à capuche' -> 'Pulls, sweats et gilets'
UPDATE listings SET category_id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3' WHERE category_id = '8999faf0-7fff-5c9a-b3a3-7fb1048d70c0';
DELETE FROM user_preferences WHERE category_id = '8999faf0-7fff-5c9a-b3a3-7fb1048d70c0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3');
UPDATE user_preferences SET category_id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3' WHERE category_id = '8999faf0-7fff-5c9a-b3a3-7fb1048d70c0';
UPDATE categories SET parent_id = 'b5ce8cf8-7f91-5fe6-94a7-59128cd742a3' WHERE parent_id = '8999faf0-7fff-5c9a-b3a3-7fb1048d70c0';
DELETE FROM categories WHERE id = '8999faf0-7fff-5c9a-b3a3-7fb1048d70c0';
-- 'Vêtements de maternité' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '65c39b2e-fd34-5be9-b07a-6672ef24b23f';
DELETE FROM user_preferences WHERE category_id = '65c39b2e-fd34-5be9-b07a-6672ef24b23f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '65c39b2e-fd34-5be9-b07a-6672ef24b23f';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = '65c39b2e-fd34-5be9-b07a-6672ef24b23f';
DELETE FROM categories WHERE id = '65c39b2e-fd34-5be9-b07a-6672ef24b23f';
-- 'Escarpins' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'd34694f5-e7da-5f2b-8dbd-51fd149d2794';
DELETE FROM user_preferences WHERE category_id = 'd34694f5-e7da-5f2b-8dbd-51fd149d2794' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'd34694f5-e7da-5f2b-8dbd-51fd149d2794';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = 'd34694f5-e7da-5f2b-8dbd-51fd149d2794';
DELETE FROM categories WHERE id = 'd34694f5-e7da-5f2b-8dbd-51fd149d2794';
-- 'Montres' -> 'Montres et bijoux'
UPDATE listings SET category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE category_id = '32704fcd-07f8-5547-820f-5fa0c980caa1';
DELETE FROM user_preferences WHERE category_id = '32704fcd-07f8-5547-820f-5fa0c980caa1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648');
UPDATE user_preferences SET category_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE category_id = '32704fcd-07f8-5547-820f-5fa0c980caa1';
UPDATE categories SET parent_id = 'd564afe6-338a-5fcb-acdb-5786b3db0648' WHERE parent_id = '32704fcd-07f8-5547-820f-5fa0c980caa1';
DELETE FROM categories WHERE id = '32704fcd-07f8-5547-820f-5fa0c980caa1';
-- 'Ensembles traditionnels' -> 'Autres vêtements traditionnels'
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'd1515d79-225a-526a-bf3a-63054e7e2dec';
DELETE FROM user_preferences WHERE category_id = 'd1515d79-225a-526a-bf3a-63054e7e2dec' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'd1515d79-225a-526a-bf3a-63054e7e2dec';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'd1515d79-225a-526a-bf3a-63054e7e2dec';
DELETE FROM categories WHERE id = 'd1515d79-225a-526a-bf3a-63054e7e2dec';
-- 'Robes et jupes' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '7cd288a4-091e-58aa-870d-245158f13a43';
DELETE FROM user_preferences WHERE category_id = '7cd288a4-091e-58aa-870d-245158f13a43' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '7cd288a4-091e-58aa-870d-245158f13a43';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '7cd288a4-091e-58aa-870d-245158f13a43';
DELETE FROM categories WHERE id = '7cd288a4-091e-58aa-870d-245158f13a43';
-- 'T-shirts et hauts' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'e8026a6c-7d5a-5b35-91d3-46b704896f95';
DELETE FROM user_preferences WHERE category_id = 'e8026a6c-7d5a-5b35-91d3-46b704896f95' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'e8026a6c-7d5a-5b35-91d3-46b704896f95';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'e8026a6c-7d5a-5b35-91d3-46b704896f95';
DELETE FROM categories WHERE id = 'e8026a6c-7d5a-5b35-91d3-46b704896f95';
-- 'Pantalons et shorts' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '49939f03-a8c4-523f-a84d-18f381a6710e';
DELETE FROM user_preferences WHERE category_id = '49939f03-a8c4-523f-a84d-18f381a6710e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '49939f03-a8c4-523f-a84d-18f381a6710e';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '49939f03-a8c4-523f-a84d-18f381a6710e';
DELETE FROM categories WHERE id = '49939f03-a8c4-523f-a84d-18f381a6710e';
-- 'Pulls et sweats' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'ec13eb75-76f3-5b91-a58e-47311ae3e4ae';
DELETE FROM user_preferences WHERE category_id = 'ec13eb75-76f3-5b91-a58e-47311ae3e4ae' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'ec13eb75-76f3-5b91-a58e-47311ae3e4ae';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'ec13eb75-76f3-5b91-a58e-47311ae3e4ae';
DELETE FROM categories WHERE id = 'ec13eb75-76f3-5b91-a58e-47311ae3e4ae';
-- 'Manteaux et vestes' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'a214264b-1a01-55eb-b550-6430e5b4e050';
DELETE FROM user_preferences WHERE category_id = 'a214264b-1a01-55eb-b550-6430e5b4e050' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'a214264b-1a01-55eb-b550-6430e5b4e050';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'a214264b-1a01-55eb-b550-6430e5b4e050';
DELETE FROM categories WHERE id = 'a214264b-1a01-55eb-b550-6430e5b4e050';
-- 'Pyjamas' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '6fb4609e-cb8e-590c-9944-d1ce2f323161';
DELETE FROM user_preferences WHERE category_id = '6fb4609e-cb8e-590c-9944-d1ce2f323161' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '6fb4609e-cb8e-590c-9944-d1ce2f323161';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '6fb4609e-cb8e-590c-9944-d1ce2f323161';
DELETE FROM categories WHERE id = '6fb4609e-cb8e-590c-9944-d1ce2f323161';
-- 'Vêtements de sport' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '1bd90c49-4e3f-5aa6-8a14-384a4ae14208';
DELETE FROM user_preferences WHERE category_id = '1bd90c49-4e3f-5aa6-8a14-384a4ae14208' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '1bd90c49-4e3f-5aa6-8a14-384a4ae14208';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '1bd90c49-4e3f-5aa6-8a14-384a4ae14208';
DELETE FROM categories WHERE id = '1bd90c49-4e3f-5aa6-8a14-384a4ae14208';
-- 'Jackets et blousons' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '75e6051c-6a34-586c-bfb6-65e5166a9644';
DELETE FROM user_preferences WHERE category_id = '75e6051c-6a34-586c-bfb6-65e5166a9644' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '75e6051c-6a34-586c-bfb6-65e5166a9644';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '75e6051c-6a34-586c-bfb6-65e5166a9644';
DELETE FROM categories WHERE id = '75e6051c-6a34-586c-bfb6-65e5166a9644';
-- 'Blazers' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '40aa8b86-a49a-5ce9-b369-a9cb839bea91';
DELETE FROM user_preferences WHERE category_id = '40aa8b86-a49a-5ce9-b369-a9cb839bea91' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '40aa8b86-a49a-5ce9-b369-a9cb839bea91';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '40aa8b86-a49a-5ce9-b369-a9cb839bea91';
DELETE FROM categories WHERE id = '40aa8b86-a49a-5ce9-b369-a9cb839bea91';
-- 'Doudounes' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'e507ae81-62dc-5d39-aa2a-b426dfac059e';
DELETE FROM user_preferences WHERE category_id = 'e507ae81-62dc-5d39-aa2a-b426dfac059e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'e507ae81-62dc-5d39-aa2a-b426dfac059e';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = 'e507ae81-62dc-5d39-aa2a-b426dfac059e';
DELETE FROM categories WHERE id = 'e507ae81-62dc-5d39-aa2a-b426dfac059e';
-- 'Parkas' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'b0551467-2377-5f71-ad7c-24906e25ac45';
DELETE FROM user_preferences WHERE category_id = 'b0551467-2377-5f71-ad7c-24906e25ac45' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = 'b0551467-2377-5f71-ad7c-24906e25ac45';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = 'b0551467-2377-5f71-ad7c-24906e25ac45';
DELETE FROM categories WHERE id = 'b0551467-2377-5f71-ad7c-24906e25ac45';
-- 'Trenchs et imperméables' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '73708edd-d2b5-522a-a951-aaf307aa416b';
DELETE FROM user_preferences WHERE category_id = '73708edd-d2b5-522a-a951-aaf307aa416b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '73708edd-d2b5-522a-a951-aaf307aa416b';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '73708edd-d2b5-522a-a951-aaf307aa416b';
DELETE FROM categories WHERE id = '73708edd-d2b5-522a-a951-aaf307aa416b';
-- 'Vestes en jean' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '898780a9-7430-5c37-ae3d-ffc3f6d66bed';
DELETE FROM user_preferences WHERE category_id = '898780a9-7430-5c37-ae3d-ffc3f6d66bed' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '898780a9-7430-5c37-ae3d-ffc3f6d66bed';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '898780a9-7430-5c37-ae3d-ffc3f6d66bed';
DELETE FROM categories WHERE id = '898780a9-7430-5c37-ae3d-ffc3f6d66bed';
-- 'Vestes en cuir' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '0424db04-a890-526f-b6fb-534214e8efa0';
DELETE FROM user_preferences WHERE category_id = '0424db04-a890-526f-b6fb-534214e8efa0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '0424db04-a890-526f-b6fb-534214e8efa0';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '0424db04-a890-526f-b6fb-534214e8efa0';
DELETE FROM categories WHERE id = '0424db04-a890-526f-b6fb-534214e8efa0';
-- 'Polaires' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '55b4e73f-d21d-5015-9d97-9b4f918496a0';
DELETE FROM user_preferences WHERE category_id = '55b4e73f-d21d-5015-9d97-9b4f918496a0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02');
UPDATE user_preferences SET category_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE category_id = '55b4e73f-d21d-5015-9d97-9b4f918496a0';
UPDATE categories SET parent_id = 'd103cc35-670f-5520-b0b8-846cb4950a02' WHERE parent_id = '55b4e73f-d21d-5015-9d97-9b4f918496a0';
DELETE FROM categories WHERE id = '55b4e73f-d21d-5015-9d97-9b4f918496a0';
-- 'Débardeurs' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'cab5423c-7516-5769-95ab-773d055bd4b5';
DELETE FROM user_preferences WHERE category_id = 'cab5423c-7516-5769-95ab-773d055bd4b5' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'cab5423c-7516-5769-95ab-773d055bd4b5';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = 'cab5423c-7516-5769-95ab-773d055bd4b5';
DELETE FROM categories WHERE id = 'cab5423c-7516-5769-95ab-773d055bd4b5';
-- 'Survêtements' -> 'Sport et maillots de bain'
UPDATE listings SET category_id = '5af2861a-6177-553b-9c30-299e856bd81b' WHERE category_id = '9991471c-0b8b-5c27-b36b-f631dc0f2c17';
DELETE FROM user_preferences WHERE category_id = '9991471c-0b8b-5c27-b36b-f631dc0f2c17' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5af2861a-6177-553b-9c30-299e856bd81b');
UPDATE user_preferences SET category_id = '5af2861a-6177-553b-9c30-299e856bd81b' WHERE category_id = '9991471c-0b8b-5c27-b36b-f631dc0f2c17';
UPDATE categories SET parent_id = '5af2861a-6177-553b-9c30-299e856bd81b' WHERE parent_id = '9991471c-0b8b-5c27-b36b-f631dc0f2c17';
DELETE FROM categories WHERE id = '9991471c-0b8b-5c27-b36b-f631dc0f2c17';
-- 'Peignoirs' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = '8906e9e6-33c5-58b5-a0d9-dc10694fda6b';
DELETE FROM user_preferences WHERE category_id = '8906e9e6-33c5-58b5-a0d9-dc10694fda6b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb');
UPDATE user_preferences SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = '8906e9e6-33c5-58b5-a0d9-dc10694fda6b';
UPDATE categories SET parent_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE parent_id = '8906e9e6-33c5-58b5-a0d9-dc10694fda6b';
DELETE FROM categories WHERE id = '8906e9e6-33c5-58b5-a0d9-dc10694fda6b';
-- 'Chaussettes' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'ae411ebf-f0f2-5f44-bdcf-f5c46a9fce49';
DELETE FROM user_preferences WHERE category_id = 'ae411ebf-f0f2-5f44-bdcf-f5c46a9fce49' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb');
UPDATE user_preferences SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'ae411ebf-f0f2-5f44-bdcf-f5c46a9fce49';
UPDATE categories SET parent_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE parent_id = 'ae411ebf-f0f2-5f44-bdcf-f5c46a9fce49';
DELETE FROM categories WHERE id = 'ae411ebf-f0f2-5f44-bdcf-f5c46a9fce49';
-- 'Sous-vêtements' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'c6bd8006-4c3c-52d1-9d67-951a4c5ade1a';
DELETE FROM user_preferences WHERE category_id = 'c6bd8006-4c3c-52d1-9d67-951a4c5ade1a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb');
UPDATE user_preferences SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'c6bd8006-4c3c-52d1-9d67-951a4c5ade1a';
UPDATE categories SET parent_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE parent_id = 'c6bd8006-4c3c-52d1-9d67-951a4c5ade1a';
DELETE FROM categories WHERE id = 'c6bd8006-4c3c-52d1-9d67-951a4c5ade1a';
-- 'Autres vêtements' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '3ece4181-faf1-55a0-aede-d809a08ebb75';
DELETE FROM user_preferences WHERE category_id = '3ece4181-faf1-55a0-aede-d809a08ebb75' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '3ece4181-faf1-55a0-aede-d809a08ebb75';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = '3ece4181-faf1-55a0-aede-d809a08ebb75';
DELETE FROM categories WHERE id = '3ece4181-faf1-55a0-aede-d809a08ebb75';
-- 'Chaussons' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '69de4c6e-3aeb-5321-adf5-570cdca0822f';
DELETE FROM user_preferences WHERE category_id = '69de4c6e-3aeb-5321-adf5-570cdca0822f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '69de4c6e-3aeb-5321-adf5-570cdca0822f';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '69de4c6e-3aeb-5321-adf5-570cdca0822f';
DELETE FROM categories WHERE id = '69de4c6e-3aeb-5321-adf5-570cdca0822f';
-- 'Chaussures de sport' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'a1ade773-d843-5c22-ae5d-c35a2b5164db';
DELETE FROM user_preferences WHERE category_id = 'a1ade773-d843-5c22-ae5d-c35a2b5164db' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'a1ade773-d843-5c22-ae5d-c35a2b5164db';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = 'a1ade773-d843-5c22-ae5d-c35a2b5164db';
DELETE FROM categories WHERE id = 'a1ade773-d843-5c22-ae5d-c35a2b5164db';
-- 'Chaussures de randonnée' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '7778c9ca-2de1-5324-8b75-b25d5eabfda5';
DELETE FROM user_preferences WHERE category_id = '7778c9ca-2de1-5324-8b75-b25d5eabfda5' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '7778c9ca-2de1-5324-8b75-b25d5eabfda5';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '7778c9ca-2de1-5324-8b75-b25d5eabfda5';
DELETE FROM categories WHERE id = '7778c9ca-2de1-5324-8b75-b25d5eabfda5';
-- 'Espadrilles' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'c9246324-c3ce-583b-8c2a-570b6a4cd3a4';
DELETE FROM user_preferences WHERE category_id = 'c9246324-c3ce-583b-8c2a-570b6a4cd3a4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'c9246324-c3ce-583b-8c2a-570b6a4cd3a4';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = 'c9246324-c3ce-583b-8c2a-570b6a4cd3a4';
DELETE FROM categories WHERE id = 'c9246324-c3ce-583b-8c2a-570b6a4cd3a4';
-- 'Autres chaussures' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'f405f822-6c86-5fa6-bfea-0f8b5466866c';
DELETE FROM user_preferences WHERE category_id = 'f405f822-6c86-5fa6-bfea-0f8b5466866c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'f405f822-6c86-5fa6-bfea-0f8b5466866c';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = 'f405f822-6c86-5fa6-bfea-0f8b5466866c';
DELETE FROM categories WHERE id = 'f405f822-6c86-5fa6-bfea-0f8b5466866c';
-- 'Sacs de voyage' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'd81257c4-d32d-5f1d-8aac-05156fd0e812';
DELETE FROM user_preferences WHERE category_id = 'd81257c4-d32d-5f1d-8aac-05156fd0e812' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'd81257c4-d32d-5f1d-8aac-05156fd0e812';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = 'd81257c4-d32d-5f1d-8aac-05156fd0e812';
DELETE FROM categories WHERE id = 'd81257c4-d32d-5f1d-8aac-05156fd0e812';
-- 'Sacs banane' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'a0674efd-a5cc-5e10-82cf-2784fdbffdc2';
DELETE FROM user_preferences WHERE category_id = 'a0674efd-a5cc-5e10-82cf-2784fdbffdc2' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'a0674efd-a5cc-5e10-82cf-2784fdbffdc2';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = 'a0674efd-a5cc-5e10-82cf-2784fdbffdc2';
DELETE FROM categories WHERE id = 'a0674efd-a5cc-5e10-82cf-2784fdbffdc2';
-- 'Portefeuilles et porte-monnaie' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '23559eb6-004f-5757-b101-d37286dfff5c';
DELETE FROM user_preferences WHERE category_id = '23559eb6-004f-5757-b101-d37286dfff5c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '23559eb6-004f-5757-b101-d37286dfff5c';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = '23559eb6-004f-5757-b101-d37286dfff5c';
DELETE FROM categories WHERE id = '23559eb6-004f-5757-b101-d37286dfff5c';
-- 'Pochettes' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'c0819a6d-0438-5a9b-96ff-d4899cec2ac0';
DELETE FROM user_preferences WHERE category_id = 'c0819a6d-0438-5a9b-96ff-d4899cec2ac0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'c0819a6d-0438-5a9b-96ff-d4899cec2ac0';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = 'c0819a6d-0438-5a9b-96ff-d4899cec2ac0';
DELETE FROM categories WHERE id = 'c0819a6d-0438-5a9b-96ff-d4899cec2ac0';
-- 'Cravates et nœuds papillon' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'ed686897-89c4-5c27-9328-e34d80d0da55';
DELETE FROM user_preferences WHERE category_id = 'ed686897-89c4-5c27-9328-e34d80d0da55' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'ed686897-89c4-5c27-9328-e34d80d0da55';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = 'ed686897-89c4-5c27-9328-e34d80d0da55';
DELETE FROM categories WHERE id = 'ed686897-89c4-5c27-9328-e34d80d0da55';
-- 'Accessoires pour cheveux' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '313fbb95-3817-5088-a1a7-1b5082a025f0';
DELETE FROM user_preferences WHERE category_id = '313fbb95-3817-5088-a1a7-1b5082a025f0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '313fbb95-3817-5088-a1a7-1b5082a025f0';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '313fbb95-3817-5088-a1a7-1b5082a025f0';
DELETE FROM categories WHERE id = '313fbb95-3817-5088-a1a7-1b5082a025f0';
-- 'Porte-clés' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '37a1b50a-9ecf-5b44-84c1-574fb874279e';
DELETE FROM user_preferences WHERE category_id = '37a1b50a-9ecf-5b44-84c1-574fb874279e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '37a1b50a-9ecf-5b44-84c1-574fb874279e';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '37a1b50a-9ecf-5b44-84c1-574fb874279e';
DELETE FROM categories WHERE id = '37a1b50a-9ecf-5b44-84c1-574fb874279e';
-- 'Autres accessoires' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '40ba81dc-147e-552b-afd8-c6442d820ab4';
DELETE FROM user_preferences WHERE category_id = '40ba81dc-147e-552b-afd8-c6442d820ab4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '40ba81dc-147e-552b-afd8-c6442d820ab4';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '40ba81dc-147e-552b-afd8-c6442d820ab4';
DELETE FROM categories WHERE id = '40ba81dc-147e-552b-afd8-c6442d820ab4';
-- 'Casquettes' -> 'Chapeaux et bonnets'
UPDATE listings SET category_id = 'f559655f-55b8-5198-af15-404c7c585c9c' WHERE category_id = 'aad95742-f14a-574a-bc97-a91e223d8ef7';
DELETE FROM user_preferences WHERE category_id = 'aad95742-f14a-574a-bc97-a91e223d8ef7' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'f559655f-55b8-5198-af15-404c7c585c9c');
UPDATE user_preferences SET category_id = 'f559655f-55b8-5198-af15-404c7c585c9c' WHERE category_id = 'aad95742-f14a-574a-bc97-a91e223d8ef7';
UPDATE categories SET parent_id = 'f559655f-55b8-5198-af15-404c7c585c9c' WHERE parent_id = 'aad95742-f14a-574a-bc97-a91e223d8ef7';
DELETE FROM categories WHERE id = 'aad95742-f14a-574a-bc97-a91e223d8ef7';
-- 'Jackets et blousons' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'de9fb4f7-b75c-59ff-9555-92074155a322';
DELETE FROM user_preferences WHERE category_id = 'de9fb4f7-b75c-59ff-9555-92074155a322' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'de9fb4f7-b75c-59ff-9555-92074155a322';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = 'de9fb4f7-b75c-59ff-9555-92074155a322';
DELETE FROM categories WHERE id = 'de9fb4f7-b75c-59ff-9555-92074155a322';
-- 'Blazers' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'ca4c7951-2bc9-5594-960f-f32c2c2e2773';
DELETE FROM user_preferences WHERE category_id = 'ca4c7951-2bc9-5594-960f-f32c2c2e2773' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'ca4c7951-2bc9-5594-960f-f32c2c2e2773';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = 'ca4c7951-2bc9-5594-960f-f32c2c2e2773';
DELETE FROM categories WHERE id = 'ca4c7951-2bc9-5594-960f-f32c2c2e2773';
-- 'Doudounes' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'bd03302f-f41d-56f7-a416-782699f55a57';
DELETE FROM user_preferences WHERE category_id = 'bd03302f-f41d-56f7-a416-782699f55a57' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = 'bd03302f-f41d-56f7-a416-782699f55a57';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = 'bd03302f-f41d-56f7-a416-782699f55a57';
DELETE FROM categories WHERE id = 'bd03302f-f41d-56f7-a416-782699f55a57';
-- 'Parkas' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '4c720955-2ed7-58ef-b3ca-f946dad5009c';
DELETE FROM user_preferences WHERE category_id = '4c720955-2ed7-58ef-b3ca-f946dad5009c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '4c720955-2ed7-58ef-b3ca-f946dad5009c';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '4c720955-2ed7-58ef-b3ca-f946dad5009c';
DELETE FROM categories WHERE id = '4c720955-2ed7-58ef-b3ca-f946dad5009c';
-- 'Trenchs et imperméables' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '240d637f-6479-52c9-b521-f17c95247096';
DELETE FROM user_preferences WHERE category_id = '240d637f-6479-52c9-b521-f17c95247096' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '240d637f-6479-52c9-b521-f17c95247096';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '240d637f-6479-52c9-b521-f17c95247096';
DELETE FROM categories WHERE id = '240d637f-6479-52c9-b521-f17c95247096';
-- 'Vestes en jean' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '1aa60a45-5d97-5706-9279-dab0f8298baa';
DELETE FROM user_preferences WHERE category_id = '1aa60a45-5d97-5706-9279-dab0f8298baa' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '1aa60a45-5d97-5706-9279-dab0f8298baa';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '1aa60a45-5d97-5706-9279-dab0f8298baa';
DELETE FROM categories WHERE id = '1aa60a45-5d97-5706-9279-dab0f8298baa';
-- 'Vestes en cuir' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '83f166b5-cf33-5b3a-b713-4d056eb61552';
DELETE FROM user_preferences WHERE category_id = '83f166b5-cf33-5b3a-b713-4d056eb61552' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '83f166b5-cf33-5b3a-b713-4d056eb61552';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '83f166b5-cf33-5b3a-b713-4d056eb61552';
DELETE FROM categories WHERE id = '83f166b5-cf33-5b3a-b713-4d056eb61552';
-- 'Polaires' -> 'Vestes, jackets et manteaux'
UPDATE listings SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '8ac50270-2f3a-5206-8495-f9fa0c5b814f';
DELETE FROM user_preferences WHERE category_id = '8ac50270-2f3a-5206-8495-f9fa0c5b814f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1');
UPDATE user_preferences SET category_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE category_id = '8ac50270-2f3a-5206-8495-f9fa0c5b814f';
UPDATE categories SET parent_id = 'abfa8737-ec8f-5851-91d9-442c8d28f3f1' WHERE parent_id = '8ac50270-2f3a-5206-8495-f9fa0c5b814f';
DELETE FROM categories WHERE id = '8ac50270-2f3a-5206-8495-f9fa0c5b814f';
-- 'Débardeurs' -> 'T-shirts'
UPDATE listings SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'a4ca1c5e-e78a-5bd5-91a1-3d9ae1020201';
DELETE FROM user_preferences WHERE category_id = 'a4ca1c5e-e78a-5bd5-91a1-3d9ae1020201' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd');
UPDATE user_preferences SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'a4ca1c5e-e78a-5bd5-91a1-3d9ae1020201';
UPDATE categories SET parent_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE parent_id = 'a4ca1c5e-e78a-5bd5-91a1-3d9ae1020201';
DELETE FROM categories WHERE id = 'a4ca1c5e-e78a-5bd5-91a1-3d9ae1020201';
-- 'Survêtements' -> 'Sport et maillots de bain'
UPDATE listings SET category_id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2' WHERE category_id = 'e5385186-93b6-5ec2-b14e-5d45f4357330';
DELETE FROM user_preferences WHERE category_id = 'e5385186-93b6-5ec2-b14e-5d45f4357330' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2');
UPDATE user_preferences SET category_id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2' WHERE category_id = 'e5385186-93b6-5ec2-b14e-5d45f4357330';
UPDATE categories SET parent_id = 'a4ddd3ab-d878-5993-a191-c09bf1839df2' WHERE parent_id = 'e5385186-93b6-5ec2-b14e-5d45f4357330';
DELETE FROM categories WHERE id = 'e5385186-93b6-5ec2-b14e-5d45f4357330';
-- 'Peignoirs' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '9ae4af56-ea6f-5155-9994-630957ddcb05';
DELETE FROM user_preferences WHERE category_id = '9ae4af56-ea6f-5155-9994-630957ddcb05' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd85f47ee-c40b-51ec-b098-e982278439af');
UPDATE user_preferences SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '9ae4af56-ea6f-5155-9994-630957ddcb05';
UPDATE categories SET parent_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE parent_id = '9ae4af56-ea6f-5155-9994-630957ddcb05';
DELETE FROM categories WHERE id = '9ae4af56-ea6f-5155-9994-630957ddcb05';
-- 'Chaussettes' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '284de27e-dc22-54d1-a943-a15471c6ce27';
DELETE FROM user_preferences WHERE category_id = '284de27e-dc22-54d1-a943-a15471c6ce27' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd85f47ee-c40b-51ec-b098-e982278439af');
UPDATE user_preferences SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '284de27e-dc22-54d1-a943-a15471c6ce27';
UPDATE categories SET parent_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE parent_id = '284de27e-dc22-54d1-a943-a15471c6ce27';
DELETE FROM categories WHERE id = '284de27e-dc22-54d1-a943-a15471c6ce27';
-- 'Sous-vêtements' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '0405d4cb-1bb0-57b6-98fa-c270aa525f40';
DELETE FROM user_preferences WHERE category_id = '0405d4cb-1bb0-57b6-98fa-c270aa525f40' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd85f47ee-c40b-51ec-b098-e982278439af');
UPDATE user_preferences SET category_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE category_id = '0405d4cb-1bb0-57b6-98fa-c270aa525f40';
UPDATE categories SET parent_id = 'd85f47ee-c40b-51ec-b098-e982278439af' WHERE parent_id = '0405d4cb-1bb0-57b6-98fa-c270aa525f40';
DELETE FROM categories WHERE id = '0405d4cb-1bb0-57b6-98fa-c270aa525f40';
-- 'Autres vêtements' -> 'T-shirts'
UPDATE listings SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'f6b9281d-2b9f-5a53-804c-e96b889fef90';
DELETE FROM user_preferences WHERE category_id = 'f6b9281d-2b9f-5a53-804c-e96b889fef90' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd');
UPDATE user_preferences SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'f6b9281d-2b9f-5a53-804c-e96b889fef90';
UPDATE categories SET parent_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE parent_id = 'f6b9281d-2b9f-5a53-804c-e96b889fef90';
DELETE FROM categories WHERE id = 'f6b9281d-2b9f-5a53-804c-e96b889fef90';
-- 'Chaussons' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '1c8f0973-f200-5bbd-aadf-e76b55da253a';
DELETE FROM user_preferences WHERE category_id = '1c8f0973-f200-5bbd-aadf-e76b55da253a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '1c8f0973-f200-5bbd-aadf-e76b55da253a';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '1c8f0973-f200-5bbd-aadf-e76b55da253a';
DELETE FROM categories WHERE id = '1c8f0973-f200-5bbd-aadf-e76b55da253a';
-- 'Chaussures de sport' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'b3331362-6fea-53cf-908c-e9388d6571de';
DELETE FROM user_preferences WHERE category_id = 'b3331362-6fea-53cf-908c-e9388d6571de' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'b3331362-6fea-53cf-908c-e9388d6571de';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = 'b3331362-6fea-53cf-908c-e9388d6571de';
DELETE FROM categories WHERE id = 'b3331362-6fea-53cf-908c-e9388d6571de';
-- 'Chaussures de randonnée' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'd0334dd7-2490-5ff4-abcc-6029950dbd74';
DELETE FROM user_preferences WHERE category_id = 'd0334dd7-2490-5ff4-abcc-6029950dbd74' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'd0334dd7-2490-5ff4-abcc-6029950dbd74';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = 'd0334dd7-2490-5ff4-abcc-6029950dbd74';
DELETE FROM categories WHERE id = 'd0334dd7-2490-5ff4-abcc-6029950dbd74';
-- 'Espadrilles' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '6d46618d-37d1-5286-a49f-b1353ead7b35';
DELETE FROM user_preferences WHERE category_id = '6d46618d-37d1-5286-a49f-b1353ead7b35' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '6d46618d-37d1-5286-a49f-b1353ead7b35';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '6d46618d-37d1-5286-a49f-b1353ead7b35';
DELETE FROM categories WHERE id = '6d46618d-37d1-5286-a49f-b1353ead7b35';
-- 'Autres chaussures' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '523e7804-9d32-50fa-ab56-44f12ac40b24';
DELETE FROM user_preferences WHERE category_id = '523e7804-9d32-50fa-ab56-44f12ac40b24' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = '523e7804-9d32-50fa-ab56-44f12ac40b24';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = '523e7804-9d32-50fa-ab56-44f12ac40b24';
DELETE FROM categories WHERE id = '523e7804-9d32-50fa-ab56-44f12ac40b24';
-- 'Sacs de voyage' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '7c138657-5e5a-5a3b-8466-21b642435ee9';
DELETE FROM user_preferences WHERE category_id = '7c138657-5e5a-5a3b-8466-21b642435ee9' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '7c138657-5e5a-5a3b-8466-21b642435ee9';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = '7c138657-5e5a-5a3b-8466-21b642435ee9';
DELETE FROM categories WHERE id = '7c138657-5e5a-5a3b-8466-21b642435ee9';
-- 'Sacs banane' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = 'ce162942-3260-5976-b6ac-d1b0e04a7467';
DELETE FROM user_preferences WHERE category_id = 'ce162942-3260-5976-b6ac-d1b0e04a7467' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = 'ce162942-3260-5976-b6ac-d1b0e04a7467';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = 'ce162942-3260-5976-b6ac-d1b0e04a7467';
DELETE FROM categories WHERE id = 'ce162942-3260-5976-b6ac-d1b0e04a7467';
-- 'Portefeuilles et porte-monnaie' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '295c1eb8-066a-5f6d-95b7-236a677904cd';
DELETE FROM user_preferences WHERE category_id = '295c1eb8-066a-5f6d-95b7-236a677904cd' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '295c1eb8-066a-5f6d-95b7-236a677904cd';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = '295c1eb8-066a-5f6d-95b7-236a677904cd';
DELETE FROM categories WHERE id = '295c1eb8-066a-5f6d-95b7-236a677904cd';
-- 'Pochettes' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '22c38f65-34dc-5f8f-a5eb-858d8ec99533';
DELETE FROM user_preferences WHERE category_id = '22c38f65-34dc-5f8f-a5eb-858d8ec99533' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '22c38f65-34dc-5f8f-a5eb-858d8ec99533';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = '22c38f65-34dc-5f8f-a5eb-858d8ec99533';
DELETE FROM categories WHERE id = '22c38f65-34dc-5f8f-a5eb-858d8ec99533';
-- 'Cravates et nœuds papillon' -> 'Cravates et nœuds papillon'
UPDATE listings SET category_id = '4875829f-51e7-59fe-885d-3f4294256486' WHERE category_id = '96b6b48b-d99e-5b7e-bf78-4d618fd636f9';
DELETE FROM user_preferences WHERE category_id = '96b6b48b-d99e-5b7e-bf78-4d618fd636f9' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '4875829f-51e7-59fe-885d-3f4294256486');
UPDATE user_preferences SET category_id = '4875829f-51e7-59fe-885d-3f4294256486' WHERE category_id = '96b6b48b-d99e-5b7e-bf78-4d618fd636f9';
UPDATE categories SET parent_id = '4875829f-51e7-59fe-885d-3f4294256486' WHERE parent_id = '96b6b48b-d99e-5b7e-bf78-4d618fd636f9';
DELETE FROM categories WHERE id = '96b6b48b-d99e-5b7e-bf78-4d618fd636f9';
-- 'Accessoires pour cheveux' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '6d5c4ecb-0038-5736-bf1b-ce03b66d6017';
DELETE FROM user_preferences WHERE category_id = '6d5c4ecb-0038-5736-bf1b-ce03b66d6017' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '6d5c4ecb-0038-5736-bf1b-ce03b66d6017';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '6d5c4ecb-0038-5736-bf1b-ce03b66d6017';
DELETE FROM categories WHERE id = '6d5c4ecb-0038-5736-bf1b-ce03b66d6017';
-- 'Porte-clés' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '77b24a71-cb9e-57c0-89c0-83767bae03b6';
DELETE FROM user_preferences WHERE category_id = '77b24a71-cb9e-57c0-89c0-83767bae03b6' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '77b24a71-cb9e-57c0-89c0-83767bae03b6';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '77b24a71-cb9e-57c0-89c0-83767bae03b6';
DELETE FROM categories WHERE id = '77b24a71-cb9e-57c0-89c0-83767bae03b6';
-- 'Autres accessoires' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '62b5f216-9842-502d-9c39-65cc1ec7a511';
DELETE FROM user_preferences WHERE category_id = '62b5f216-9842-502d-9c39-65cc1ec7a511' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '62b5f216-9842-502d-9c39-65cc1ec7a511';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '62b5f216-9842-502d-9c39-65cc1ec7a511';
DELETE FROM categories WHERE id = '62b5f216-9842-502d-9c39-65cc1ec7a511';
-- 'Casquettes' -> 'Chapeaux et bonnets'
UPDATE listings SET category_id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178' WHERE category_id = '6271f435-2d8c-55dd-b397-7898f0326a5f';
DELETE FROM user_preferences WHERE category_id = '6271f435-2d8c-55dd-b397-7898f0326a5f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178');
UPDATE user_preferences SET category_id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178' WHERE category_id = '6271f435-2d8c-55dd-b397-7898f0326a5f';
UPDATE categories SET parent_id = '6d31aaac-96b8-5660-93b6-ee7ffc6ce178' WHERE parent_id = '6271f435-2d8c-55dd-b397-7898f0326a5f';
DELETE FROM categories WHERE id = '6271f435-2d8c-55dd-b397-7898f0326a5f';
-- 'T-shirts' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'c8c9e069-c417-533c-b095-e88d6714a9e9';
DELETE FROM user_preferences WHERE category_id = 'c8c9e069-c417-533c-b095-e88d6714a9e9' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'c8c9e069-c417-533c-b095-e88d6714a9e9';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = 'c8c9e069-c417-533c-b095-e88d6714a9e9';
DELETE FROM categories WHERE id = 'c8c9e069-c417-533c-b095-e88d6714a9e9';
-- 'Bodys' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'f6313010-1e34-5751-8f69-22a40e4ec892';
DELETE FROM user_preferences WHERE category_id = 'f6313010-1e34-5751-8f69-22a40e4ec892' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = 'f6313010-1e34-5751-8f69-22a40e4ec892';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = 'f6313010-1e34-5751-8f69-22a40e4ec892';
DELETE FROM categories WHERE id = 'f6313010-1e34-5751-8f69-22a40e4ec892';
-- 'Leggings' -> 'Pantalons et shorts'
UPDATE listings SET category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE category_id = '0c5266ad-8067-5325-92b5-b0a8a1ce034d';
DELETE FROM user_preferences WHERE category_id = '0c5266ad-8067-5325-92b5-b0a8a1ce034d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80');
UPDATE user_preferences SET category_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE category_id = '0c5266ad-8067-5325-92b5-b0a8a1ce034d';
UPDATE categories SET parent_id = '77bc4938-c572-56f8-9797-ec9bb3240b80' WHERE parent_id = '0c5266ad-8067-5325-92b5-b0a8a1ce034d';
DELETE FROM categories WHERE id = '0c5266ad-8067-5325-92b5-b0a8a1ce034d';
-- 'Tailleurs' -> 'Ensembles'
UPDATE listings SET category_id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89' WHERE category_id = 'f54df04a-3101-5e8f-8f5c-d437226fb736';
DELETE FROM user_preferences WHERE category_id = 'f54df04a-3101-5e8f-8f5c-d437226fb736' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89');
UPDATE user_preferences SET category_id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89' WHERE category_id = 'f54df04a-3101-5e8f-8f5c-d437226fb736';
UPDATE categories SET parent_id = '8a8c0ae5-81e4-57e2-8e6f-4e59df4e5c89' WHERE parent_id = 'f54df04a-3101-5e8f-8f5c-d437226fb736';
DELETE FROM categories WHERE id = 'f54df04a-3101-5e8f-8f5c-d437226fb736';
-- 'Tuniques' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '752703bb-3821-5675-b2a0-d810fbfe48eb';
DELETE FROM user_preferences WHERE category_id = '752703bb-3821-5675-b2a0-d810fbfe48eb' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '752703bb-3821-5675-b2a0-d810fbfe48eb';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = '752703bb-3821-5675-b2a0-d810fbfe48eb';
DELETE FROM categories WHERE id = '752703bb-3821-5675-b2a0-d810fbfe48eb';
-- 'Kimonos' -> 'Tops'
UPDATE listings SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '717c38f5-0ad8-5a79-8301-d8c8eda2e073';
DELETE FROM user_preferences WHERE category_id = '717c38f5-0ad8-5a79-8301-d8c8eda2e073' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996');
UPDATE user_preferences SET category_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE category_id = '717c38f5-0ad8-5a79-8301-d8c8eda2e073';
UPDATE categories SET parent_id = 'd60d476e-acde-5af8-87d3-2c6d48eb4996' WHERE parent_id = '717c38f5-0ad8-5a79-8301-d8c8eda2e073';
DELETE FROM categories WHERE id = '717c38f5-0ad8-5a79-8301-d8c8eda2e073';
-- 'Lingerie' -> 'Lingerie'
UPDATE listings SET category_id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5' WHERE category_id = 'd03c30fd-267b-5c92-86a0-f19d41ef7198';
DELETE FROM user_preferences WHERE category_id = 'd03c30fd-267b-5c92-86a0-f19d41ef7198' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5');
UPDATE user_preferences SET category_id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5' WHERE category_id = 'd03c30fd-267b-5c92-86a0-f19d41ef7198';
UPDATE categories SET parent_id = '4dfeb597-f5b5-59f1-9af2-fe4b289c9fd5' WHERE parent_id = 'd03c30fd-267b-5c92-86a0-f19d41ef7198';
DELETE FROM categories WHERE id = 'd03c30fd-267b-5c92-86a0-f19d41ef7198';
-- 'Collants et bas' -> 'Pyjamas et sous-vêtements'
UPDATE listings SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'a068fed5-6338-5186-a297-3a131b3a8350';
DELETE FROM user_preferences WHERE category_id = 'a068fed5-6338-5186-a297-3a131b3a8350' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb');
UPDATE user_preferences SET category_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE category_id = 'a068fed5-6338-5186-a297-3a131b3a8350';
UPDATE categories SET parent_id = '50bb5384-fa8b-5b56-9345-c610decdfeeb' WHERE parent_id = 'a068fed5-6338-5186-a297-3a131b3a8350';
DELETE FROM categories WHERE id = 'a068fed5-6338-5186-a297-3a131b3a8350';
-- 'Robes de mariée' -> 'Robes'
UPDATE listings SET category_id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174' WHERE category_id = 'cab58064-4f90-532b-90ca-d6dc9d0b616d';
DELETE FROM user_preferences WHERE category_id = 'cab58064-4f90-532b-90ca-d6dc9d0b616d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174');
UPDATE user_preferences SET category_id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174' WHERE category_id = 'cab58064-4f90-532b-90ca-d6dc9d0b616d';
UPDATE categories SET parent_id = 'd0b99ed7-fac8-521e-b512-2832dbe9f174' WHERE parent_id = 'cab58064-4f90-532b-90ca-d6dc9d0b616d';
DELETE FROM categories WHERE id = 'cab58064-4f90-532b-90ca-d6dc9d0b616d';
-- 'Ballerines' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '78a75df2-b259-5f47-a4ad-64b5349b0a84';
DELETE FROM user_preferences WHERE category_id = '78a75df2-b259-5f47-a4ad-64b5349b0a84' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '78a75df2-b259-5f47-a4ad-64b5349b0a84';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '78a75df2-b259-5f47-a4ad-64b5349b0a84';
DELETE FROM categories WHERE id = '78a75df2-b259-5f47-a4ad-64b5349b0a84';
-- 'Chaussures compensées' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '38d7a374-78d5-5af1-80d2-151504af6140';
DELETE FROM user_preferences WHERE category_id = '38d7a374-78d5-5af1-80d2-151504af6140' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '38d7a374-78d5-5af1-80d2-151504af6140';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '38d7a374-78d5-5af1-80d2-151504af6140';
DELETE FROM categories WHERE id = '38d7a374-78d5-5af1-80d2-151504af6140';
-- 'Mules' -> 'Chaussures'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'f3c8ec22-bb83-5334-9e40-d84c7ab503b4';
DELETE FROM user_preferences WHERE category_id = 'f3c8ec22-bb83-5334-9e40-d84c7ab503b4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = 'f3c8ec22-bb83-5334-9e40-d84c7ab503b4';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = 'f3c8ec22-bb83-5334-9e40-d84c7ab503b4';
DELETE FROM categories WHERE id = 'f3c8ec22-bb83-5334-9e40-d84c7ab503b4';
-- 'Sacs à main' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '8f3cc8f1-51f8-5016-806e-14ee0a3f6698';
DELETE FROM user_preferences WHERE category_id = '8f3cc8f1-51f8-5016-806e-14ee0a3f6698' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '8f3cc8f1-51f8-5016-806e-14ee0a3f6698';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = '8f3cc8f1-51f8-5016-806e-14ee0a3f6698';
DELETE FROM categories WHERE id = '8f3cc8f1-51f8-5016-806e-14ee0a3f6698';
-- 'Sacs à bandoulière' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'c5f98c9e-bcf5-51b8-a43d-96a44595cc7d';
DELETE FROM user_preferences WHERE category_id = 'c5f98c9e-bcf5-51b8-a43d-96a44595cc7d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = 'c5f98c9e-bcf5-51b8-a43d-96a44595cc7d';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = 'c5f98c9e-bcf5-51b8-a43d-96a44595cc7d';
DELETE FROM categories WHERE id = 'c5f98c9e-bcf5-51b8-a43d-96a44595cc7d';
-- 'Cabas' -> 'Sacs'
UPDATE listings SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '5083bfd2-7f97-5e9e-a347-8df9699b88a6';
DELETE FROM user_preferences WHERE category_id = '5083bfd2-7f97-5e9e-a347-8df9699b88a6' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1');
UPDATE user_preferences SET category_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE category_id = '5083bfd2-7f97-5e9e-a347-8df9699b88a6';
UPDATE categories SET parent_id = 'ad948d76-aa80-5be7-8f27-45c4c8ab52d1' WHERE parent_id = '5083bfd2-7f97-5e9e-a347-8df9699b88a6';
DELETE FROM categories WHERE id = '5083bfd2-7f97-5e9e-a347-8df9699b88a6';
-- 'Bijoux de cheveux' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3e731663-da52-519e-8aff-b5312fb9336d';
DELETE FROM user_preferences WHERE category_id = '3e731663-da52-519e-8aff-b5312fb9336d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3e731663-da52-519e-8aff-b5312fb9336d';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '3e731663-da52-519e-8aff-b5312fb9336d';
DELETE FROM categories WHERE id = '3e731663-da52-519e-8aff-b5312fb9336d';
-- 'Polos' -> 'T-shirts'
UPDATE listings SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'bf34232e-d8e2-50b8-8417-5c77ab0fd549';
DELETE FROM user_preferences WHERE category_id = 'bf34232e-d8e2-50b8-8417-5c77ab0fd549' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd');
UPDATE user_preferences SET category_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE category_id = 'bf34232e-d8e2-50b8-8417-5c77ab0fd549';
UPDATE categories SET parent_id = '9c9f02e9-fb48-53b3-8af5-4a73ca1a67cd' WHERE parent_id = 'bf34232e-d8e2-50b8-8417-5c77ab0fd549';
DELETE FROM categories WHERE id = 'bf34232e-d8e2-50b8-8417-5c77ab0fd549';
-- 'Gilets de costume' -> 'Pulls, sweats et gilets'
UPDATE listings SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = 'ffac5df8-af0d-54d0-a4b3-b823e925da0d';
DELETE FROM user_preferences WHERE category_id = 'ffac5df8-af0d-54d0-a4b3-b823e925da0d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265');
UPDATE user_preferences SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = 'ffac5df8-af0d-54d0-a4b3-b823e925da0d';
UPDATE categories SET parent_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE parent_id = 'ffac5df8-af0d-54d0-a4b3-b823e925da0d';
DELETE FROM categories WHERE id = 'ffac5df8-af0d-54d0-a4b3-b823e925da0d';
-- 'Bermudas' -> 'Pantalons et shorts'
UPDATE listings SET category_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE category_id = '89af030b-7c4e-5267-b24f-fd1d53620a2e';
DELETE FROM user_preferences WHERE category_id = '89af030b-7c4e-5267-b24f-fd1d53620a2e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '785d358e-f083-51a2-8957-a5e8c84bc933');
UPDATE user_preferences SET category_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE category_id = '89af030b-7c4e-5267-b24f-fd1d53620a2e';
UPDATE categories SET parent_id = '785d358e-f083-51a2-8957-a5e8c84bc933' WHERE parent_id = '89af030b-7c4e-5267-b24f-fd1d53620a2e';
DELETE FROM categories WHERE id = '89af030b-7c4e-5267-b24f-fd1d53620a2e';
-- 'Sweats à capuche' -> 'Pulls, sweats et gilets'
UPDATE listings SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = '7f513db3-4c46-5b77-a88b-74f41d4c434b';
DELETE FROM user_preferences WHERE category_id = '7f513db3-4c46-5b77-a88b-74f41d4c434b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265');
UPDATE user_preferences SET category_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE category_id = '7f513db3-4c46-5b77-a88b-74f41d4c434b';
UPDATE categories SET parent_id = '280b284f-d0b7-54bd-be5c-25bbe8771265' WHERE parent_id = '7f513db3-4c46-5b77-a88b-74f41d4c434b';
DELETE FROM categories WHERE id = '7f513db3-4c46-5b77-a88b-74f41d4c434b';
-- 'Derbies et richelieus' -> 'Chaussures'
UPDATE listings SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'ce5a6c6c-c1cb-5194-8d72-20b1da6d98c1';
DELETE FROM user_preferences WHERE category_id = 'ce5a6c6c-c1cb-5194-8d72-20b1da6d98c1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '643090d8-7434-5c37-92a8-29372ae7340a');
UPDATE user_preferences SET category_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE category_id = 'ce5a6c6c-c1cb-5194-8d72-20b1da6d98c1';
UPDATE categories SET parent_id = '643090d8-7434-5c37-92a8-29372ae7340a' WHERE parent_id = 'ce5a6c6c-c1cb-5194-8d72-20b1da6d98c1';
DELETE FROM categories WHERE id = 'ce5a6c6c-c1cb-5194-8d72-20b1da6d98c1';
-- 'Sacoches' -> 'Sacs'
UPDATE listings SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '1aeb905c-12b7-5932-be0d-83bad15d60af';
DELETE FROM user_preferences WHERE category_id = '1aeb905c-12b7-5932-be0d-83bad15d60af' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b');
UPDATE user_preferences SET category_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE category_id = '1aeb905c-12b7-5932-be0d-83bad15d60af';
UPDATE categories SET parent_id = '353aa7ae-e01d-545c-99c2-9fa3264fdb9b' WHERE parent_id = '1aeb905c-12b7-5932-be0d-83bad15d60af';
DELETE FROM categories WHERE id = '1aeb905c-12b7-5932-be0d-83bad15d60af';
-- 'Bretelles' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '76aeb079-bd56-5cb7-ac40-d7392752d4a1';
DELETE FROM user_preferences WHERE category_id = '76aeb079-bd56-5cb7-ac40-d7392752d4a1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '76aeb079-bd56-5cb7-ac40-d7392752d4a1';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '76aeb079-bd56-5cb7-ac40-d7392752d4a1';
DELETE FROM categories WHERE id = '76aeb079-bd56-5cb7-ac40-d7392752d4a1';
-- 'Boutons de manchette' -> 'Accessoires'
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '558d051d-8927-54e1-991c-47ccc860485c';
DELETE FROM user_preferences WHERE category_id = '558d051d-8927-54e1-991c-47ccc860485c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '558d051d-8927-54e1-991c-47ccc860485c';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '558d051d-8927-54e1-991c-47ccc860485c';
DELETE FROM categories WHERE id = '558d051d-8927-54e1-991c-47ccc860485c';
-- 'Selhams' -> 'Autres vêtements traditionnels'
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'd47dce13-1ba2-532b-8c65-5179508f05ab';
DELETE FROM user_preferences WHERE category_id = 'd47dce13-1ba2-532b-8c65-5179508f05ab' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'd47dce13-1ba2-532b-8c65-5179508f05ab';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'd47dce13-1ba2-532b-8c65-5179508f05ab';
DELETE FROM categories WHERE id = 'd47dce13-1ba2-532b-8c65-5179508f05ab';
-- 'Sarouels' -> 'Autres vêtements traditionnels'
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '4fe4be80-c800-5a71-9178-c2e7927915ba';
DELETE FROM user_preferences WHERE category_id = '4fe4be80-c800-5a71-9178-c2e7927915ba' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '4fe4be80-c800-5a71-9178-c2e7927915ba';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = '4fe4be80-c800-5a71-9178-c2e7927915ba';
DELETE FROM categories WHERE id = '4fe4be80-c800-5a71-9178-c2e7927915ba';
-- 'Capes traditionnelles' -> 'Autres vêtements traditionnels'
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'e9b3b818-3999-5dea-8739-981aa4c69356';
DELETE FROM user_preferences WHERE category_id = 'e9b3b818-3999-5dea-8739-981aa4c69356' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'e9b3b818-3999-5dea-8739-981aa4c69356';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'e9b3b818-3999-5dea-8739-981aa4c69356';
DELETE FROM categories WHERE id = 'e9b3b818-3999-5dea-8739-981aa4c69356';
-- 'Babouches' -> 'Belghas'
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '0face1dc-e8a0-5ed9-b14e-4cf72ab57b28';
DELETE FROM user_preferences WHERE category_id = '0face1dc-e8a0-5ed9-b14e-4cf72ab57b28' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '0face1dc-e8a0-5ed9-b14e-4cf72ab57b28';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '0face1dc-e8a0-5ed9-b14e-4cf72ab57b28';
DELETE FROM categories WHERE id = '0face1dc-e8a0-5ed9-b14e-4cf72ab57b28';
-- 'Foulards et voiles' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '06a7e96a-ad5f-5286-9168-67662f5fef5a';
DELETE FROM user_preferences WHERE category_id = '06a7e96a-ad5f-5286-9168-67662f5fef5a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '06a7e96a-ad5f-5286-9168-67662f5fef5a';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '06a7e96a-ad5f-5286-9168-67662f5fef5a';
DELETE FROM categories WHERE id = '06a7e96a-ad5f-5286-9168-67662f5fef5a';
-- 'Bijoux traditionnels' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '65385d44-bdca-5ab3-8967-939bd7b30c7b';
DELETE FROM user_preferences WHERE category_id = '65385d44-bdca-5ab3-8967-939bd7b30c7b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '65385d44-bdca-5ab3-8967-939bd7b30c7b';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '65385d44-bdca-5ab3-8967-939bd7b30c7b';
DELETE FROM categories WHERE id = '65385d44-bdca-5ab3-8967-939bd7b30c7b';
-- 'Ceintures traditionnelles' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3e7999bc-2180-5262-b897-bc1aad7c8f1e';
DELETE FROM user_preferences WHERE category_id = '3e7999bc-2180-5262-b897-bc1aad7c8f1e' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3e7999bc-2180-5262-b897-bc1aad7c8f1e';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '3e7999bc-2180-5262-b897-bc1aad7c8f1e';
DELETE FROM categories WHERE id = '3e7999bc-2180-5262-b897-bc1aad7c8f1e';
-- 'Sacs traditionnels' -> 'Accessoires'
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'cd2800bb-2d6f-503b-9034-895eddadbe96';
DELETE FROM user_preferences WHERE category_id = 'cd2800bb-2d6f-503b-9034-895eddadbe96' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'cd2800bb-2d6f-503b-9034-895eddadbe96';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = 'cd2800bb-2d6f-503b-9034-895eddadbe96';
DELETE FROM categories WHERE id = 'cd2800bb-2d6f-503b-9034-895eddadbe96';
-- 'Bodies bébé' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = 'b6201d90-96ec-5993-a67b-2383af75bcbf';
DELETE FROM user_preferences WHERE category_id = 'b6201d90-96ec-5993-a67b-2383af75bcbf' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = 'b6201d90-96ec-5993-a67b-2383af75bcbf';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = 'b6201d90-96ec-5993-a67b-2383af75bcbf';
DELETE FROM categories WHERE id = 'b6201d90-96ec-5993-a67b-2383af75bcbf';
-- 'Grenouillères' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '1c8bdac9-fd00-5532-a42e-6de45de637d4';
DELETE FROM user_preferences WHERE category_id = '1c8bdac9-fd00-5532-a42e-6de45de637d4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '1c8bdac9-fd00-5532-a42e-6de45de637d4';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = '1c8bdac9-fd00-5532-a42e-6de45de637d4';
DELETE FROM categories WHERE id = '1c8bdac9-fd00-5532-a42e-6de45de637d4';
-- 'Ensembles bébé' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '18f5a8b7-1d39-5b0e-9fa1-e0b60cc5e5d0';
DELETE FROM user_preferences WHERE category_id = '18f5a8b7-1d39-5b0e-9fa1-e0b60cc5e5d0' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '18f5a8b7-1d39-5b0e-9fa1-e0b60cc5e5d0';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = '18f5a8b7-1d39-5b0e-9fa1-e0b60cc5e5d0';
DELETE FROM categories WHERE id = '18f5a8b7-1d39-5b0e-9fa1-e0b60cc5e5d0';
-- 'Gigoteuses' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '32013067-d745-5f23-927e-0df7de4bdad4';
DELETE FROM user_preferences WHERE category_id = '32013067-d745-5f23-927e-0df7de4bdad4' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '32013067-d745-5f23-927e-0df7de4bdad4';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = '32013067-d745-5f23-927e-0df7de4bdad4';
DELETE FROM categories WHERE id = '32013067-d745-5f23-927e-0df7de4bdad4';
-- 'Combinaisons bébé' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '8228e6b8-0f9d-5d31-b3f5-dd3dbaea9163';
DELETE FROM user_preferences WHERE category_id = '8228e6b8-0f9d-5d31-b3f5-dd3dbaea9163' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = '8228e6b8-0f9d-5d31-b3f5-dd3dbaea9163';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = '8228e6b8-0f9d-5d31-b3f5-dd3dbaea9163';
DELETE FROM categories WHERE id = '8228e6b8-0f9d-5d31-b3f5-dd3dbaea9163';
-- 'Autres vêtements bébé' -> 'Bébé'
UPDATE listings SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = 'fdbab94d-e982-59ec-8fba-ff54544ac500';
DELETE FROM user_preferences WHERE category_id = 'fdbab94d-e982-59ec-8fba-ff54544ac500' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '75aa697f-4e6f-543e-a566-ec856f75d702');
UPDATE user_preferences SET category_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE category_id = 'fdbab94d-e982-59ec-8fba-ff54544ac500';
UPDATE categories SET parent_id = '75aa697f-4e6f-543e-a566-ec856f75d702' WHERE parent_id = 'fdbab94d-e982-59ec-8fba-ff54544ac500';
DELETE FROM categories WHERE id = 'fdbab94d-e982-59ec-8fba-ff54544ac500';
-- 'Jackets et blousons' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'b626afba-7b2b-50ef-bcb9-132af75f0383';
DELETE FROM user_preferences WHERE category_id = 'b626afba-7b2b-50ef-bcb9-132af75f0383' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'b626afba-7b2b-50ef-bcb9-132af75f0383';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'b626afba-7b2b-50ef-bcb9-132af75f0383';
DELETE FROM categories WHERE id = 'b626afba-7b2b-50ef-bcb9-132af75f0383';
-- 'Jeans' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'c5bcca64-3c33-5eb4-aafc-464d923306ea';
DELETE FROM user_preferences WHERE category_id = 'c5bcca64-3c33-5eb4-aafc-464d923306ea' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'c5bcca64-3c33-5eb4-aafc-464d923306ea';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'c5bcca64-3c33-5eb4-aafc-464d923306ea';
DELETE FROM categories WHERE id = 'c5bcca64-3c33-5eb4-aafc-464d923306ea';
-- 'Ensembles' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'c2a639cd-9695-592c-bd11-3b1e7cfdcb3a';
DELETE FROM user_preferences WHERE category_id = 'c2a639cd-9695-592c-bd11-3b1e7cfdcb3a' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'c2a639cd-9695-592c-bd11-3b1e7cfdcb3a';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'c2a639cd-9695-592c-bd11-3b1e7cfdcb3a';
DELETE FROM categories WHERE id = 'c2a639cd-9695-592c-bd11-3b1e7cfdcb3a';
-- 'Maillots de bain' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '7aea999c-04d5-5567-bce4-6839d51147bb';
DELETE FROM user_preferences WHERE category_id = '7aea999c-04d5-5567-bce4-6839d51147bb' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '7aea999c-04d5-5567-bce4-6839d51147bb';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '7aea999c-04d5-5567-bce4-6839d51147bb';
DELETE FROM categories WHERE id = '7aea999c-04d5-5567-bce4-6839d51147bb';
-- 'Déguisements' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'e6f05e0a-8435-5267-a4aa-8b9b7e49e388';
DELETE FROM user_preferences WHERE category_id = 'e6f05e0a-8435-5267-a4aa-8b9b7e49e388' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'e6f05e0a-8435-5267-a4aa-8b9b7e49e388';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'e6f05e0a-8435-5267-a4aa-8b9b7e49e388';
DELETE FROM categories WHERE id = 'e6f05e0a-8435-5267-a4aa-8b9b7e49e388';
-- 'Sous-vêtements et chaussettes' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '0643e37b-73e8-5287-9a34-98a9a8b2b821';
DELETE FROM user_preferences WHERE category_id = '0643e37b-73e8-5287-9a34-98a9a8b2b821' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = '0643e37b-73e8-5287-9a34-98a9a8b2b821';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = '0643e37b-73e8-5287-9a34-98a9a8b2b821';
DELETE FROM categories WHERE id = '0643e37b-73e8-5287-9a34-98a9a8b2b821';
-- 'Autres vêtements enfant' -> 'Vêtements enfant'
UPDATE listings SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'b4217ccb-2903-5f06-9940-104fbdc3463c';
DELETE FROM user_preferences WHERE category_id = 'b4217ccb-2903-5f06-9940-104fbdc3463c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269');
UPDATE user_preferences SET category_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE category_id = 'b4217ccb-2903-5f06-9940-104fbdc3463c';
UPDATE categories SET parent_id = '6b9d8067-b786-526f-99b6-3df2ab9be269' WHERE parent_id = 'b4217ccb-2903-5f06-9940-104fbdc3463c';
DELETE FROM categories WHERE id = 'b4217ccb-2903-5f06-9940-104fbdc3463c';
-- 'Chaussons bébé' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'ce37d20a-cb69-5b15-bd26-a32fec357924';
DELETE FROM user_preferences WHERE category_id = 'ce37d20a-cb69-5b15-bd26-a32fec357924' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'ce37d20a-cb69-5b15-bd26-a32fec357924';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = 'ce37d20a-cb69-5b15-bd26-a32fec357924';
DELETE FROM categories WHERE id = 'ce37d20a-cb69-5b15-bd26-a32fec357924';
-- 'Baskets' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = '933b2aad-a4b4-5d07-a614-cb1928975a06';
DELETE FROM user_preferences WHERE category_id = '933b2aad-a4b4-5d07-a614-cb1928975a06' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = '933b2aad-a4b4-5d07-a614-cb1928975a06';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = '933b2aad-a4b4-5d07-a614-cb1928975a06';
DELETE FROM categories WHERE id = '933b2aad-a4b4-5d07-a614-cb1928975a06';
-- 'Bottes et bottines' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'da2349b9-fa42-5e63-838d-5904ec8aa001';
DELETE FROM user_preferences WHERE category_id = 'da2349b9-fa42-5e63-838d-5904ec8aa001' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'da2349b9-fa42-5e63-838d-5904ec8aa001';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = 'da2349b9-fa42-5e63-838d-5904ec8aa001';
DELETE FROM categories WHERE id = 'da2349b9-fa42-5e63-838d-5904ec8aa001';
-- 'Sandales' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'efdae7f7-77d5-5c2c-8a57-b4afac159475';
DELETE FROM user_preferences WHERE category_id = 'efdae7f7-77d5-5c2c-8a57-b4afac159475' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'efdae7f7-77d5-5c2c-8a57-b4afac159475';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = 'efdae7f7-77d5-5c2c-8a57-b4afac159475';
DELETE FROM categories WHERE id = 'efdae7f7-77d5-5c2c-8a57-b4afac159475';
-- 'Chaussons' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = '0b510910-223c-5e78-9418-e53c5d7291c3';
DELETE FROM user_preferences WHERE category_id = '0b510910-223c-5e78-9418-e53c5d7291c3' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = '0b510910-223c-5e78-9418-e53c5d7291c3';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = '0b510910-223c-5e78-9418-e53c5d7291c3';
DELETE FROM categories WHERE id = '0b510910-223c-5e78-9418-e53c5d7291c3';
-- 'Autres chaussures enfant' -> 'Chaussures'
UPDATE listings SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'f872b131-70fb-59ee-8aaf-2d5951204914';
DELETE FROM user_preferences WHERE category_id = 'f872b131-70fb-59ee-8aaf-2d5951204914' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf');
UPDATE user_preferences SET category_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE category_id = 'f872b131-70fb-59ee-8aaf-2d5951204914';
UPDATE categories SET parent_id = 'a5875435-98bf-5825-a654-a1bb44d428cf' WHERE parent_id = 'f872b131-70fb-59ee-8aaf-2d5951204914';
DELETE FROM categories WHERE id = 'f872b131-70fb-59ee-8aaf-2d5951204914';
-- 'Cartables' -> 'Accessoires'
UPDATE listings SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = 'fd9aed73-140e-56ce-8326-e0975eed4fae';
DELETE FROM user_preferences WHERE category_id = 'fd9aed73-140e-56ce-8326-e0975eed4fae' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96');
UPDATE user_preferences SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = 'fd9aed73-140e-56ce-8326-e0975eed4fae';
UPDATE categories SET parent_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE parent_id = 'fd9aed73-140e-56ce-8326-e0975eed4fae';
DELETE FROM categories WHERE id = 'fd9aed73-140e-56ce-8326-e0975eed4fae';
-- 'Sacs à dos' -> 'Accessoires'
UPDATE listings SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = '23c1176f-6835-53dc-9bd4-97d49537640b';
DELETE FROM user_preferences WHERE category_id = '23c1176f-6835-53dc-9bd4-97d49537640b' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96');
UPDATE user_preferences SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = '23c1176f-6835-53dc-9bd4-97d49537640b';
UPDATE categories SET parent_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE parent_id = '23c1176f-6835-53dc-9bd4-97d49537640b';
DELETE FROM categories WHERE id = '23c1176f-6835-53dc-9bd4-97d49537640b';
-- 'Chapeaux et bonnets' -> 'Accessoires'
UPDATE listings SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = 'bf2275b2-f144-5338-a7f7-a7efd4f777ca';
DELETE FROM user_preferences WHERE category_id = 'bf2275b2-f144-5338-a7f7-a7efd4f777ca' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96');
UPDATE user_preferences SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = 'bf2275b2-f144-5338-a7f7-a7efd4f777ca';
UPDATE categories SET parent_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE parent_id = 'bf2275b2-f144-5338-a7f7-a7efd4f777ca';
DELETE FROM categories WHERE id = 'bf2275b2-f144-5338-a7f7-a7efd4f777ca';
-- 'Bavoirs et langes' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'd9d2293d-8713-5ee7-b464-247109a69ecb';
DELETE FROM user_preferences WHERE category_id = 'd9d2293d-8713-5ee7-b464-247109a69ecb' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'd9d2293d-8713-5ee7-b464-247109a69ecb';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = 'd9d2293d-8713-5ee7-b464-247109a69ecb';
DELETE FROM categories WHERE id = 'd9d2293d-8713-5ee7-b464-247109a69ecb';
-- 'Couvertures bébé' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'd7eaf3e5-565a-55f0-b62e-8ac2facb8f58';
DELETE FROM user_preferences WHERE category_id = 'd7eaf3e5-565a-55f0-b62e-8ac2facb8f58' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'd7eaf3e5-565a-55f0-b62e-8ac2facb8f58';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = 'd7eaf3e5-565a-55f0-b62e-8ac2facb8f58';
DELETE FROM categories WHERE id = 'd7eaf3e5-565a-55f0-b62e-8ac2facb8f58';
-- 'Peluches' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '6038c0f4-3d17-5c30-857e-396a2999f0a7';
DELETE FROM user_preferences WHERE category_id = '6038c0f4-3d17-5c30-857e-396a2999f0a7' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '6038c0f4-3d17-5c30-857e-396a2999f0a7';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = '6038c0f4-3d17-5c30-857e-396a2999f0a7';
DELETE FROM categories WHERE id = '6038c0f4-3d17-5c30-857e-396a2999f0a7';
-- 'Jeux de société et puzzles' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '0b7db8a1-673c-5f46-9af1-def14a34fbec';
DELETE FROM user_preferences WHERE category_id = '0b7db8a1-673c-5f46-9af1-def14a34fbec' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '0b7db8a1-673c-5f46-9af1-def14a34fbec';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = '0b7db8a1-673c-5f46-9af1-def14a34fbec';
DELETE FROM categories WHERE id = '0b7db8a1-673c-5f46-9af1-def14a34fbec';
-- 'Jouets bébé' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '8d5b4f36-62a5-5fba-83ba-5968202973ee';
DELETE FROM user_preferences WHERE category_id = '8d5b4f36-62a5-5fba-83ba-5968202973ee' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = '8d5b4f36-62a5-5fba-83ba-5968202973ee';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = '8d5b4f36-62a5-5fba-83ba-5968202973ee';
DELETE FROM categories WHERE id = '8d5b4f36-62a5-5fba-83ba-5968202973ee';
-- 'Jouets éducatifs' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'b7f40213-2ac9-5503-967d-dc78d971ef35';
DELETE FROM user_preferences WHERE category_id = 'b7f40213-2ac9-5503-967d-dc78d971ef35' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'b7f40213-2ac9-5503-967d-dc78d971ef35';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = 'b7f40213-2ac9-5503-967d-dc78d971ef35';
DELETE FROM categories WHERE id = 'b7f40213-2ac9-5503-967d-dc78d971ef35';
-- 'Poupées et figurines' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'a2f76c5c-37cd-5028-a4e2-13c9ab2e1076';
DELETE FROM user_preferences WHERE category_id = 'a2f76c5c-37cd-5028-a4e2-13c9ab2e1076' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'a2f76c5c-37cd-5028-a4e2-13c9ab2e1076';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = 'a2f76c5c-37cd-5028-a4e2-13c9ab2e1076';
DELETE FROM categories WHERE id = 'a2f76c5c-37cd-5028-a4e2-13c9ab2e1076';
-- 'Véhicules et jeux de construction' -> 'Jouets'
UPDATE listings SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'eeb0690c-caab-538e-8bd2-cec2084867ba';
DELETE FROM user_preferences WHERE category_id = 'eeb0690c-caab-538e-8bd2-cec2084867ba' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331');
UPDATE user_preferences SET category_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE category_id = 'eeb0690c-caab-538e-8bd2-cec2084867ba';
UPDATE categories SET parent_id = '506d68f9-ecc4-5dc0-9355-2e74bca09331' WHERE parent_id = 'eeb0690c-caab-538e-8bd2-cec2084867ba';
DELETE FROM categories WHERE id = 'eeb0690c-caab-538e-8bd2-cec2084867ba';
-- 'Livres enfant' -> 'Accessoires'
UPDATE listings SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = '84524458-3932-549f-afec-ad98c1086c3d';
DELETE FROM user_preferences WHERE category_id = '84524458-3932-549f-afec-ad98c1086c3d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96');
UPDATE user_preferences SET category_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE category_id = '84524458-3932-549f-afec-ad98c1086c3d';
UPDATE categories SET parent_id = '9eff6390-c2fc-5683-a360-93c99c82eb96' WHERE parent_id = '84524458-3932-549f-afec-ad98c1086c3d';
DELETE FROM categories WHERE id = '84524458-3932-549f-afec-ad98c1086c3d';
-- 'Poussettes' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'a642b572-e59f-5a32-a4db-881ce444402d';
DELETE FROM user_preferences WHERE category_id = 'a642b572-e59f-5a32-a4db-881ce444402d' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = 'a642b572-e59f-5a32-a4db-881ce444402d';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = 'a642b572-e59f-5a32-a4db-881ce444402d';
DELETE FROM categories WHERE id = 'a642b572-e59f-5a32-a4db-881ce444402d';
-- 'Porte-bébés' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '14d81a79-3e67-5d81-8a36-272d82466caf';
DELETE FROM user_preferences WHERE category_id = '14d81a79-3e67-5d81-8a36-272d82466caf' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '14d81a79-3e67-5d81-8a36-272d82466caf';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = '14d81a79-3e67-5d81-8a36-272d82466caf';
DELETE FROM categories WHERE id = '14d81a79-3e67-5d81-8a36-272d82466caf';
-- 'Sacs à langer' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '2dfc4abe-a3f1-5176-8bf8-22fe85039368';
DELETE FROM user_preferences WHERE category_id = '2dfc4abe-a3f1-5176-8bf8-22fe85039368' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '2dfc4abe-a3f1-5176-8bf8-22fe85039368';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = '2dfc4abe-a3f1-5176-8bf8-22fe85039368';
DELETE FROM categories WHERE id = '2dfc4abe-a3f1-5176-8bf8-22fe85039368';
-- 'Accessoires de repas bébé' -> 'Périculture'
UPDATE listings SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '41ba34d0-06b1-503d-964f-952745b33751';
DELETE FROM user_preferences WHERE category_id = '41ba34d0-06b1-503d-964f-952745b33751' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '2975fe84-ef33-5056-b272-17f5c5c73333');
UPDATE user_preferences SET category_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE category_id = '41ba34d0-06b1-503d-964f-952745b33751';
UPDATE categories SET parent_id = '2975fe84-ef33-5056-b272-17f5c5c73333' WHERE parent_id = '41ba34d0-06b1-503d-964f-952745b33751';
DELETE FROM categories WHERE id = '41ba34d0-06b1-503d-964f-952745b33751';
-- 'Rideaux' -> 'Linge de maison'
UPDATE listings SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = 'd4cbaebe-f040-5d1d-925b-d2d564e62015';
DELETE FROM user_preferences WHERE category_id = 'd4cbaebe-f040-5d1d-925b-d2d564e62015' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2');
UPDATE user_preferences SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = 'd4cbaebe-f040-5d1d-925b-d2d564e62015';
UPDATE categories SET parent_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE parent_id = 'd4cbaebe-f040-5d1d-925b-d2d564e62015';
DELETE FROM categories WHERE id = 'd4cbaebe-f040-5d1d-925b-d2d564e62015';
-- 'Plaids et couvertures' -> 'Linge de maison'
UPDATE listings SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = '0bf1c64e-c564-53e7-8f8e-d3565f8cae4f';
DELETE FROM user_preferences WHERE category_id = '0bf1c64e-c564-53e7-8f8e-d3565f8cae4f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2');
UPDATE user_preferences SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = '0bf1c64e-c564-53e7-8f8e-d3565f8cae4f';
UPDATE categories SET parent_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE parent_id = '0bf1c64e-c564-53e7-8f8e-d3565f8cae4f';
DELETE FROM categories WHERE id = '0bf1c64e-c564-53e7-8f8e-d3565f8cae4f';
-- 'Miroirs' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = '2e7e3b3a-daa3-5086-b7df-5a5f48f771f2';
DELETE FROM user_preferences WHERE category_id = '2e7e3b3a-daa3-5086-b7df-5a5f48f771f2' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = '2e7e3b3a-daa3-5086-b7df-5a5f48f771f2';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = '2e7e3b3a-daa3-5086-b7df-5a5f48f771f2';
DELETE FROM categories WHERE id = '2e7e3b3a-daa3-5086-b7df-5a5f48f771f2';
-- 'Vases' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = '5db8b131-79c4-518c-8735-4098890445d7';
DELETE FROM user_preferences WHERE category_id = '5db8b131-79c4-518c-8735-4098890445d7' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = '5db8b131-79c4-518c-8735-4098890445d7';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = '5db8b131-79c4-518c-8735-4098890445d7';
DELETE FROM categories WHERE id = '5db8b131-79c4-518c-8735-4098890445d7';
-- 'Cadres et affiches' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'd490c09b-19b5-5b67-997a-4a232947e5e6';
DELETE FROM user_preferences WHERE category_id = 'd490c09b-19b5-5b67-997a-4a232947e5e6' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'd490c09b-19b5-5b67-997a-4a232947e5e6';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = 'd490c09b-19b5-5b67-997a-4a232947e5e6';
DELETE FROM categories WHERE id = 'd490c09b-19b5-5b67-997a-4a232947e5e6';
-- 'Décorations saisonnières' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'aa8bff05-e263-569d-a235-44ef9a2207e5';
DELETE FROM user_preferences WHERE category_id = 'aa8bff05-e263-569d-a235-44ef9a2207e5' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'aa8bff05-e263-569d-a235-44ef9a2207e5';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = 'aa8bff05-e263-569d-a235-44ef9a2207e5';
DELETE FROM categories WHERE id = 'aa8bff05-e263-569d-a235-44ef9a2207e5';
-- 'Draps et housses de couette' -> 'Linge de maison'
UPDATE listings SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = 'a7574a72-4bbf-5951-89fc-d374c0cb442c';
DELETE FROM user_preferences WHERE category_id = 'a7574a72-4bbf-5951-89fc-d374c0cb442c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2');
UPDATE user_preferences SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = 'a7574a72-4bbf-5951-89fc-d374c0cb442c';
UPDATE categories SET parent_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE parent_id = 'a7574a72-4bbf-5951-89fc-d374c0cb442c';
DELETE FROM categories WHERE id = 'a7574a72-4bbf-5951-89fc-d374c0cb442c';
-- 'Serviettes et linge de bain' -> 'Linge de maison'
UPDATE listings SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = '123ef506-5a24-53a7-8935-57a0b5014681';
DELETE FROM user_preferences WHERE category_id = '123ef506-5a24-53a7-8935-57a0b5014681' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2');
UPDATE user_preferences SET category_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE category_id = '123ef506-5a24-53a7-8935-57a0b5014681';
UPDATE categories SET parent_id = '059a92f6-58ae-5ae1-bbc8-82b39bec87d2' WHERE parent_id = '123ef506-5a24-53a7-8935-57a0b5014681';
DELETE FROM categories WHERE id = '123ef506-5a24-53a7-8935-57a0b5014681';
-- 'Vaisselle' -> 'Art de la table'
UPDATE listings SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = 'f95b430e-c1a5-5802-89ed-360d5460a561';
DELETE FROM user_preferences WHERE category_id = 'f95b430e-c1a5-5802-89ed-360d5460a561' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a');
UPDATE user_preferences SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = 'f95b430e-c1a5-5802-89ed-360d5460a561';
UPDATE categories SET parent_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE parent_id = 'f95b430e-c1a5-5802-89ed-360d5460a561';
DELETE FROM categories WHERE id = 'f95b430e-c1a5-5802-89ed-360d5460a561';
-- 'Verrerie' -> 'Art de la table'
UPDATE listings SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = '43b62580-d8df-59ee-a53f-9ebdc2566d7f';
DELETE FROM user_preferences WHERE category_id = '43b62580-d8df-59ee-a53f-9ebdc2566d7f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a');
UPDATE user_preferences SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = '43b62580-d8df-59ee-a53f-9ebdc2566d7f';
UPDATE categories SET parent_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE parent_id = '43b62580-d8df-59ee-a53f-9ebdc2566d7f';
DELETE FROM categories WHERE id = '43b62580-d8df-59ee-a53f-9ebdc2566d7f';
-- 'Ustensiles de cuisine' -> 'Art de la table'
UPDATE listings SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = '27712725-8f06-52a3-8b27-8ce25c59653f';
DELETE FROM user_preferences WHERE category_id = '27712725-8f06-52a3-8b27-8ce25c59653f' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a');
UPDATE user_preferences SET category_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE category_id = '27712725-8f06-52a3-8b27-8ce25c59653f';
UPDATE categories SET parent_id = '9421cd84-e6a3-5e0c-b4d4-caf810f2b90a' WHERE parent_id = '27712725-8f06-52a3-8b27-8ce25c59653f';
DELETE FROM categories WHERE id = '27712725-8f06-52a3-8b27-8ce25c59653f';
-- 'Bougies et bougeoirs' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'fbb51da1-db9c-5f2c-a22f-a74f209f343c';
DELETE FROM user_preferences WHERE category_id = 'fbb51da1-db9c-5f2c-a22f-a74f209f343c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'fbb51da1-db9c-5f2c-a22f-a74f209f343c';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = 'fbb51da1-db9c-5f2c-a22f-a74f209f343c';
DELETE FROM categories WHERE id = 'fbb51da1-db9c-5f2c-a22f-a74f209f343c';
-- 'Paniers et boîtes' -> 'Rangement'
UPDATE listings SET category_id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130' WHERE category_id = '30e478a5-e97c-588e-a34b-169d77e382e1';
DELETE FROM user_preferences WHERE category_id = '30e478a5-e97c-588e-a34b-169d77e382e1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130');
UPDATE user_preferences SET category_id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130' WHERE category_id = '30e478a5-e97c-588e-a34b-169d77e382e1';
UPDATE categories SET parent_id = '4c934d61-f71b-5d67-a57f-cd2cb0f6b130' WHERE parent_id = '30e478a5-e97c-588e-a34b-169d77e382e1';
DELETE FROM categories WHERE id = '30e478a5-e97c-588e-a34b-169d77e382e1';
-- 'Autres articles maison' -> 'Déco'
UPDATE listings SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'd3d94de2-a6b5-5cd6-9e44-e0d8ca460a85';
DELETE FROM user_preferences WHERE category_id = 'd3d94de2-a6b5-5cd6-9e44-e0d8ca460a85' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920');
UPDATE user_preferences SET category_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE category_id = 'd3d94de2-a6b5-5cd6-9e44-e0d8ca460a85';
UPDATE categories SET parent_id = '5e18cd5a-f228-53d3-80a8-aaa9fe4a3920' WHERE parent_id = 'd3d94de2-a6b5-5cd6-9e44-e0d8ca460a85';
DELETE FROM categories WHERE id = 'd3d94de2-a6b5-5cd6-9e44-e0d8ca460a85';

-- Merge Gants into Accessoires
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c';
DELETE FROM user_preferences WHERE category_id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c';
DELETE FROM categories WHERE id = 'c6825ff5-b146-5394-bae2-569f2c3c5e3c';

-- Merge Gants into Accessoires
UPDATE listings SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '19c23238-eba7-5497-8f0f-b64c5720faf6';
DELETE FROM user_preferences WHERE category_id = '19c23238-eba7-5497-8f0f-b64c5720faf6' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36');
UPDATE user_preferences SET category_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE category_id = '19c23238-eba7-5497-8f0f-b64c5720faf6';
UPDATE categories SET parent_id = 'b89e2afd-f253-565b-b8c8-e7d9f140bf36' WHERE parent_id = '19c23238-eba7-5497-8f0f-b64c5720faf6';
DELETE FROM categories WHERE id = '19c23238-eba7-5497-8f0f-b64c5720faf6';

-- Remove former traditional categories after moving references. Ambiguous legacy items default to Femme; review manually.
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'a2983f81-d956-5728-868d-e32860dcac91';
DELETE FROM user_preferences WHERE category_id = 'a2983f81-d956-5728-868d-e32860dcac91' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'a2983f81-d956-5728-868d-e32860dcac91';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'a2983f81-d956-5728-868d-e32860dcac91';
DELETE FROM categories WHERE id = 'a2983f81-d956-5728-868d-e32860dcac91';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '14fea17d-2a98-530b-9d68-51ac27427d65';
DELETE FROM user_preferences WHERE category_id = '14fea17d-2a98-530b-9d68-51ac27427d65' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '14fea17d-2a98-530b-9d68-51ac27427d65';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = '14fea17d-2a98-530b-9d68-51ac27427d65';
DELETE FROM categories WHERE id = '14fea17d-2a98-530b-9d68-51ac27427d65';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '6d53e10f-3d9b-5f1f-a491-9128b8c21f9c';
DELETE FROM user_preferences WHERE category_id = '6d53e10f-3d9b-5f1f-a491-9128b8c21f9c' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '6d53e10f-3d9b-5f1f-a491-9128b8c21f9c';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = '6d53e10f-3d9b-5f1f-a491-9128b8c21f9c';
DELETE FROM categories WHERE id = '6d53e10f-3d9b-5f1f-a491-9128b8c21f9c';
UPDATE listings SET category_id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5' WHERE category_id = '6882c5ce-317f-5c3d-9332-fe607987bfe1';
DELETE FROM user_preferences WHERE category_id = '6882c5ce-317f-5c3d-9332-fe607987bfe1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5');
UPDATE user_preferences SET category_id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5' WHERE category_id = '6882c5ce-317f-5c3d-9332-fe607987bfe1';
UPDATE categories SET parent_id = 'f1dac599-2e1d-55d8-9cde-a04d02a5d8d5' WHERE parent_id = '6882c5ce-317f-5c3d-9332-fe607987bfe1';
DELETE FROM categories WHERE id = '6882c5ce-317f-5c3d-9332-fe607987bfe1';
UPDATE listings SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '70184df3-3cc7-5638-af83-82d6d0d00834';
DELETE FROM user_preferences WHERE category_id = '70184df3-3cc7-5638-af83-82d6d0d00834' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354');
UPDATE user_preferences SET category_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE category_id = '70184df3-3cc7-5638-af83-82d6d0d00834';
UPDATE categories SET parent_id = 'deeef5b3-1228-51a7-85e5-0f6cc5406354' WHERE parent_id = '70184df3-3cc7-5638-af83-82d6d0d00834';
DELETE FROM categories WHERE id = '70184df3-3cc7-5638-af83-82d6d0d00834';
UPDATE listings SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3d8f77e5-f449-5822-98ac-6a46f2b5bb19';
DELETE FROM user_preferences WHERE category_id = '3d8f77e5-f449-5822-98ac-6a46f2b5bb19' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37');
UPDATE user_preferences SET category_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE category_id = '3d8f77e5-f449-5822-98ac-6a46f2b5bb19';
UPDATE categories SET parent_id = '6cf54c8e-d29c-555d-8a29-330d75d52b37' WHERE parent_id = '3d8f77e5-f449-5822-98ac-6a46f2b5bb19';
DELETE FROM categories WHERE id = '3d8f77e5-f449-5822-98ac-6a46f2b5bb19';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'f0b6eec1-9e45-5633-9e3c-4d15844fac95';
DELETE FROM user_preferences WHERE category_id = 'f0b6eec1-9e45-5633-9e3c-4d15844fac95' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'f0b6eec1-9e45-5633-9e3c-4d15844fac95';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'f0b6eec1-9e45-5633-9e3c-4d15844fac95';
DELETE FROM categories WHERE id = 'f0b6eec1-9e45-5633-9e3c-4d15844fac95';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'c4aa3167-5918-554e-b436-959ebe23dcd1';
DELETE FROM user_preferences WHERE category_id = 'c4aa3167-5918-554e-b436-959ebe23dcd1' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'c4aa3167-5918-554e-b436-959ebe23dcd1';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'c4aa3167-5918-554e-b436-959ebe23dcd1';
DELETE FROM categories WHERE id = 'c4aa3167-5918-554e-b436-959ebe23dcd1';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'a14ba60e-ee7e-539d-9110-64037c877e22';
DELETE FROM user_preferences WHERE category_id = 'a14ba60e-ee7e-539d-9110-64037c877e22' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = 'a14ba60e-ee7e-539d-9110-64037c877e22';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = 'a14ba60e-ee7e-539d-9110-64037c877e22';
DELETE FROM categories WHERE id = 'a14ba60e-ee7e-539d-9110-64037c877e22';
UPDATE listings SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '93f2f6a2-6935-5c87-becd-c65ef92c8b84';
DELETE FROM user_preferences WHERE category_id = '93f2f6a2-6935-5c87-becd-c65ef92c8b84' AND user_id IN (SELECT user_id FROM user_preferences WHERE category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b');
UPDATE user_preferences SET category_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE category_id = '93f2f6a2-6935-5c87-becd-c65ef92c8b84';
UPDATE categories SET parent_id = 'a55fb5d3-11f5-5f66-a2c5-fc61a467656b' WHERE parent_id = '93f2f6a2-6935-5c87-becd-c65ef92c8b84';
DELETE FROM categories WHERE id = '93f2f6a2-6935-5c87-becd-c65ef92c8b84';