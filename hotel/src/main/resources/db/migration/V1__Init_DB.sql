create table hotels (
                             id varchar(255) not null,
                             created_at timestamp not null,
                             modified_at timestamp,
                             version int4 not null,
                             name varchar(255) not null,
                             address varchar(255) not null,
                             email varchar(255) not null,
                             phone_number varchar(255) not null,
                             website varchar(255),
                             description text,
                             hotel_class int4 not null,
                             room_features text[],
                             room_types text[],
                             services_and_facilitates text[],
                             languages_used text[],
                             hotel_style text[],
                             file_names text[],
                             primary key (id)
);

create unique index hotel_email_unique on hotels (email);