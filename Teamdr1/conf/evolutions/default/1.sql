# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user_account (
  username                  varchar(255) not null,
  password                  varchar(255),
  constraint pk_user_account primary key (username))
;

create sequence user_account_seq;



<<<<<<< HEAD

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user_account;
=======
create table team (
  name                  varchar(255) not null,
  class_name                  varchar(255),
  username					varchar(225),
  constraint pk_team primary key (username))
;

create sequence team_seq;

create table class (
  name                  varchar(255) not null,
  constraint pk_class primary key (username))
;

create sequence class_seq;



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user_account;
drop table if exists team;
drop table if exists class;
>>>>>>> 70dfc7b3a649b925f94a8d8f439ba922da56b336

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_account_seq;
<<<<<<< HEAD
=======
drop sequence if exists team_seq;
drop sequence if exists class_seq;
>>>>>>> 70dfc7b3a649b925f94a8d8f439ba922da56b336

