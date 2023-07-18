DELETE FROM images;

INSERT INTO images (id, created_at, modified_at, version, file_name, file_type, size, data, user_id)
VALUES ('11', NOW(), null, '0', 'image1.jpg', 'image/jpeg', 779360, 'image1'::bytea, '3'),
       ('12', NOW(), null, '0',  'image2.jpg', 'image/jpeg', 559333, 'image2'::bytea, '3');