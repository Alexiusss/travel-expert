DELETE
FROM user_entity;
DELETE
FROM user_role_mapping;
DELETE
FROM keycloak_role;

INSERT INTO user_entity (id, email, email_constraint, email_verified, enabled, first_name, last_name, realm_id, username, created_timestamp)
VALUES ('user-id', 'user@test.com', 'user@test.com', true, true, 'User', 'One', 'travel-expert-realm', 'travel-user', 1684924946651),
       ('moder-id', 'moder@test.com', 'moder@test.com', true, true, 'Moder', 'One', 'travel-expert-realm', 'travel-moder', 1684924946652),
       ('admin-id', 'admin@test.com', 'admin@test.com', true, true, 'Admin', 'One', 'travel-expert-realm', 'travel-admin', 1684924946653);


INSERT INTO user_role_mapping (role_id, user_id)
VALUES ('user-role-id', 'user-id'),
       ('user-role-id', 'moder-id'),
       ('moder-role-id', 'moder-id'),
       ('user-role-id', 'admin-id'),
       ('moder-role-id', 'admin-id'),
       ('admin-role-id', 'admin-id');

INSERT INTO keycloak_role (id, client_realm_constraint, client_role, name, realm_id)
VALUES ('user-role-id', 'travel-expert-realm', false,  'USER', 'travel-expert-realm'),
       ('moder-role-id', 'travel-expert-realm', false,  'MODERATOR', 'travel-expert-realm'),
       ('admin-role-id', 'travel-expert-realm', false,  'ADMIN', 'travel-expert-realm');