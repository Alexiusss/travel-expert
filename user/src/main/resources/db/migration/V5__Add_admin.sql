insert into users (id, created_at, modified_at, version, email, activation_code, enabled, first_name, last_name, password, file_name, username)
values ('1', NOW(), null, 0, 'admin@gmail.com', null, true,  'Admin', 'First', '{bcrypt}$2a$10$97dYrAzhqyagIw3met.lhOZ5W8Bm.Ez078pBTAYK5J6m.TzxEbtCy', 'empty', 'AdminMain');

insert into user_role (user_id, role)
values ('1', 'USER'),
       ('1', 'MODERATOR'),
       ('1', 'ADMIN');