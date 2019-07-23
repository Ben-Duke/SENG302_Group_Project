# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                            bigint auto_increment not null,
  user_id                       integer,
  user_id_to_edit               integer,
  is_default                    boolean default false not null,
  constraint pk_admin primary key (id)
);

create table album (
  album_id                      integer auto_increment not null,
  user                          integer,
  destination                   integer,
  title                         varchar(255),
  constraint pk_album primary key (album_id)
);

create table album_media (
  album_album_id                integer not null,
  media_media_id                integer not null,
  constraint pk_album_media primary key (album_album_id,media_media_id)
);

create table destination (
  destid                        integer auto_increment not null,
  dest_name                     varchar(255),
  dest_type                     varchar(255),
  district                      varchar(255),
  country                       varchar(255),
  is_country_valid              boolean default false not null,
  latitude                      double not null,
  longitude                     double not null,
  is_public                     boolean default false not null,
  primary_photo_media_id        integer,
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

create table media (
  dtype                         varchar(31) not null,
  media_id                      integer auto_increment not null,
  url                           varchar(255),
  user                          integer,
  is_media_public               boolean default false not null,
  is_profile                    boolean default false not null,
  constraint uq_media_url unique (url),
  constraint pk_media primary key (media_id)
);

create table media_destination (
  media_media_id                integer not null,
  destination_destid            integer not null,
  constraint pk_media_destination primary key (media_media_id,destination_destid)
);

create table nationality (
  natid                         integer auto_increment not null,
  country_valid                 boolean,
  nationality_name              varchar(255),
  constraint uq_nationality_nationality_name unique (nationality_name),
  constraint pk_nationality primary key (natid)
);

create table passport (
  passid                        integer auto_increment not null,
  country_valid                 boolean,
  passport_name                 varchar(255),
  constraint uq_passport_passport_name unique (passport_name),
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
  destination_destid            integer,
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
  password_hash                 varchar(255),
  date_of_birth                 date,
  gender                        varchar(255),
  f_name                        varchar(255),
  l_name                        varchar(255),
  undo_redo_error               boolean default false not null,
  is_admin                      boolean,
  creation_date                 timestamp not null,
  constraint uq_user_email unique (email),
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

create index ix_album_user on album (user);
alter table album add constraint fk_album_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_album_destination on album (destination);
alter table album add constraint fk_album_destination foreign key (destination) references destination (destid) on delete restrict on update restrict;

create index ix_album_media_album on album_media (album_album_id);
alter table album_media add constraint fk_album_media_album foreign key (album_album_id) references album (album_id) on delete restrict on update restrict;

create index ix_album_media_media on album_media (media_media_id);
alter table album_media add constraint fk_album_media_media foreign key (media_media_id) references media (media_id) on delete restrict on update restrict;

create index ix_destination_primary_photo_media_id on destination (primary_photo_media_id);
alter table destination add constraint fk_destination_primary_photo_media_id foreign key (primary_photo_media_id) references media (media_id) on delete restrict on update restrict;

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

create index ix_media_user on media (user);
alter table media add constraint fk_media_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_media_destination_media on media_destination (media_media_id);
alter table media_destination add constraint fk_media_destination_media foreign key (media_media_id) references media (media_id) on delete restrict on update restrict;

create index ix_media_destination_destination on media_destination (destination_destid);
alter table media_destination add constraint fk_media_destination_destination foreign key (destination_destid) references destination (destid) on delete restrict on update restrict;

create index ix_treasure_hunt_destination_destid on treasure_hunt (destination_destid);
alter table treasure_hunt add constraint fk_treasure_hunt_destination_destid foreign key (destination_destid) references destination (destid) on delete restrict on update restrict;

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

create index ix_visit_destination on visit (destination);
alter table visit add constraint fk_visit_destination foreign key (destination) references destination (destid) on delete restrict on update restrict;

create index ix_visit_trip on visit (trip);
alter table visit add constraint fk_visit_trip foreign key (trip) references trip (tripid) on delete restrict on update restrict;


# --- !Downs

alter table album drop constraint if exists fk_album_user;
drop index if exists ix_album_user;

alter table album drop constraint if exists fk_album_destination;
drop index if exists ix_album_destination;

alter table album_media drop constraint if exists fk_album_media_album;
drop index if exists ix_album_media_album;

alter table album_media drop constraint if exists fk_album_media_media;
drop index if exists ix_album_media_media;

alter table destination drop constraint if exists fk_destination_primary_photo_media_id;
drop index if exists ix_destination_primary_photo_media_id;

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

alter table media drop constraint if exists fk_media_user;
drop index if exists ix_media_user;

alter table media_destination drop constraint if exists fk_media_destination_media;
drop index if exists ix_media_destination_media;

alter table media_destination drop constraint if exists fk_media_destination_destination;
drop index if exists ix_media_destination_destination;

alter table treasure_hunt drop constraint if exists fk_treasure_hunt_destination_destid;
drop index if exists ix_treasure_hunt_destination_destid;

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

alter table visit drop constraint if exists fk_visit_destination;
drop index if exists ix_visit_destination;

alter table visit drop constraint if exists fk_visit_trip;
drop index if exists ix_visit_trip;

drop table if exists admin;

drop table if exists album;

drop table if exists album_media;

drop table if exists destination;

drop table if exists destination_traveller_type;

drop table if exists destination_modification_request;

drop table if exists destination_modification_request_traveller_type;

drop table if exists media;

drop table if exists media_destination;

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

drop table if exists visit;

