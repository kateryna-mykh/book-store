insert into users (id, email, password, first_name, last_name) values(1, 'user@i.ua', '$2a$10$EI0DTlSemzjxkXu2n11AmuCZ4f0xKuvz9UgNYj/5lQNuPTkCED4sG', 'Sim', 'Pleuser');
insert into users (id, email, password, first_name, last_name, shipping_address) values(2, 'test_admin@i.ua', '$2a$10$pwqQmfX4FG5IcfOh7amiTu2ko5P86qiQxRY3zqUk2tziRRne/Ja0G', 'Ad', 'Min', 'Street, Town, Country');
insert into users_roles (user_id, role_id) values(1, 2);
insert into users_roles (user_id, role_id) values(1, 1);
insert into users_roles (user_id, role_id) values(2, 1);
