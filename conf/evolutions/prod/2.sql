# # --- DB population file, same contents as prod/2.sql except for ups/downs comments
#
# # -- Disable inspection for checking tables exist as they are in another file

# noinspection SqlResolveForFile

# !Ups


# Admin
INSERT INTO `admin` (`id`, `user_id`, `user_id_to_edit`, `is_default`) VALUES
(1, 1, NULL, 1);


# Destination
INSERT INTO `destination` (`destid`, `dest_name`, `dest_type`, `district`, `country`,
                           `is_country_valid`, `latitude`, `longitude`, `is_public`,
                           `primary_photo_photo_id`, `user`) VALUES
(1, 'Christchurch', 'Town', 'Canterbury', 'New Zealand', 1, -43.5321, 172.6362, 1, NULL, 2),
(2, 'Wellington', 'Town', 'Wellington', 'New Zealand', 1, -41.2866, 174.7756, 0, NULL, 2),
(3, 'The Wok', 'Cafe/Restaurant', 'Canterbury', 'New Zealand', 1, -43.523593, 172.582971, 1, NULL, 2),
(4, 'Hanmer Springs Thermal Pools', 'Attraction', 'North Canterbury', 'New Zealand', 1, -42.522791, 172.828944, 1, NULL, 3),
(5, 'Le Mans 24 hour race', 'Event', 'Le Mans', 'France', 1, 47.956221, 0.207828, 0, NULL, 3),
(6, 'Great Pyramid of Giza', 'Attraction', 'Giza', 'Egypt', 1, 29.979481, 31.134159, 1, NULL, 3),
(7, 'Niagara Falls', 'Natural Spot', 'New York', 'United States', 0, 29.979481, 31.134159, 0, NULL, 4),
(8, 'Vatican City', 'Country', 'Rome', 'Vatican City', 0, 41.903133, 12.454341, 0, NULL, 4),
(9, 'Lincoln Memorial', 'Monument', 'Washington DC', 'United States', 0, 38.889406, -77.050155, 1, NULL, 4);


# Destination_Traveller_Type
INSERT INTO `destination_traveller_type` (`destination_destid`,
                                          `traveller_type_ttypeid`) VALUES
(1, 1),
(3, 1),
(3, 3),
(4, 5),
(4, 7),
(6, 7),
(7, 2),
(9, 1),
(9, 4),
(9, 6);


# destination_modification_request
# no test data


# destination_modification_request_traveller_type
# no test data


# nationality
# set in code


# passport
# set in code

# Traveller Types
INSERT INTO traveller_type (traveller_type_name)
VALUES ('Groupie'),
('Thrillseeker'),
('Gap Year'),
('Frequent Weekender'),
('Holidaymaker'),
('Business Traveller'),
('Backpacker');


# treasure_hunt


# trip


# user

#


# User table
# Have to insert passwords hashed
INSERT INTO `user` (`userid`, `email`, `password_hash`, `date_of_birth`, `gender`,
                    `f_name`, `l_name`, `undo_redo_error`, `is_admin`,
                    `creation_date`) VALUES
(1, 'admin@admin.com', '$2a$10$XIZ6dbHcnk3PrVvsYftFGekaxFhdmasUWNCM/ya.OCXuVqxRBJ0qe',
 '2019-02-18', 'male', 'admin', 'admin', 0, 0, '2019-07-26 03:59:17'),

(2, 'testuser1@uclive.ac.nz', '$2a$10$QpIBHLosdViQscy5ydKnceyMe52jhtThQtHz/Xb8ylp6rIlENkpay',
 '1998-08-23', 'Male', 'Gavin', 'Ong', 0, 0, '2019-07-26 03:59:17'),

(3, 'testuser2@uclive.ac.nz', '$2a$10$9vvZhEw.MV1wKhUazZLV7uYTzq.iShj9UBCNcrhTJVNA38SZUmtjK',
 '1960-12-25', 'Female', 'Caitlyn', 'Jenner', 0, 0, '2019-07-26 03:59:17'),

(4, 'testuser3@uclive.ac.nz', '$2a$10$H4tQZWBh3Hd3JqUV3NV5Z.4TnG.mdztdSjUs9VIlQToyuGl4Lz6LW',
 '2006-06-09', 'Male', 'John', 'Smith', 0, 0, '2019-07-26 03:59:17');


#
# !Downs

# delete from admin;
#
# delete from destination;
#
# delete from destination_traveller_type;
#
# delete from destination_modification_request;
#
# delete from destination_modification_request_traveller_type;
#
# delete from nationality;
#
# delete from passport;
#
# delete from traveller_type;
#
# delete from treasure_hunt;
#
# delete from trip;
#
# delete from user;
#
# delete from user_nationality;
#
# delete from user_passport;
#
# delete from user_traveller_type;
#
# delete from user_treasure_hunt;
#
# delete from user_photo;
#
# delete from user_photo_destination;
#
# delete from visit;