# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user_account (
  username                  varchar(255) not null,
  password                  varchar(255),
  constraint pk_user_account primary key (username))
;

create sequence user_account_seq;

create table teams (
  username                  varchar(255) not null,
  password                  varchar(255),
  constraint pk_teams primary key (username))
;

create sequence teams_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user_account;
drop table if exists teams;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_account_seq;
drop sequence if exists teams_seq;

