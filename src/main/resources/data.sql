merge into mpa (name)
KEY (name) 
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
merge into genres (name)
KEY (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');