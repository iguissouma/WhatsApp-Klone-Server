create table if not exists chats
(
    id serial not null,
    constraint chats_pkey
        primary key (id)
);

create table if not exists users
(
    id       serial       not null,
    username varchar(50)  not null,
    name     varchar(50)  not null,
    password varchar(255) not null,
    picture  varchar(255) not null,
    constraint users_pkey
        primary key (id),
    constraint users_username_key
        unique (username)
);

create table if not exists chats_users
(
    chat_id integer not null,
    user_id integer not null,
    constraint chats_users_chat_id_fkey
        foreign key (chat_id) references chats
            on delete cascade,
    constraint chats_users_user_id_fkey
        foreign key (user_id) references users
            on delete cascade
);

create table if not exists messages
(
    id             serial                  not null,
    content        varchar(355)            not null,
    created_at     timestamp default now() not null,
    chat_id        integer                 not null,
    sender_user_id integer                 not null,
    constraint messages_pkey
        primary key (id),
    constraint messages_chat_id_fkey
        foreign key (chat_id) references chats
            on delete cascade,
    constraint messages_sender_user_id_fkey
        foreign key (sender_user_id) references users
            on delete cascade
);

