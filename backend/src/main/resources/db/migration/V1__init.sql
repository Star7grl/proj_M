CREATE TABLE users (
  id                    bigserial,
  username              varchar(30) NOT NULL UNIQUE,
  password              varchar(80) NOT NULL,
  email                 varchar(50) UNIQUE,
  PRIMARY KEY (id)
);

CREATE TABLE roles (
  id                    serial,
  name                  varchar(50) NOT NULL,
  PRIMARY KEY (id)
);

insert into roles (name)
values
('ROLE_USER'),
('ROLE_ADMIN');

insert into users (username, password, email)
values
('user', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'user@gmail.com'),
('admin', '$2a$04$Fx/SX9.BAvtPlMyIIqqFx.hLY2Xp8nnhpzvEEVINvVpwIPbA3v/.i', 'admin@gmail.com');


-- select * from "public".users;