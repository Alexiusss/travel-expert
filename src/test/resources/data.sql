DELETE
FROM user_role;
DELETE
FROM users;

INSERT INTO users (id, created_at, modified_at, version, activation_code, enabled, email, first_name, last_name, password)
VALUES ('1', TIMESTAMP '2022-04-05 21:36:19.543515', null, 1, null, true, 'admin@gmail.com', 'Admin', 'AdminLast','1'),
       ('2', TIMESTAMP '2022-04-05 21:36:19.543515', null, 1, null, true, 'moder@gmail.com', 'Moder', 'ModerLast','1'),
       ('3', TIMESTAMP '2022-04-05 21:36:19.543515', null, 1, null, true, 'user@gmail.com', 'User', 'UserLast','1');

INSERT INTO user_role (role, user_id)
VALUES ('ADMIN', 1),
       ('MODERATOR', 1),
       ('USER', 1),
       ('MODERATOR', 2),
       ('USER', 2),
       ('USER', 3);