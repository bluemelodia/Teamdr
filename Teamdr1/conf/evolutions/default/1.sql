# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table class_record (
  class_id                  varchar(255) not null,
  class_name                varchar(255),
  constraint pk_class_record primary key (class_id))
;

create table team_record (
  tid                       varchar(255) not null,
  team_members              varchar(255),
  team_name                 varchar(255),
  this_class                varchar(255),
  constraint pk_team_record primary key (tid))
;

create table user_account (
  username                  varchar(255) not null,
  password                  varchar(255),
  constraint pk_user_account primary key (username))
;

create sequence class_record_seq;

create sequence team_record_seq;

create sequence user_account_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists class_record;

drop table if exists team_record;

drop table if exists user_account;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists class_record_seq;

drop sequence if exists team_record_seq;

drop sequence if exists user_account_seq;

