DELETE
FROM hotels;

INSERT INTO hotels (id, created_at, modified_at, version, name, email, address, phone_number, website, hotel_class,
                    description, room_features, room_types, services_and_facilitates, languages_used, hotel_style,
                    file_names)
VALUES ('1', '2023-01-25T10:05:23.583567', '2023-01-25T10:05:23.583567', 0, 'Hotel1 name', 'hotel1@gmail.com',
        'Hotel1 address', '+1111111111', 'hotel1.com', 5, 'Hotel1 description', '{Sea view, Kitchen}',
        '{single, double}', '{}', '{English, Russian}', '{}', '{hotel1.jpg}'),
       ('2', NOW(), NOW(), 0, 'Hotel2 name', 'hotel2@gmail.com', 'Hotel2 address', '+2222222222', 'hotel2.com', 5,
        'Hotel2 description', '{Big beds, Walk-in shower}', '{double}', '{Bar, Yoga room}', '{English, Turkey}', '{}',
        '{hotel2.jpg}'),
       ('3', NOW(), null, 0, 'Hotel3 name', 'hotel3@gmail.com', 'Hotel3 address', '+3333333333', 'hotel3.com', 5,
        'Hotel3 description', '{Blackout curtains, Air conditioning}', '{double, triple}', null, '{English, Spanish}',
        '{Trendy, Charming}', '{hotel3.jpg}');