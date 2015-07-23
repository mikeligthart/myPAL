# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table diary_activity (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  type                      varchar(6),
  description               varchar(255),
  picture                   varchar(255),
  emotion_id                integer,
  constraint ck_diary_activity_type check (type in ('SCHOOL','MEAL','SPORT','OTHER')),
  constraint pk_diary_activity primary key (id))
;

create table diary_item (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  constraint pk_diary_item primary key (id))
;

create table emotion (
  id                        integer not null,
  pleased                   float,
  aroused                   float,
  dominant                  float,
  constraint pk_emotion primary key (id))
;

create table user (
  email                     varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  user_type                 varchar(255),
  constraint pk_user primary key (email))
;

create sequence diary_activity_seq;

create sequence diary_item_seq;

create sequence emotion_seq;

create sequence user_seq;

alter table diary_activity add constraint fk_diary_activity_emotion_1 foreign key (emotion_id) references emotion (id) on delete restrict on update restrict;
create index ix_diary_activity_emotion_1 on diary_activity (emotion_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists diary_activity;

drop table if exists diary_item;

drop table if exists emotion;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists diary_activity_seq;

drop sequence if exists diary_item_seq;

drop sequence if exists emotion_seq;

drop sequence if exists user_seq;

