# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table diary_activity (
  id                        bigint not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  type                      varchar(6),
  description               varchar(255),
  emotion                   varchar(7),
  constraint ck_diary_activity_type check (type in ('SCHOOL','MEAL','SPORT','OTHER')),
  constraint ck_diary_activity_emotion check (emotion in ('HAPPY','NEUTRAL','SAD')),
  constraint pk_diary_activity primary key (id))
;

create table diary_item (
  id                        bigint not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  constraint pk_diary_item primary key (id))
;

create table diary_measurement (
  id                        bigint not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  type                      integer,
  value                     float,
  constraint ck_diary_measurement_type check (type in (0,1,2)),
  constraint pk_diary_measurement primary key (id))
;

create table log_action (
  id                        bigint not null,
  timestamp                 timestamp,
  type                      integer,
  user_email                varchar(255),
  constraint ck_log_action_type check (type in (0,1,2,3,4,5,6)),
  constraint pk_log_action primary key (id))
;

create table user (
  email                     varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birthdate                 date,
  password                  varchar(255),
  user_type                 varchar(20),
  last_activity             timestamp,
  constraint ck_user_user_type check (user_type in ('CHILD','PARENT','PROFESSIONAL','ADMIN')),
  constraint pk_user primary key (email))
;

create sequence diary_activity_seq;

create sequence diary_item_seq;

create sequence diary_measurement_seq;

create sequence log_action_seq;

create sequence user_seq;

alter table diary_activity add constraint fk_diary_activity_user_1 foreign key (user_email) references user (email) on delete restrict on update restrict;
create index ix_diary_activity_user_1 on diary_activity (user_email);
alter table diary_item add constraint fk_diary_item_user_2 foreign key (user_email) references user (email) on delete restrict on update restrict;
create index ix_diary_item_user_2 on diary_item (user_email);
alter table diary_measurement add constraint fk_diary_measurement_user_3 foreign key (user_email) references user (email) on delete restrict on update restrict;
create index ix_diary_measurement_user_3 on diary_measurement (user_email);
alter table log_action add constraint fk_log_action_user_4 foreign key (user_email) references user (email) on delete restrict on update restrict;
create index ix_log_action_user_4 on log_action (user_email);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists diary_activity;

drop table if exists diary_item;

drop table if exists diary_measurement;

drop table if exists log_action;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists diary_activity_seq;

drop sequence if exists diary_item_seq;

drop sequence if exists diary_measurement_seq;

drop sequence if exists log_action_seq;

drop sequence if exists user_seq;

