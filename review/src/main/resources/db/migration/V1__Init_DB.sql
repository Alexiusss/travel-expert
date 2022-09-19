create table reviews (
                       id varchar(255) not null,
                       created_at timestamp not null,
                       modified_at timestamp,
                       active bool default false not null,
                       version int4 not null,
                       title varchar(255) not null,
                       description varchar(255),
                       rating int4 not null,
                       file_names TEXT[],
                       user_id varchar(255) not null,
                       item_id varchar(255) not null,
                       primary key (id)
);

create unique index review_user_unique on reviews (user_id, item_id);