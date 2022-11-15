create table user_subscriptions
(
    channel_id    varchar(255) not null,
    subscriber_id varchar(255) not null,
    primary key (channel_id, subscriber_id),
    foreign key (channel_id) references users (id) on delete cascade,
    foreign key (subscriber_id) references users (id) on delete cascade
)