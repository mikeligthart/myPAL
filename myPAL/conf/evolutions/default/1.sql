# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table log_action (
  id                        bigint not null,
  timestamp                 timestamp,
  type                      integer,
  user_email                varchar(255),
  constraint ck_log_action_type check (type in (0,1,2,3,4)),
  constraint pk_log_action primary key (id))
;

create table user (
  email                     varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birth_date                date,
  password                  varchar(255),
  user_type                 varchar(20),
  last_activity             timestamp,
  constraint ck_user_user_type check (user_type in ('CHILD','PARENT','PROFESSIONAL','ADMIN')),
  constraint pk_user primary key (email))
;

create sequence log_action_seq;

create sequence user_seq;

alter table log_action add constraint fk_log_action_user_1 foreign key (user_email) references user (email) on delete restrict on update restrict;
create index ix_log_action_user_1 on log_action (user_email);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists log_action;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists log_action_seq;

drop sequence if exists user_seq;

