# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table carbo_hydrate (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  value                     double,
  daypart                   integer,
  comment                   varchar(255),
  constraint ck_carbo_hydrate_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_carbo_hydrate primary key (id))
;

create table diary_activity (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  type_id                   integer,
  description               varchar(1200),
  picture_id                integer,
  emotion                   varchar(7),
  constraint ck_diary_activity_emotion check (emotion in ('HAPPY','NEUTRAL','SAD')),
  constraint pk_diary_activity primary key (id))
;

create table diary_activity_type (
  id                        integer not null,
  name                      varchar(255),
  icon_location             varchar(255),
  color                     varchar(255),
  user_email                varchar(255),
  constraint pk_diary_activity_type primary key (id))
;

create table diary_item (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  constraint pk_diary_item primary key (id))
;

create table diary_measurement (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  value                     double,
  daypart                   integer,
  constraint ck_diary_measurement_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_diary_measurement primary key (id))
;

create table glucose (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  value                     double,
  daypart                   integer,
  comment                   varchar(255),
  constraint ck_glucose_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_glucose primary key (id))
;

create table insulin (
  id                        integer not null,
  date                      date,
  starttime                 time,
  endtime                   time,
  user_email                varchar(255),
  value                     double,
  daypart                   integer,
  comment                   varchar(255),
  constraint ck_insulin_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_insulin primary key (id))
;

create table log_action (
  id                        bigint not null,
  timestamp                 timestamp,
  type                      integer,
  user_email                varchar(255),
  constraint ck_log_action_type check (type in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35)),
  constraint pk_log_action primary key (id))
;

create table picture (
  id                        integer not null,
  name                      varchar(255),
  thumbnail                 varchar(255),
  diary_activity_id         integer,
  user_email                varchar(255),
  date                      date,
  constraint pk_picture primary key (id))
;

create table user_my_pal (
  email                     varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birthdate                 date,
  password                  varchar(255),
  user_type                 varchar(20),
  last_activity             timestamp,
  constraint ck_user_my_pal_user_type check (user_type in ('CHILD','PARENT','PROFESSIONAL','ADMIN')),
  constraint pk_user_my_pal primary key (email))
;

create sequence carbo_hydrate_seq;

create sequence diary_activity_seq;

create sequence diary_activity_type_seq;

create sequence diary_item_seq;

create sequence diary_measurement_seq;

create sequence glucose_seq;

create sequence insulin_seq;

create sequence log_action_seq;

create sequence picture_seq;

create sequence user_my_pal_seq;

alter table carbo_hydrate add constraint fk_carbo_hydrate_user_1 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_carbo_hydrate_user_1 on carbo_hydrate (user_email);
alter table diary_activity add constraint fk_diary_activity_user_2 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_diary_activity_user_2 on diary_activity (user_email);
alter table diary_activity add constraint fk_diary_activity_type_3 foreign key (type_id) references diary_activity_type (id) on delete restrict on update restrict;
create index ix_diary_activity_type_3 on diary_activity (type_id);
alter table diary_activity add constraint fk_diary_activity_picture_4 foreign key (picture_id) references picture (id) on delete restrict on update restrict;
create index ix_diary_activity_picture_4 on diary_activity (picture_id);
alter table diary_activity_type add constraint fk_diary_activity_type_user_5 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_diary_activity_type_user_5 on diary_activity_type (user_email);
alter table diary_item add constraint fk_diary_item_user_6 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_diary_item_user_6 on diary_item (user_email);
alter table diary_measurement add constraint fk_diary_measurement_user_7 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_diary_measurement_user_7 on diary_measurement (user_email);
alter table glucose add constraint fk_glucose_user_8 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_glucose_user_8 on glucose (user_email);
alter table insulin add constraint fk_insulin_user_9 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_insulin_user_9 on insulin (user_email);
alter table log_action add constraint fk_log_action_user_10 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_log_action_user_10 on log_action (user_email);
alter table picture add constraint fk_picture_diaryActivity_11 foreign key (diary_activity_id) references diary_activity (id) on delete restrict on update restrict;
create index ix_picture_diaryActivity_11 on picture (diary_activity_id);
alter table picture add constraint fk_picture_user_12 foreign key (user_email) references user_my_pal (email) on delete restrict on update restrict;
create index ix_picture_user_12 on picture (user_email);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists carbo_hydrate;

drop table if exists diary_activity;

drop table if exists diary_activity_type;

drop table if exists diary_item;

drop table if exists diary_measurement;

drop table if exists glucose;

drop table if exists insulin;

drop table if exists log_action;

drop table if exists picture;

drop table if exists user_my_pal;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists carbo_hydrate_seq;

drop sequence if exists diary_activity_seq;

drop sequence if exists diary_activity_type_seq;

drop sequence if exists diary_item_seq;

drop sequence if exists diary_measurement_seq;

drop sequence if exists glucose_seq;

drop sequence if exists insulin_seq;

drop sequence if exists log_action_seq;

drop sequence if exists picture_seq;

drop sequence if exists user_my_pal_seq;

