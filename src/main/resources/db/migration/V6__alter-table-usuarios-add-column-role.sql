ALTER TABLE usuarios ADD role varchar(20);
UPDATE usuarios SET role = "ADMIN";