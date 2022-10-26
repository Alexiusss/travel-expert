DELETE
FROM restaurants;

INSERT INTO  restaurants (id, created_at, modified_at, version, name, cuisine, file_names, email, address, phone_number, website)
VALUES ('1', NOW(), NOW(), 0, 'restaurant1', 'restaurant1 cuisine', null, 'restaurant1@gmail.com', 'restaurant1 address', '+1 (111) 111-11-11', 'restaurant1.com'),
       ('2', NOW(), NOW(), 0, 'restaurant2', 'restaurant2 cuisine', null, 'restaurant2@gmail.com', 'restaurant2 address', '+2 (222) 222-22-22', 'restaurant2.com'),
       ('3', '2022-07-25 10:39:59.711012', '2022-07-25 10:39:59.711012', 0, 'restaurant3', 'restaurant3 cuisine', null, 'restaurant3@gmail.com', 'restaurant3 address', '+3 (333) 333-33-33', 'restaurant3.com');