DELETE
FROM user_role;
DELETE
FROM user_subscriptions;
DELETE
FROM users;

INSERT INTO users (id, created_at, modified_at, version, activation_code, enabled, email, first_name, last_name, username, password)
VALUES ('1', NOW(), NOW(), 0, null, true, 'admin@gmail.com', 'Admin', 'AdminLast', 'adminUserName','{bcrypt}$2a$10$7tv2rvUDR37OsFxUjXpQmeBHnBT9JGvro9F.igDQ0HsYlw/A1OCsK'),
       ('2', NOW(), NOW(), 0, null, true, 'moder@gmail.com', 'Moder', 'ModerLast', 'moderUserName','{bcrypt}$2a$10$cvsdB43HwQaDeP13GhGGA.7.cBXTzjYErg1u6T2k6TpWyQPUETpiy'),
       ('3', '2022-04-27 19:16:53.292882', '2022-04-27 19:16:53.292882', 0, null, true, 'user@gmail.com', 'User', 'UserLast', 'userName','{bcrypt}$2a$10$MCR.pfu.PVQiPK4Tu6fAZukpR1bj1TD8YyF/GmSRIpxic2lHaqb5y');

INSERT INTO user_role (role, user_id)
VALUES ('ADMIN', 1),
       ('MODERATOR', 1),
       ('USER', 1),
       ('MODERATOR', 2),
       ('USER', 2),
       ('USER', 3);

INSERT INTO user_subscriptions (channel_id, subscriber_id)
VALUES ( '1','2' ),
       ( '1','3' );