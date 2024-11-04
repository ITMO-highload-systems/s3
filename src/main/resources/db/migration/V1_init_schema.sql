CREATE TABLE image_record
(
    id           SERIAL PRIMARY KEY,
    image_hash   VARCHAR(255) NOT NULL,
    paragraph_id INT
);