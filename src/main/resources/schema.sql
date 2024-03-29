-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/rff9Cp
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.

-- Modify this code to update the DB schema diagram.
-- To reset the sample schema, replace everything with
-- two dots ('..' - without quotes).

DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users (
                                     id int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     email varchar(255)   NOT NULL,
                                     login varchar(255)   NOT NULL,
                                     name varchar(255)   NOT NULL,
                                     birthday date   NOT NULL,
                                     CONSTRAINT pk_users PRIMARY KEY (
                                                                      id
                                         )
);

CREATE UNIQUE INDEX idx_users_email ON users(email);

CREATE UNIQUE INDEX idx_users_login ON users(login);

CREATE TABLE IF NOT EXISTS friendships (
                                           id int GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
                                           user1_id int   NOT NULL,
                                           user2_id int   NOT NULL,
                                           is_approved boolean   NOT NULL,
                                           CONSTRAINT pk_friendships PRIMARY KEY (
                                                                                  id
                                               )
);

CREATE TABLE IF NOT EXISTS films (
                                     id int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name varchar(255)   NOT NULL,
                                     description varchar(1000)   NOT NULL,
                                     release_date date   NOT NULL,
                                     duration int   NOT NULL,
                                     mpa_rating_id int   NOT NULL,
                                     CONSTRAINT pk_films PRIMARY KEY (
                                                                      id
                                         )
);

CREATE TABLE IF NOT EXISTS likes (
                                     id int GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
                                     film_id int   NOT NULL,
                                     user_id int   NOT NULL,
                                     CONSTRAINT pk_likes PRIMARY KEY (
                                                                      id
                                         )
);

CREATE TABLE IF NOT EXISTS genres (
                                      id int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
                                      name varchar(255)   NOT NULL,
                                      CONSTRAINT pk_genres PRIMARY KEY (
                                                                        id
                                          )
);

CREATE TABLE IF NOT EXISTS film_genres (
                                           id int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
                                           film_id int   NOT NULL,
                                           genre_id int   NOT NULL,
                                           CONSTRAINT pk_film_genres PRIMARY KEY (
                                                                                  id
                                               )
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
                                           id int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
                                           name varchar(255)   NOT NULL,
                                           description varchar(255)   NOT NULL,
                                           CONSTRAINT pk_mpa_ratings PRIMARY KEY (
                                                                                  id
                                               )
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50) NOT NULL
);
CREATE TABLE IF NOT EXISTS directors_films
(
    film_id INTEGER NOT NULL REFERENCES films (id) ON DELETE CASCADE,
    director_id INTEGER NOT NULL REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content varchar(500)   NOT NULL,
    is_positive boolean,
    user_id int NOT NULL,
    film_id int NOT NULL,
    CONSTRAINT reviews_fk1 FOREIGN KEY (user_id) REFERENCES PUBLIC.users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT reviews_fk2 FOREIGN KEY (film_id) REFERENCES PUBLIC.films (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    review_id int NOT NULL,
    user_id int NOT NULL,
    CONSTRAINT reviews_likes_pk PRIMARY KEY (review_id, user_id),
    CONSTRAINT reviews_likes_fk1 FOREIGN KEY (user_id) REFERENCES PUBLIC.users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT reviews_likes_fk2 FOREIGN KEY (review_id)
    REFERENCES PUBLIC.reviews (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_dislikes (
    review_id int NOT NULL,
    user_id int NOT NULL,
    CONSTRAINT reviews_dislikes_pk PRIMARY KEY (review_id, user_id),
    CONSTRAINT reviews_dislikes_fk1 FOREIGN KEY (user_id)
    REFERENCES PUBLIC.users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT reviews_dislikes_fk2 FOREIGN KEY (review_id)
    REFERENCES PUBLIC.reviews (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS event
(
  event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  timestamp LONG,
  user_id INTEGER NOT NULL,
  event_type varchar,
  operation varchar,
  entity_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO mpa_ratings (name, description)
VALUES ('G', 'У фильма нет возрастных ограничений'),
       ('PG', 'Детям рекомендуется смотреть фильм с родителями'),
       ('PG-13', 'Детям до 13 лет просмотр не желателен'),
       ('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
       ('NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO genres (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

ALTER TABLE friendships ADD CONSTRAINT IF NOT EXISTS fk_friendships_user1_id FOREIGN KEY(user1_id)
    REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friendships ADD CONSTRAINT IF NOT EXISTS fk_friendships_user2_id FOREIGN KEY(user2_id)
    REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE films ADD CONSTRAINT IF NOT EXISTS fk_film_mpa_rating_id FOREIGN KEY(mpa_rating_id)
    REFERENCES mpa_ratings (id);

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_like_film_id FOREIGN KEY(film_id)
    REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_like_user_id FOREIGN KEY(user_id)
    REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE film_genres ADD CONSTRAINT IF NOT EXISTS fk_film_genres_film_id FOREIGN KEY(film_id)
    REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE film_genres ADD CONSTRAINT IF NOT EXISTS fk_film_genres_genre_id FOREIGN KEY(genre_id)
    REFERENCES genres (id);