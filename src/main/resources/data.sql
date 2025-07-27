-- Category Definitions
INSERT INTO category(id, name)
    VALUES('1', 'Art History');

INSERT INTO category(id, name)
    VALUES('2', 'Thermodynamics');

INSERT INTO category(id, name)
    VALUES('3', 'Computer Science');

INSERT INTO category(id, name)
    VALUES('4', 'American History');


-- Study Sessions (IDs 1–5)
INSERT INTO study_session(id, category_id, name)
    VALUES('1', '1', 'Northern Renaissance');

INSERT INTO study_session(id, category_id, name)
    VALUES('2', '1', 'Renaissance');

INSERT INTO study_session(id, category_id, name)
    VALUES('3', '2', 'Second Law of Thermodynamics');

INSERT INTO study_session(id, category_id, name)
    VALUES('4', '3', 'Object Oriented Programming (OOP)');

INSERT INTO study_session(id, category_id, name)
    VALUES('5', '4', 'Presidents');


-- Flashcards (IDs 1–7)
INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('1', '1', 'Who painted "The Garden of Earthly Delights"?', 'Hieronymus Bosch');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('2', '2', 'Who painted "The Last Supper"?', 'Leonardo da Vinci');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('3', '2', 'Who sculpted "David"?', 'Michelangelo');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('4', '3', 'What is a measure of disorder or randomness in a system?', 'Entropy');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('5', '4', 'What are three Object-Oriented Design Goals?', 'Adaptability, Reusability, Robustness');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('6', '4', 'What are three Object-Oriented Design Principles?', 'Abstraction, Encapsulation, Modularity');

INSERT INTO flashcard(id, study_session_id, question, answer)
    VALUES('7', '5', 'Who issued the Emancipation Proclamation?', 'Abraham Lincoln');