DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS  PUBLIC.MPA (
	MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	NAME CHARACTER VARYING(50) NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.DIRECTOR
(
    DIRECTOR_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY,
    DIRECTOR_NAME CHARACTER VARYING(24),
    CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	NAME CHARACTER VARYING(50) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	DURATION INTEGER NOT NULL,
	RELEASE_DATE DATE NOT NULL CHECK (RELEASE_DATE >= '1895-12-28'),
	MPA_ID INTEGER,
	CONSTRAINT FILM_PK PRIMARY KEY (ID),
	CONSTRAINT FILM_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR
(
    DIRECTOR_ID INTEGER NOT NULL,
    FILM_ID     INTEGER NOT NULL,
    CONSTRAINT FILM_DIRECTOR_PK
        PRIMARY KEY (DIRECTOR_ID, FILM_ID),
    CONSTRAINT FILM_DIRECTOR_DIRECTOR_DIRECTOR_ID_FK
        FOREIGN KEY (DIRECTOR_ID) REFERENCES DIRECTOR,
    CONSTRAINT FILM_DIRECTOR_FILM_ID_FK
        FOREIGN KEY (FILM_ID) REFERENCES FILM
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
	GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	GENRE_NAME CHARACTER VARYING(50) NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY(FILM_ID) REFERENCES PUBLIC.FILM(ID) ON DELETE CASCADE ON UPDATE RESTRICT,
	CONSTRAINT FILM_GENRE_FK_2 FOREIGN KEY(GENRE_ID) REFERENCES PUBLIC.GENRE(GENRE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	LOGIN VARCHAR(50) NOT NULL,
	NAME VARCHAR(50),
	EMAIL VARCHAR(50) NOT NULL,
	BIRTHDAY DATE NOT NULL,
	CONSTRAINT USER_PK PRIMARY KEY (ID),
	CONSTRAINT LOGIN_UNIQUE UNIQUE (LOGIN)
);

CREATE TABLE PUBLIC.FRIENDS (
	USER_ID INTEGER NOT NULL,
	FRIEND_ID INTEGER NOT NULL,
	FRIENDSHIP_STATUS BOOLEAN DEFAULT FALSE,
	CONSTRAINT FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT FRIENDS_FK_1 FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE PUBLIC.LIKES (
	FILM_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	CONSTRAINT LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(ID) ON DELETE CASCADE ON UPDATE RESTRICT,
	CONSTRAINT LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE PUBLIC.REVIEWS (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	DESCRIPTION VARCHAR(200),
	IS_POSITIVE BOOLEAN,
	USER_ID INTEGER,
	FILM_ID INTEGER,
	CONSTRAINT REVIEWS_PK PRIMARY KEY (ID),
	CONSTRAINT REVIEWS_FILM_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(ID) ON DELETE CASCADE,
	CONSTRAINT REVIEWS_USER_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE PUBLIC.REVIEW_LIKES (
    REVIEW_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    RATING INTEGER CHECK (RATING IN (1, -1)),
    PRIMARY KEY (REVIEW_ID, USER_ID),
    CONSTRAINT REVIEW_LIKES_ID_FK FOREIGN KEY (REVIEW_ID) REFERENCES PUBLIC.REVIEWS(ID) ON DELETE CASCADE,
    CONSTRAINT REVIEW_LIKES_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.EVENTS (
	EVENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	DATE_TIME TIMESTAMP NOT NULL,
	USER_ID INTEGER NOT NULL,
	EVENT_TYPE VARCHAR(50) NOT NULL,
	OPERATION VARCHAR(50) NOT NULL,
	ENTITY_ID INTEGER NOT NULL
);


