create table images(
                         id varchar(255) not null,
                         created_at timestamp not null,
                         modified_at timestamp,
                         version int4 not null,
                         file_name varchar(255) not null,
                         file_type varchar(255) not null,
                         size int8 not null,
                         data bytea not null,
                         primary key (id)
);