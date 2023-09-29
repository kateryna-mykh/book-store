UPDATE books SET title = 'Book 1', author = 'Author A', isbn = '978-1234567890', price = 5.00 WHERE id = 1;
UPDATE books SET is_deleted = false WHERE id = 1;
DELETE from books_categories where book_id = 1;
insert into books_categories (book_id, category_id) values (1,1);
insert into books_categories (book_id, category_id) values (1,3);