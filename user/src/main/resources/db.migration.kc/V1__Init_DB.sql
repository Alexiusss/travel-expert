create table subscriptions
(
    channel_id    varchar(255) not null,
    subscriber_id varchar(255) not null,
    primary key (channel_id, subscriber_id),
    constraint subscribers_unique unique (channel_id, subscriber_id)
)