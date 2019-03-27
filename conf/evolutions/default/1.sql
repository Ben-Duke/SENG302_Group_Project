# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table admin (
  id                            bigint auto_increment not null,
  user_id                       integer,
  is_default                    boolean default false not null,
  constraint pk_admin primary key (id)
);

create table company (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_company primary key (id)
);

create table computer (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  introduced                    timestamp,
  discontinued                  timestamp,
  company_id                    bigint,
  constraint pk_computer primary key (id)
);

create table destination (
  destid                        integer auto_increment not null,
  dest_name                     varchar(255),
  dest_type                     varchar(255),
  district                      varchar(255),
  country                       varchar(255),
  latitude                      double not null,
  longitude                     double not null,
  user                          integer,
  constraint pk_destination primary key (destid)
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

create table trip (
  tripid                        integer auto_increment not null,
  trip_name                     varchar(255),
  removed_visits                integer default 0,
  user                          integer,
  constraint pk_trip primary key (tripid)
);

create table user (
  userid                        integer auto_increment not null,
  username                      varchar(255),
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

create table user_photo (
  photo_id                      integer auto_increment not null,
  url                           varchar(255),
  is_public                     boolean default false not null,
  user                          integer,
  constraint pk_user_photo primary key (photo_id)
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

create index ix_computer_company_id on computer (company_id);
alter table computer add constraint fk_computer_company_id foreign key (company_id) references company (id) on delete restrict on update restrict;

create index ix_destination_user on destination (user);
alter table destination add constraint fk_destination_user foreign key (user) references user (userid) on delete restrict on update restrict;

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

create index ix_user_photo_user on user_photo (user);
alter table user_photo add constraint fk_user_photo_user foreign key (user) references user (userid) on delete restrict on update restrict;

create index ix_visit_destination on visit (destination);
alter table visit add constraint fk_visit_destination foreign key (destination) references destination (destid) on delete restrict on update restrict;

create index ix_visit_trip on visit (trip);
alter table visit add constraint fk_visit_trip foreign key (trip) references trip (tripid) on delete restrict on update restrict;


# --- !Downs

alter table computer drop constraint if exists fk_computer_company_id;
drop index if exists ix_computer_company_id;

alter table destination drop constraint if exists fk_destination_user;
drop index if exists ix_destination_user;

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

alter table user_photo drop constraint if exists fk_user_photo_user;
drop index if exists ix_user_photo_user;

alter table visit drop constraint if exists fk_visit_destination;
drop index if exists ix_visit_destination;

alter table visit drop constraint if exists fk_visit_trip;
drop index if exists ix_visit_trip;

drop table if exists admin;

drop table if exists company;

drop table if exists computer;

drop table if exists destination;

drop table if exists nationality;

drop table if exists passport;

drop table if exists traveller_type;

drop table if exists trip;

drop table if exists user;

drop table if exists user_nationality;

drop table if exists user_passport;

drop table if exists user_traveller_type;

drop table if exists user_photo;

drop table if exists visit;

