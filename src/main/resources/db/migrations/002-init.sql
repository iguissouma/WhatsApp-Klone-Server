INSERT INTO users (id, username, name, password, picture) VALUES (1, 'ray', 'Ray Edwards', '$2a$08$NO9tkFLCoSqX1c5wk3s7z.JfxaVMKA.m7zUDdDwEquo4rvzimQeJm', 'https://randomuser.me/api/portraits/thumb/lego/1.jpg');
INSERT INTO users (id, username, name, password, picture) VALUES (2, 'ethan', 'Ethan Gonzalez', '$2a$08$xE4FuCi/ifxjL2S8CzKAmuKLwv18ktksSN.F3XYEnpmcKtpbpeZgO', 'https://randomuser.me/api/portraits/thumb/men/1.jpg');
INSERT INTO users (id, username, name, password, picture) VALUES (3, 'bryan', 'Bryan Wallace', '$2a$08$UHgH7J8G6z1mGQn2qx2kdeWv0jvgHItyAsL9hpEUI3KJmhVW5Q1d.', 'https://randomuser.me/api/portraits/thumb/men/2.jpg');
INSERT INTO users (id, username, name, password, picture) VALUES (4, 'avery', 'Avery Stewart', '$2a$08$wR1k5Q3T9FC7fUgB7Gdb9Os/GV7dGBBf4PLlWT7HERMFhmFDt47xi', 'https://randomuser.me/api/portraits/thumb/women/1.jpg');
INSERT INTO users (id, username, name, password, picture) VALUES (5, 'katie', 'Katie Peterson', '$2a$08$6.mbXqsDX82ZZ7q5d8Osb..JrGSsNp4R3IKj7mxgF6YGT0OmMw242', 'https://randomuser.me/api/portraits/thumb/women/2.jpg');
SELECT setval('users_id_seq', (SELECT max(id) FROM users));

INSERT INTO chats (id) VALUES (1);
INSERT INTO chats (id) VALUES (2);
INSERT INTO chats (id) VALUES (3);
INSERT INTO chats (id) VALUES (4);
SELECT setval('chats_id_seq', (SELECT max(id) FROM chats));

INSERT INTO messages (id, content, created_at, chat_id, sender_user_id) VALUES (1, 'You on your way?', '2018-12-31 08:20:00.000000', 1, 1);
INSERT INTO messages (id, content, created_at, chat_id, sender_user_id) VALUES (2, 'Hey, it''s me', '2018-12-30 15:40:00.000000', 2, 1);
INSERT INTO messages (id, content, created_at, chat_id, sender_user_id) VALUES (3, 'I should buy a boat', '2018-12-15 09:00:00.000000', 3, 1);
INSERT INTO messages (id, content, created_at, chat_id, sender_user_id) VALUES (4, 'This is wicked good ice cream.', '2018-05-12 18:00:00.000000', 4, 1);
SELECT setval('messages_id_seq', (SELECT max(id) FROM messages));

INSERT INTO chats_users (chat_id, user_id) VALUES (1, 1);
INSERT INTO chats_users (chat_id, user_id) VALUES (1, 2);
INSERT INTO chats_users (chat_id, user_id) VALUES (2, 1);
INSERT INTO chats_users (chat_id, user_id) VALUES (2, 3);
INSERT INTO chats_users (chat_id, user_id) VALUES (3, 1);
INSERT INTO chats_users (chat_id, user_id) VALUES (3, 4);
INSERT INTO chats_users (chat_id, user_id) VALUES (4, 1);
INSERT INTO chats_users (chat_id, user_id) VALUES (4, 5);
