DELETE
FROM subscriptions;

INSERT INTO subscriptions (channel_id, subscriber_id)
VALUES ('1', '2'),
       ('1', '3'),
       ('2', '1'),
       ('3', '2'),
       ('4', '2'),
       ('3', '4');