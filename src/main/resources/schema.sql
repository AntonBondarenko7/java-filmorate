-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/rff9Cp
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.

-- Modify this code to update the DB schema diagram.
-- To reset the sample schema, replace everything with
-- two dots ('..' - without quotes).

CREATE TABLE IF NOT EXISTS "users" (
    "id" int   NOT NULL,
    "email" varchar(255)   NOT NULL,
    "login" varchar(255)   NOT NULL,
    "name" varchar(255)   NOT NULL,
    "birthday" date   NOT NULL,
    CONSTRAINT "pk_users" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "friendships" (
    "id" int GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
    "user1_id" int   NOT NULL,
    "user2_id" int   NOT NULL,
    "is_approved" boolean   NOT NULL,
    CONSTRAINT "pk_friendships" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "films" (
    "id" int   NOT NULL,
    "name" varchar(255)   NOT NULL,
    "description" varchar(1000)   NOT NULL,
    "release_date" date   NOT NULL,
    "duration" int   NOT NULL,
    "mpa_rating_id" int   NOT NULL,
    CONSTRAINT "pk_films" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "likes" (
    "id" int GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
    "film_id" int   NOT NULL,
    "user_id" int   NOT NULL,
    CONSTRAINT "pk_likes" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "genres" (
    "id" int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
    "name" varchar(255)   NOT NULL,
    CONSTRAINT "pk_genres" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "film_genres" (
    "id" int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
    "film_id" int   NOT NULL,
    "genre_id" int   NOT NULL,
    CONSTRAINT "pk_film_genres" PRIMARY KEY (
        "id"
     )
);

CREATE TABLE IF NOT EXISTS "mpa_ratings" (
    "id" int GENERATED BY DEFAULT AS IDENTITY  NOT NULL,
    "name" varchar(255)   NOT NULL,
    "description" varchar(255)   NOT NULL,
    CONSTRAINT "pk_mpa_ratings" PRIMARY KEY (
        "id"
     )
);

ALTER TABLE "friendships" ADD CONSTRAINT IF NOT EXISTS "fk_friendships_user1_id" FOREIGN KEY("user1_id")
REFERENCES "users" ("id");

ALTER TABLE "friendships" ADD CONSTRAINT IF NOT EXISTS "fk_friendships_user2_id" FOREIGN KEY("user2_id")
REFERENCES "users" ("id");

ALTER TABLE "films" ADD CONSTRAINT IF NOT EXISTS "fk_film_mpa_rating_id" FOREIGN KEY("mpa_rating_id")
REFERENCES "mpa_ratings" ("id");

ALTER TABLE "likes" ADD CONSTRAINT IF NOT EXISTS "fk_like_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "likes" ADD CONSTRAINT IF NOT EXISTS "fk_like_user_id" FOREIGN KEY("user_id")
REFERENCES "users" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT IF NOT EXISTS "fk_film_genres_film_id" FOREIGN KEY("film_id")
REFERENCES "films" ("id");

ALTER TABLE "film_genres" ADD CONSTRAINT IF NOT EXISTS "fk_film_genres_genre_id" FOREIGN KEY("genre_id")
REFERENCES "genres" ("id");


