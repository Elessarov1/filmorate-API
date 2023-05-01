# Java-filmorate

Template repository for Filmorate project.

## Swagger UI

For more details you can go [here](localhost:8080/swagger-ui.html) when the app is started
and explore API from your browser

### It looks like this

![](swagger.png)

## Database diagram

![](schema.png)

## What can we get from database:

### User:

+ Get all users
+ Get user by ID
+ Get users friends
+ Get common friends
+ Check friendship status

### Film:

+ Get all films
+ Get film by ID
+ Get film genre
+ Get most popular films
+ Get film likes

## Examples

Get user friends

```postgres-psql
SELECT * FROM USERS 
WHERE ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)
 ```

Get genres by film id

```postgres-psql
SELECT * FROM GENRE 
WHERE GENRE_ID IN 
(SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)
 ```

Get films of concrete genre

```postgres-psql
SELECT * FROM FILMS f
JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID
WHERE fg.GENRE_ID = ?
 ```