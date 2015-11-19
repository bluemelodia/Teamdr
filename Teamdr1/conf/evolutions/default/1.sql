# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table class_record (
  class_id                  varchar(255) not null,
  class_name                varchar(255),
  constraint pk_class_record primary key (class_id))
;

create table notification (
  notif_id                  integer not null,
  username                  varchar(255),
  message_type              integer,
  class_id                  varchar(255),
  message                   varchar(255),
  team_id                   varchar(255),
  constraint pk_notification primary key (notif_id))
;

create table team_record (
  tid                       varchar(255) not null,
  team_members              varchar(255),
  team_name                 varchar(255),
  this_class                varchar(255),
  seen_teams                varchar(255),
  constraint pk_team_record primary key (tid))
;

create table user_account (
  username                  varchar(255) not null,
  password                  varchar(255),
  profile_username          varchar(255),
  current_class             varchar(255),
  all_classes               varchar(255),
  constraint uq_user_account_profile_username unique (profile_username),
  constraint pk_user_account primary key (username))
;

create table user_profile (
  username                  varchar(255) not null,
  email                     varchar(255),
  pic_url                   varchar(255),
  description               varchar(255),
  constraint pk_user_profile primary key (username))
;

create sequence class_record_seq;

create sequence notification_seq;

create sequence team_record_seq;

create sequence user_account_seq;

create sequence user_profile_seq;

alter table user_account add constraint fk_user_account_profile_1 foreign key (profile_username) references user_profile (username) on delete restrict on update restrict;
create index ix_user_account_profile_1 on user_account (profile_username);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists class_record;

drop table if exists notification;

drop table if exists team_record;

drop table if exists user_account;

drop table if exists user_profile;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists class_record_seq;

drop sequence if exists notification_seq;

drop sequence if exists team_record_seq;

drop sequence if exists user_account_seq;

drop sequence if exists user_profile_seq;

