
drop table if exists user;
drop table if exists hibernate_sequence;

create table user (
    id serial primary key,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    role ENUM('USER', 'ADMIN'),
    image_url varchar(255),
    refresh_token varchar(255),
    social_id varchar(255),
    social_type ENUM('GOOGLE', 'NAVER', 'KAKAO')
) engine=InnoDB;



