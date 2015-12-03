# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table avatar_behavior (
  id                        integer not null,
  gesture_id                integer,
  avatar_html_type          varchar(16),
  last_modified             bigint,
  constraint ck_avatar_behavior_avatar_html_type check (avatar_html_type in ('NULL','YES_NO','YES_NO_DONTKNOW','TEXT','TEXTFIELD','PICTURE1','PICTURE2','PICTURE3','PICTURE4','PICTURE5','PICTURE6','PICTURE7','PICTURE8','PICTURE9','PICTURE10','PICTURE11','PICTURE12','PICTURE13','PICTURE14','PICTURE15','PICTURE16','PICTURE17','PICTURE18','PICTURE19','PICTURE20','PICTURE21','PICTURE22','PICTURE23','PICTURE24','PICTURE25','PICTURE26','PICTURE27','PICTURE28','PICTURE29','PICTURE30','PICTURE31','PICTURE32','PICTURE33','PICTURE34','PICTURE35','PICTURE36','TOGETHER_OR_SELF')),
  constraint pk_avatar_behavior primary key (id))
;

create table avatar_gesture (
  id                        integer not null,
  file_name                 varchar(255),
  is_video                  boolean,
  duration                  integer,
  constraint pk_avatar_gesture primary key (id))
;

create table avatar_line (
  id                        integer not null,
  behavior_id               integer,
  line                      varchar(255),
  version                   integer,
  speech_source             varchar(255),
  speech_file_source        varchar(255),
  is_complete               boolean,
  constraint pk_avatar_line primary key (id))
;

create table diary_activity (
  id                        integer not null,
  date                      date,
  added                     timestamp,
  starttime                 time,
  endtime                   time,
  user_user_name            varchar(255),
  user_name                 varchar(255),
  type_id                   integer,
  name                      varchar(255),
  description               varchar(1200),
  picture_id                integer,
  emotion                   varchar(7),
  carbohydrate_value        double,
  constraint ck_diary_activity_emotion check (emotion in ('HAPPY','NEUTRAL','SAD')),
  constraint pk_diary_activity primary key (id))
;

create table diary_activity_type (
  id                        integer not null,
  name                      varchar(255),
  icon_location             varchar(255),
  color                     varchar(255),
  user_user_name            varchar(255),
  constraint pk_diary_activity_type primary key (id))
;

create table diary_measurement (
  id                        integer not null,
  date                      date,
  added                     timestamp,
  starttime                 time,
  endtime                   time,
  user_user_name            varchar(255),
  user_name                 varchar(255),
  value                     double,
  daypart                   integer,
  constraint ck_diary_measurement_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_diary_measurement primary key (id))
;

create table glucose (
  id                        integer not null,
  date                      date,
  added                     timestamp,
  starttime                 time,
  endtime                   time,
  user_user_name            varchar(255),
  user_name                 varchar(255),
  value                     double,
  daypart                   integer,
  comment                   varchar(255),
  constraint ck_glucose_daypart check (daypart in (0,1,2,3,4,5)),
  constraint pk_glucose primary key (id))
;

create table goal (
  id                        integer not null,
  target                    varchar(16),
  target_value              integer,
  added                     timestamp,
  met_at_timestamp          timestamp,
  start_date                timestamp,
  deadline                  timestamp,
  met_at                    timestamp,
  met                       boolean,
  goal_type                 varchar(5),
  user_user_name            varchar(255),
  constraint ck_goal_target check (target in ('ADDxACTIVITIES','ADDxMEASUREMENTS','ADDxPICTURES','ADDxYESTERDAY','CONACTIVITIES','CONMEASUREMENTS','CONPICTURES','CONLOGINS')),
  constraint ck_goal_goal_type check (goal_type in ('DAILY','TOTAL')),
  constraint pk_goal primary key (id))
;

create table insulin (
  id                        integer not null,
  date                      date,
  added                     timestamp,
  starttime                 time,
  endtime                   time,
  user_user_name            varchar(255),
  user_name                 varchar(255),
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
  user_user_name            varchar(255),
  constraint ck_log_action_type check (type in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53)),
  constraint pk_log_action primary key (id))
;

create table log_avatar (
  id                        integer not null,
  timestamp                 timestamp,
  type                      integer,
  user_user_name            varchar(255),
  constraint ck_log_avatar_type check (type in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)),
  constraint pk_log_avatar primary key (id))
;

create table picture (
  id                        integer not null,
  name                      varchar(255),
  thumbnail                 varchar(255),
  diary_activity_id         integer,
  added                     timestamp,
  user_user_name            varchar(255),
  date                      date,
  constraint pk_picture primary key (id))
;

create table user_my_pal (
  user_name                 varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birthdate                 date,
  password                  varchar(255),
  user_type                 varchar(32),
  last_activity             timestamp,
  gluconline_id             varchar(255),
  constraint ck_user_my_pal_user_type check (user_type in ('CHILD','PARENT','PROFESSIONAL','ADMIN')),
  constraint pk_user_my_pal primary key (user_name))
;

create sequence avatar_behavior_seq;

create sequence avatar_gesture_seq;

create sequence avatar_line_seq;

create sequence diary_activity_seq;

create sequence diary_activity_type_seq;

create sequence diary_measurement_seq;

create sequence glucose_seq;

create sequence goal_seq;

create sequence insulin_seq;

create sequence log_action_seq;

create sequence log_avatar_seq;

create sequence picture_seq;

create sequence user_my_pal_seq;

alter table avatar_line add constraint fk_avatar_line_behavior_1 foreign key (behavior_id) references avatar_behavior (id) on delete restrict on update restrict;
create index ix_avatar_line_behavior_1 on avatar_line (behavior_id);
alter table diary_activity add constraint fk_diary_activity_user_2 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_diary_activity_user_2 on diary_activity (user_user_name);
alter table diary_activity add constraint fk_diary_activity_type_3 foreign key (type_id) references diary_activity_type (id) on delete restrict on update restrict;
create index ix_diary_activity_type_3 on diary_activity (type_id);
alter table diary_activity add constraint fk_diary_activity_picture_4 foreign key (picture_id) references picture (id) on delete restrict on update restrict;
create index ix_diary_activity_picture_4 on diary_activity (picture_id);
alter table diary_activity_type add constraint fk_diary_activity_type_user_5 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_diary_activity_type_user_5 on diary_activity_type (user_user_name);
alter table diary_measurement add constraint fk_diary_measurement_user_6 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_diary_measurement_user_6 on diary_measurement (user_user_name);
alter table glucose add constraint fk_glucose_user_7 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_glucose_user_7 on glucose (user_user_name);
alter table goal add constraint fk_goal_user_8 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_goal_user_8 on goal (user_user_name);
alter table insulin add constraint fk_insulin_user_9 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_insulin_user_9 on insulin (user_user_name);
alter table log_action add constraint fk_log_action_user_10 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_log_action_user_10 on log_action (user_user_name);
alter table log_avatar add constraint fk_log_avatar_user_11 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_log_avatar_user_11 on log_avatar (user_user_name);
alter table picture add constraint fk_picture_diaryActivity_12 foreign key (diary_activity_id) references diary_activity (id) on delete restrict on update restrict;
create index ix_picture_diaryActivity_12 on picture (diary_activity_id);
alter table picture add constraint fk_picture_user_13 foreign key (user_user_name) references user_my_pal (user_name) on delete restrict on update restrict;
create index ix_picture_user_13 on picture (user_user_name);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists avatar_behavior;

drop table if exists avatar_gesture;

drop table if exists avatar_line;

drop table if exists diary_activity;

drop table if exists diary_activity_type;

drop table if exists diary_measurement;

drop table if exists glucose;

drop table if exists goal;

drop table if exists insulin;

drop table if exists log_action;

drop table if exists log_avatar;

drop table if exists picture;

drop table if exists user_my_pal;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists avatar_behavior_seq;

drop sequence if exists avatar_gesture_seq;

drop sequence if exists avatar_line_seq;

drop sequence if exists diary_activity_seq;

drop sequence if exists diary_activity_type_seq;

drop sequence if exists diary_measurement_seq;

drop sequence if exists glucose_seq;

drop sequence if exists goal_seq;

drop sequence if exists insulin_seq;

drop sequence if exists log_action_seq;

drop sequence if exists log_avatar_seq;

drop sequence if exists picture_seq;

drop sequence if exists user_my_pal_seq;

