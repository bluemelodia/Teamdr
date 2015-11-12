# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user_account (
  username                  varchar(255) not null,
  password                  varchar(255),
  constraint pk_user_account primary key (username))
;

create sequence user_account_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user_account;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists user_account_seq;
