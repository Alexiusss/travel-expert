create table likes
(
    user_id   varchar(255) not null,
    review_id varchar(255) not null,
    primary key (user_id, review_id),
    foreign key (review_id) references reviews (id) on delete cascade
);

create unique index review_user_like_unique on likes (user_id, review_id);