alter table users
    add username varchar(255);

create unique index username_unique on users (username);