# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                            bigint auto_increment not null,
  user_id                       integer,
  is_default                    boolean default false not null,
  constraint pk_admin primary key (id)
);

create table destination (
  destid                        integer auto_increment not null,
  dest_name                     varchar(255),
  dest_type                     varchar(255),
  district                      varchar(255),
  country                       varchar(255),
  latitude                      double not null,
  longitude                     double not null,
  is_public                     boolean default false not null,
  primary_photo_photo_id        integer,
  user                          integer,
  constraint pk_destination primary key (destid)
);

create table destination_traveller_type (
  destination_destid            integer not null,
  traveller_type_ttypeid        integer not null,
  constraint pk_destination_traveller_type primary key (destination_destid,traveller_type_ttypeid)
);

create table destination_modification_request (
  id                            integer auto_increment not null,
  old_destination_destid        integer,
  new_dest_name                 varchar(255),
  new_dest_type                 varchar(255),
  new_dest_country              varchar(255),
  new_dest_district             varchar(255),
  new_dest_latitude             double not null,
  new_dest_longitude            double not null,
  request_author_userid         integer,
  creation_date                 timestamp not null,
  constraint pk_destination_modification_request primary key (id)
);

create table destination_modification_request_traveller_type (
  destination_modification_request_id integer not null,
  traveller_type_ttypeid        integer not null,
  constraint pk_destination_modification_request_traveller_type primary key (destination_modification_request_id,traveller_type_ttypeid)
);

create table nationality (
  natid                         integer auto_increment not null,
  nationality_name              varchar(255),
  constraint pk_nationality primary key (natid)
);

create table passport (
  passid                        integer auto_increment not null,
  passport_name                 varchar(255),
  constraint pk_passport primary key (passid)
);

create table traveller_type (
  ttypeid                       integer auto_increment not null,
  traveller_type_name           varchar(255),
  constraint uq_traveller_type_traveller_type_name unique (traveller_type_name),
  constraint pk_traveller_type primary key (ttypeid)
);

create table treasure_hunt (
  thuntid                       integer auto_increment not null,
  title                         varchar(255),
  riddle                        varchar(255),
  start_date                    varchar(255),
  end_date                      varchar(255),
  user                          integer,
  constraint pk_treasure_hunt primary key (thuntid)
);

create table trip (
  tripid                        integer auto_increment not null,
  trip_name                     varchar(255),
  removed_visits                integer default 0,
  is_public                     boolean default false not null,
  user                          integer,
  constraint pk_trip primary key (tripid)
);

create table user (
  userid                        integer auto_increment not null,
  email                         varchar(255),
  password                      varchar(255),
  date_of_birth                 date,
  gender                        varchar(255),
  f_name                        varchar(255),
  l_name                        varchar(255),
  is_admin                      boolean,
  creation_date                 timestamp not null,
  constraint pk_user primary key (userid)
);

create table user_nationality (
  user_userid                   integer not null,
  nationality_natid             integer not null,
  constraint pk_user_nationality primary key (user_userid,nationality_natid)
);

create table user_passport (
  user_userid                   integer not null,
  passport_passid               integer not null,
  constraint pk_user_passport primary key (user_userid,passport_passid)
);

create table user_traveller_type (
  user_userid                   integer not null,
  traveller_type_ttypeid        integer not null,
  constraint pk_user_traveller_type primary key (user_userid,traveller_type_ttypeid)
);

create table user_treasure_hunt (
  user_userid                   integer not null,
  treasure_hunt_thuntid         integer not null,
  constraint pk_user_treasure_hunt primary key (user_userid,treasure_hunt_thuntid)
);

create table user_photo (
  photo_id                      integer auto_increment not null,
  url                           varchar(255),
  is_public                     boolean default false not null,
  is_profile                    boolean default false not null,
  user                          integer,
  constraint pk_user_photo primary key (photo_id)
);

create table user_photo_destination (
  user_photo_photo_id           integer not null,
  destination_destid            integer not null,
  constraint pk_user_photo_destination primary key (user_photo_photo_id,destination_destid)
);

create table visit (
  visitid                       integer auto_increment not null,
  visitorder                    integer,
  destination                   integer,
  trip                          integer,
  arrival                       varchar(255),
  departure                     varchar(255),
  visit_name                    varchar(255),
  constraint pk_visit primary key (visitid)
);

create index ix_destination_primary_photo_photo_id on destination (primary_photo_photo_id);
alter table destination add constraint fk_destination_primary_photo_photo_id foreign key (primary_photo_photo_id) references user_photo (photo_id) on delete restrict on update restrict;

create index ix_destination_user on destination (user);
alter table destination add constraint fk_destination_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_destination_traveller_type_destination on destination_traveller_type (destination_destid);
alter table destination_traveller_type add constraint fk_destination_traveller_type_destination foreign key (destination_destid) references destination (destid) on delete restrict on update restrict;

create index ix_destination_traveller_type_traveller_type on destination_traveller_type (traveller_type_ttypeid);
alter table destination_traveller_type add constraint fk_destination_traveller_type_traveller_type foreign key (traveller_type_ttypeid) references traveller_type (ttypeid) on delete restrict on update restrict;

create index ix_destination_modification_request_old_destination_destid on destination_modification_request (old_destination_destid);
alter table destination_modification_request add constraint fk_destination_modification_request_old_destination_destid foreign key (old_destination_destid) references destination (destid) on delete restrict on update restrict;

create index ix_destination_modification_request_request_author_userid on destination_modification_request (request_author_userid);
alter table destination_modification_request add constraint fk_destination_modification_request_request_author_userid foreign key (request_author_userid) references user (userid) on delete restrict on update restrict;

create index ix_destination_modification_request_traveller_type_destin_1 on destination_modification_request_traveller_type (destination_modification_request_id);
alter table destination_modification_request_traveller_type add constraint fk_destination_modification_request_traveller_type_destin_1 foreign key (destination_modification_request_id) references destination_modification_request (id) on delete restrict on update restrict;

create index ix_destination_modification_request_traveller_type_travel_2 on destination_modification_request_traveller_type (traveller_type_ttypeid);
alter table destination_modification_request_traveller_type add constraint fk_destination_modification_request_traveller_type_travel_2 foreign key (traveller_type_ttypeid) references traveller_type (ttypeid) on delete restrict on update restrict;

create index ix_treasure_hunt_user on treasure_hunt (user);
alter table treasure_hunt add constraint fk_treasure_hunt_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_trip_user on trip (user);
alter table trip add constraint fk_trip_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_user_nationality_user on user_nationality (user_userid);
alter table user_nationality add constraint fk_user_nationality_user foreign key (user_userid) references user (userid) on delete restrict on update restrict;

create index ix_user_nationality_nationality on user_nationality (nationality_natid);
alter table user_nationality add constraint fk_user_nationality_nationality foreign key (nationality_natid) references nationality (natid) on delete restrict on update restrict;

create index ix_user_passport_user on user_passport (user_userid);
alter table user_passport add constraint fk_user_passport_user foreign key (user_userid) references user (userid) on delete restrict on update restrict;

create index ix_user_passport_passport on user_passport (passport_passid);
alter table user_passport add constraint fk_user_passport_passport foreign key (passport_passid) references passport (passid) on delete restrict on update restrict;

create index ix_user_traveller_type_user on user_traveller_type (user_userid);
alter table user_traveller_type add constraint fk_user_traveller_type_user foreign key (user_userid) references user (userid) on delete restrict on update restrict;

create index ix_user_traveller_type_traveller_type on user_traveller_type (traveller_type_ttypeid);
alter table user_traveller_type add constraint fk_user_traveller_type_traveller_type foreign key (traveller_type_ttypeid) references traveller_type (ttypeid) on delete restrict on update restrict;

create index ix_user_treasure_hunt_user on user_treasure_hunt (user_userid);
alter table user_treasure_hunt add constraint fk_user_treasure_hunt_user foreign key (user_userid) references user (userid) on delete restrict on update restrict;

create index ix_user_treasure_hunt_treasure_hunt on user_treasure_hunt (treasure_hunt_thuntid);
alter table user_treasure_hunt add constraint fk_user_treasure_hunt_treasure_hunt foreign key (treasure_hunt_thuntid) references treasure_hunt (thuntid) on delete restrict on update restrict;

create index ix_user_photo_user on user_photo (user);
alter table user_photo add constraint fk_user_photo_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_user_photo_destination_user_photo on user_photo_destination (user_photo_photo_id);
alter table user_photo_destination add constraint fk_user_photo_destination_user_photo foreign key (user_photo_photo_id) references user_photo (photo_id) on delete restrict on update restrict;

create index ix_user_photo_destination_destination on user_photo_destination (destination_destid);
alter table user_photo_destination add constraint fk_user_photo_destination_destination foreign key (destination_destid) references destination (destid) on delete restrict on update restrict;

create index ix_visit_destination on visit (destination);
alter table visit add constraint fk_visit_destination foreign key (destination) references destination (destid) on delete restrict on update restrict;

create index ix_visit_trip on visit (trip);
alter table visit add constraint fk_visit_trip foreign key (trip) references trip (tripid) on delete restrict on update restrict;


# --- !Downs

alter table destination drop constraint if exists fk_destination_primary_photo_photo_id;
drop index if exists ix_destination_primary_photo_photo_id;

alter table destination drop constraint if exists fk_destination_user;
drop index if exists ix_destination_user;

alter table destination_traveller_type drop constraint if exists fk_destination_traveller_type_destination;
drop index if exists ix_destination_traveller_type_destination;

alter table destination_traveller_type drop constraint if exists fk_destination_traveller_type_traveller_type;
drop index if exists ix_destination_traveller_type_traveller_type;

alter table destination_modification_request drop constraint if exists fk_destination_modification_request_old_destination_destid;
drop index if exists ix_destination_modification_request_old_destination_destid;

alter table destination_modification_request drop constraint if exists fk_destination_modification_request_request_author_userid;
drop index if exists ix_destination_modification_request_request_author_userid;

alter table destination_modification_request_traveller_type drop constraint if exists fk_destination_modification_request_traveller_type_destin_1;
drop index if exists ix_destination_modification_request_traveller_type_destin_1;

alter table destination_modification_request_traveller_type drop constraint if exists fk_destination_modification_request_traveller_type_travel_2;
drop index if exists ix_destination_modification_request_traveller_type_travel_2;

alter table treasure_hunt drop constraint if exists fk_treasure_hunt_user;
drop index if exists ix_treasure_hunt_user;

alter table trip drop constraint if exists fk_trip_user;
drop index if exists ix_trip_user;

alter table user_nationality drop constraint if exists fk_user_nationality_user;
drop index if exists ix_user_nationality_user;

alter table user_nationality drop constraint if exists fk_user_nationality_nationality;
drop index if exists ix_user_nationality_nationality;

alter table user_passport drop constraint if exists fk_user_passport_user;
drop index if exists ix_user_passport_user;

alter table user_passport drop constraint if exists fk_user_passport_passport;
drop index if exists ix_user_passport_passport;

alter table user_traveller_type drop constraint if exists fk_user_traveller_type_user;
drop index if exists ix_user_traveller_type_user;

alter table user_traveller_type drop constraint if exists fk_user_traveller_type_traveller_type;
drop index if exists ix_user_traveller_type_traveller_type;

alter table user_treasure_hunt drop constraint if exists fk_user_treasure_hunt_user;
drop index if exists ix_user_treasure_hunt_user;

alter table user_treasure_hunt drop constraint if exists fk_user_treasure_hunt_treasure_hunt;
drop index if exists ix_user_treasure_hunt_treasure_hunt;

alter table user_photo drop constraint if exists fk_user_photo_user;
drop index if exists ix_user_photo_user;

alter table user_photo_destination drop constraint if exists fk_user_photo_destination_user_photo;
drop index if exists ix_user_photo_destination_user_photo;

alter table user_photo_destination drop constraint if exists fk_user_photo_destination_destination;
drop index if exists ix_user_photo_destination_destination;

alter table visit drop constraint if exists fk_visit_destination;
drop index if exists ix_visit_destination;

alter table visit drop constraint if exists fk_visit_trip;
drop index if exists ix_visit_trip;

drop table if exists admin;

drop table if exists destination;

drop table if exists destination_traveller_type;

drop table if exists destination_modification_request;

drop table if exists destination_modification_request_traveller_type;

drop table if exists nationality;

drop table if exists passport;

drop table if exists traveller_type;

drop table if exists treasure_hunt;

drop table if exists trip;

drop table if exists user;

drop table if exists user_nationality;

drop table if exists user_passport;

drop table if exists user_traveller_type;

drop table if exists user_treasure_hunt;

drop table if exists user_photo;

drop table if exists user_photo_destination;

drop table if exists visit;

