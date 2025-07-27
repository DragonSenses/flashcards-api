DROP TABLE IF EXISTS flashcard;
DROP TABLE IF EXISTS study_session;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT name_unique UNIQUE (name)
);

CREATE TABLE study_session (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    category_id VARCHAR(40) NOT NULL,
    name VARCHAR(30) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE flashcard (
    id VARCHAR(40) NOT NULL PRIMARY KEY,
    study_session_id VARCHAR(40) NOT NULL,
    question VARCHAR(200) NOT NULL,
    answer VARCHAR(300) NOT NULL,
    FOREIGN KEY (study_session_id) REFERENCES study_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);