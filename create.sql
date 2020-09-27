CREATE DATABASE jadlet;
\c jadlet;
CREATE TABLE restaurants(id SERIAL PRIMARY KEY, name VARCHAR, address VARCHAR, zipcode VARCHAR, phone VARCHAR, website VARCHAR, email VARCHAR);
CREATE TABLE foodtypes(id SERIAL PRIMARY KEY, name VARCHAR);
CREATE TABLE reviews(id SERIAL PRIMARY KEY, writtenby VARCHAR, rating VARCHAR, content VARCHAR, restaurantid INTEGER, createdat BIGINT);
CREATE TABLE restaurants_foodtypes(id SERIAL PRIMARY KEY, foodtypeid INTEGER, restaurantid INTEGER);
CREATE DATABASE jadlet_test WITH TEMPLATE jadlet;