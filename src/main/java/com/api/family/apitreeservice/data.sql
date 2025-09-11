INSERT INTO family.family_role VALUES (1, 'ROOT');
INSERT INTO family.family_role VALUES (2, 'ADMIN');
INSERT INTO family.family_role VALUES (3, 'CUSTOMER');
INSERT INTO family.family_role VALUES (4, 'CHILD');
INSERT INTO family.role_users (phone_number, role_name, deleted) 
VALUES ('11111111', 'ROOT', false);
INSERT INTO family.role_users (phone_number, role_name, deleted) 
VALUES ('22222222', 'ROOT', false);