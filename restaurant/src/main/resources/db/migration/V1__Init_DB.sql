create table restaurants (
                       id varchar(255) not null,
                       created_at timestamp not null,
                       modified_at timestamp,
                       version int4 not null,
                       name varchar(255),
                       cuisine varchar(255) not null,
                       file_names TEXT[],
                       email varchar(255) not null,
                       address varchar(255) not null,
                       phone_number varchar(255) not null,
                       website varchar(255),
                       primary key (id)
);

create unique index restaurant_email_unique on restaurants (email);