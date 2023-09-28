delete from books_categories where book_id in (SELECT books.id from books where isbn = '978-1234567894');
delete from books where isbn = '978-1234567894';