create table notifications
(
    id      varchar(255) not null,
    sent_at timestamp    not null,
    to_recipient_id      varchar(255) not null,
    to_recipient_email      varchar(255) not null,
    sender      varchar(255) not null,
    message      varchar(255) not null,
    primary key (id)
)