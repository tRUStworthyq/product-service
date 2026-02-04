CREATE TABLE product (
                         id BIGINT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL UNIQUE,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL,
                         category VARCHAR(50),
                         amount INTEGER NOT NULL CHECK (amount >= 0)
);

CREATE SEQUENCE product_seq
    START WITH 50
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;