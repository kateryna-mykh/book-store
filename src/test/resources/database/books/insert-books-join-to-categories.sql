insert into books (id, title, author, isbn, price, description, cover_image) values(1, 'Book 1', 'Author A', '978-1234567890', 5.00, '', 'https://example.com/default-cover-image.jpg');
insert into books (id, title, author, isbn, price, description, cover_image) values(2, 'Book 2', 'Author B', '978-1234567891', 7.30, '', 'https://example.com/default-cover-image.jpg');
insert into books (id, title, author, isbn, price, description, cover_image) values(3, 'Book 3', 'Author C', '978-1234567892', 11.20, '', 'https://example.com/default-cover-image.jpg');
insert into books (id, title, author, isbn, price, description, cover_image, is_deleted) values(4, 'Book 4', 'Author D', '978-1234567893', 3.90, '', 'https://example.com/default-cover-image.jpg', true);

insert into books_categories (book_id, category_id) values (1,1);
insert into books_categories (book_id, category_id) values (1,3);
insert into books_categories (book_id, category_id) values (2,2);
insert into books_categories (book_id, category_id) values (3,1);
insert into books_categories (book_id, category_id) values (4,3);