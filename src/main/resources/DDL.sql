create table if not exists web.users
(
    username varchar(25)          not null primary key ,
    password varchar(25)          not null,
    birthday date                 not null,
    email    varchar(255)         not null,
    country  varchar(50)          not null,
    isAdmin  tinyint(1) default 0 not null,
    constraint users_username_uindex
        unique (username)
);
