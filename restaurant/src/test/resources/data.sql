DELETE
FROM restaurants;

INSERT INTO  restaurants (id, created_at, modified_at, version, name, cuisine, filename, email, address, phone_number, website)
VALUES ('1', NOW(), NOW(), 0, 'restaurant1', 'restaurant1 cuisine', null, 'restaurant1@gmail.com', 'restaurant1 address', '+1111111111', 'restaurant1.com'),
       ('2', NOW(), NOW(), 0, 'restaurant2', 'restaurant2 cuisine', null, 'restaurant2@gmail.com', 'restaurant2 address', '+2222222222', 'restaurant2.com'),
       ('3', '2022-07-25 10:39:59.711012', '2022-07-25 10:39:59.711012', 0, 'restaurant3', 'restaurant3 cuisine', null, 'restaurant3@gmail.com', 'restaurant3 address', '+3333333333', 'restaurant3.com');