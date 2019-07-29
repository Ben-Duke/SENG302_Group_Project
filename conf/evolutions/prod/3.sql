# DB population file, same contents as test/3.sql except for ups/downs comments
# Does not populate passports/nationalities

# Disable inspection for checking tables exist as they are in another file
# noinspection SqlResolveForFile


# --------------------------------------------------------------

# !Ups


# user
# Passwords are set programmatically
INSERT INTO `user` (`userid`, `email`, `password_hash`, `date_of_birth`, `gender`,
                    `f_name`, `l_name`, `undo_redo_error`, `is_admin`,
                    `creation_date`) VALUES
(1, 'admin@admin.com', '',
 '2019-02-18', 'male', 'admin', 'admin', 0, 0, '2019-07-26 03:59:17'),

(2, 'testuser1@uclive.ac.nz', '',
 '1998-08-23', 'Male', 'Gavin', 'Ong', 0, 0, '2019-07-26 03:59:17'),

(3, 'testuser2@uclive.ac.nz', '',
 '1960-12-25', 'Female', 'Caitlyn', 'Jenner', 0, 0, '2019-07-26 03:59:17'),

(4, 'testuser3@uclive.ac.nz', '',
 '2006-06-09', 'Male', 'John', 'Smith', 0, 0, '2019-07-26 03:59:17');


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


# Traveller Types
INSERT INTO `traveller_type` (`ttypeid`, `traveller_type_name`) VALUES
(1, 'Backpacker'),
(2, 'Business Traveller'),
(3, 'Frequent Weekender'),
(4, 'Gap Year'),
(5, 'Groupie'),
(6, 'Holidaymaker'),
(7, 'Thrillseeker');


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


# treasure_hunt
INSERT INTO `treasure_hunt` (`thuntid`, `title`, `riddle`, `destination_destid`,
                             `start_date`, `end_date`, `user`) VALUES
(1, 'Surprise', 'The garden city', 1, '2019-04-17', '2019-12-25', 2),
(2, 'Surprise2', 'Prime example of inflation', 3, '2019-04-17', '2019-12-25', 3),
(3, 'Closed Treasure Hunt', 'You should not be able to view this', 4, '2019-04-17',
 '2019-04-25', 4);


# trip
INSERT INTO `trip` (`tripid`, `trip_name`, `removed_visits`, `is_public`, `user`) VALUES
(1, 'Trip to New Zealand', 0, 1, 2),
(2, 'Christchurch to Wellington, to The Wok and back', 0, 0, 2),
(3, 'World Tour', 0, 1, 3),
(4, 'Pyramid to Race and back again', 0, 0, 3),
(5, 'See the pope, the president and come back', 0, 1, 4),
(6, 'Waterfall walk and see the president', 0, 0, 4);


# user_nationality
INSERT INTO `user_nationality` (`user_userid`, `nationality_natid`) VALUES
(1, 1),
(1, 2),
(2, 251),
(3, 71),
(3, 72),
(4, 51);


# user_passport
INSERT INTO `user_passport` (`user_userid`, `passport_passid`) VALUES
(2, 251),
(3, 71),
(3, 72);


# user_traveller_type
INSERT INTO `user_traveller_type` (`user_userid`, `traveller_type_ttypeid`) VALUES
(1, 5),
(2, 3),
(3, 2),
(4, 1),
(4, 2);

# user_treasure_hunt
# no data


# user_photo
# no data


# user_photo_destination
# no data


# visit
INSERT INTO `visit` (`visitid`, `visitorder`, `destination`, `trip`, `arrival`,
                     `departure`, `visit_name`) VALUES
(1, 1, 1, 1, '2018-05-04', '2018-05-06', 'Christchurch'),
(2, 2, 2, 1, '2018-05-06', '2018-05-08', 'Wellington'),
(3, 1, 1, 2, NULL, NULL, 'Christchurch'),
(4, 2, 2, 2, NULL, NULL, 'Wellington'),
(5, 3, 3, 2, NULL, NULL, 'The Wok'),
(6, 4, 1, 2, NULL, NULL, 'Christchurch'),
(7, 1, 4, 3, '2003-08-12', NULL, 'Hanmer Springs Thermal Pools'),
(8, 2, 5, 3, NULL, NULL, 'Le Mans 24 hour race'),
(9, 3, 6, 3, NULL, NULL, 'Great Pyramid of Giza'),
(10, 1, 6, 4, NULL, '2019-04-05', 'Great Pyramid of Giza'),
(11, 2, 5, 4, NULL, NULL, 'Le Mans 24 hour race'),
(12, 3, 6, 4, NULL, NULL, 'Great Pyramid of Giza'),
(13, 1, 8, 5, NULL, NULL, 'Vatican City'),
(14, 2, 9, 5, NULL, NULL, 'Lincoln Memorial'),
(15, 3, 8, 5, NULL, NULL, 'Vatican City'),
(16, 1, 7, 6, NULL, NULL, 'Niagara Falls'),
(17, 2, 9, 6, NULL, NULL, 'Lincoln Memorial');


# -------------------------------------------------


# !Downs
# Delete in reverse order to the order data was added to avoid violating
# foreign key constraints

SET FOREIGN_KEY_CHECKS=0; -- to disable them

delete from visit;

delete from user_photo_destination;

delete from user_photo;

delete from user_treasure_hunt;

delete from user_traveller_type;

delete from user_passport;

delete from user_nationality;

delete from trip;

delete from treasure_hunt;

delete from destination_modification_request_traveller_type;

delete from destination_modification_request;

delete from destination_traveller_type;

delete from traveller_type;

delete from destination;

delete from admin;

delete from user;

SET FOREIGN_KEY_CHECKS=1; -- to re-enable them
