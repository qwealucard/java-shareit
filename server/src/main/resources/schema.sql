DROP TABLE if EXISTS users CASCADE;
DROP TABLE if EXISTS requests CASCADE;
DROP TABLE if EXISTS items CASCADE;
DROP TABLE if EXISTS bookings CASCADE;
DROP TABLE if EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users(
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS requests(
  id SERIAL PRIMARY KEY,
  description varchar(255),
  created TIMESTAMP WITHOUT TIME ZONE,
  requester_id int REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS items(
  id SERIAL PRIMARY KEY,
  name varchar(255) NOT NULL,
  description varchar(255),
  available BOOLEAN NOT NULL,
  owner_id int REFERENCES users(id) NOT NULL,
  request_id int REFERENCES requests(id)
);
CREATE TABLE IF NOT EXISTS bookings(
  id SERIAL PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id int REFERENCES items(id),
  booker_id int REFERENCES users(id),
  status varchar(255)
);
CREATE TABLE IF NOT EXISTS comments(
  id SERIAL PRIMARY KEY,
  text varchar(255),
  item_id int REFERENCES items(id),
  author_id int REFERENCES users(id),
  created TIMESTAMP WITHOUT TIME ZONE
);