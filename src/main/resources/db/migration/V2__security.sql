CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(128) UNIQUE NOT NULL,
    password VARCHAR             NOT NULL,
    role     VARCHAR(32)         NOT NULL
);

INSERT INTO users (username, password, role)
VALUES ('manager', '$2a$10$zrqlw6e91KAJnRp4ZGHwDO9wgdNUYPC5Q8Y/iAs73fXqJ4xpOUV/W', 'MANAGER');
