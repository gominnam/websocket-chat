drop table if exists user;
drop table if exists hibernate_sequence;

create table user (
    id bigint auto_increment primary key,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    created_at timestamp not null default current_timestamp(),
    updated_at timestamp not null default current_timestamp(),
    role varchar(255) check (role in ('ROLE_USER', 'ROLE_ADMIN')),
    image_url varchar(255),
    refresh_token varchar(255),
    social_id varchar(255),
    social_type varchar(255) check (social_type in ('GOOGLE', 'NAVER', 'KAKAO'))
);
