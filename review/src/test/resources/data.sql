DELETE
FROM reviews;

DELETE
FROM likes;

INSERT INTO reviews (id, created_at, modified_at, active, version, title, description, rating, file_names, user_id, item_id)
VALUES ('11', NOW(), null, true, 0, 'review #1', 'review #1 description', 5, null, 1, 2),
       ('12', NOW(), null, false, 0, 'review #2', 'review #2 description', 4, null, 2, 1),
       ('13', '2022-08-23 19:06:03.621013', null, true, 0, 'review #3', 'review #3 description', 3, null, 3, 1);

INSERT INTO likes (user_id, review_id)
VALUES ('1', '11'),
       ('2', '11');