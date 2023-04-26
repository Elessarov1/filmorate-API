DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS  PUBLIC.MPA (
	MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	NAME CHARACTER VARYING(50) NOT NULL,
	CONSTRAINT MPA_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	NAME CHARACTER VARYING(50) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	DURATION INTEGER NOT NULL,
	RELEASE_DATE DATE NOT NULL CHECK (RELEASE_DATE >= '1895-12-28'),
	MPA_ID INTEGER,
    DIRECTOR_ID  INTEGER,
	CONSTRAINT FILM_PK PRIMARY KEY (ID),
	CONSTRAINT FILM_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID)
	    ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT FILM_DIRECTOR_DIRECTOR_ID_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES PUBLIC.DIRECTOR(DIRECTOR_ID)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
	GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY,
	GENRE_NAME CHARACTER VARYING(50) NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY(FILM_ID) REFERENCES PUBLIC.FILM(ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
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
	CONSTRAINT LIKES_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
	CONSTRAINT LIKES_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);


