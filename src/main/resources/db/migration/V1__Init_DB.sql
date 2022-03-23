create table users (
                       id varchar(255) not null,
                       created_at timestamp not null,
                       modified_at timestamp,
                       version int4 not null,
                       activation_code varchar(255),
                       enabled bool default false not null,
                       email varchar(255) not null,
                       first_name varchar(255),
                       last_name varchar(255),
                       password varchar(255) not null,
                       primary key (id)
);

create unique index user_email_unique on users (email);

create table user_role (
                           user_id varchar(255) not null,
                           role varchar(255),
                           constraint user_roles_unique unique (user_id, role),
                           foreign key (user_id) references users (id) on delete cascade
);