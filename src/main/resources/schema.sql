CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255)                            NOT NULL,
    email   VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT GENERATED BY DEFAULT AS IDENTITY,
    description varchar(200),
    name        varchar(100) NOT NULL,
    available   boolean,
    owner_id    BIGINT       NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    FOREIGN KEY (owner_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id     BIGINT GENERATED BY DEFAULT AS IDENTITY,
    booking_status varchar(8),
    start_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id        BIGINT                      NOT NULL,
    booker_id      BIGINT                      NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (booking_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id),
    FOREIGN KEY (booker_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id     BIGINT GENERATED BY DEFAULT AS IDENTITY,
    item_id        BIGINT                      NOT NULL,
    commentator_id BIGINT                      NOT NULL,
    create_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    text           VARCHAR(2000)               NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (comment_id),
    FOREIGN KEY (commentator_id) REFERENCES users (user_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id)
);