CREATE TABLE IF NOT EXISTS base_price (
    pass_id SERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    cost INT NOT NULL,
    UNIQUE (type)
);
INSERT INTO base_price (type, cost) VALUES ('1jour', 35);
INSERT INTO base_price (type, cost) VALUES ('night', 19);

CREATE TABLE IF NOT EXISTS holidays (
    holiday DATE NOT NULL PRIMARY KEY,
    description VARCHAR(255) NOT NULL
);
INSERT INTO holidays (holiday, description) VALUES ('2019-02-18', 'winter');
INSERT INTO holidays (holiday, description) VALUES ('2019-02-25', 'winter');
INSERT INTO holidays (holiday, description) VALUES ('2019-03-04', 'winter');
