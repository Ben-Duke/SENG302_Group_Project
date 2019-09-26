-- Population file for manual testing/sbt run

-- Disable inspection for checking tables exist as they are in another file
-- noinspection SqlResolveForFile

# --- !Ups

-- Population data for pass/nats - same rows inserted into both tables,
-- this is reliant on them having the same structure
INSERT INTO `passport` (`passid`, `country_valid`, `passport_name`) VALUES
(1, 1, 'Afghanistan'),
(2, 1, 'Albania'),
(3, 1, 'Algeria'),
(4, 1, 'American Samoa'),
(5, 1, 'Andorra'),
(6, 1, 'Angola'),
(7, 1, 'Anguilla'),
(8, 1, 'Antarctica'),
(9, 1, 'Antigua and Barbuda'),
(10, 1, 'Argentina'),
(11, 1, 'Armenia'),
(12, 1, 'Aruba'),
(13, 1, 'Australia'),
(14, 1, 'Austria'),
(15, 1, 'Azerbaijan'),
(16, 1, 'Bahamas'),
(17, 1, 'Bahrain'),
(18, 1, 'Bangladesh'),
(19, 1, 'Barbados'),
(20, 1, 'Belarus'),
(21, 1, 'Belgium'),
(22, 1, 'Belize'),
(23, 1, 'Benin'),
(24, 1, 'Bermuda'),
(25, 1, 'Bhutan'),
(26, 1, 'Bolivia (Plurinational State of)'),
(27, 1, 'Bonaire, Sint Eustatius and Saba'),
(28, 1, 'Bosnia and Herzegovina'),
(29, 1, 'Botswana'),
(30, 1, 'Bouvet Island'),
(31, 1, 'Brazil'),
(32, 1, 'British Indian Ocean Territory'),
(33, 1, 'Brunei Darussalam'),
(34, 1, 'Bulgaria'),
(35, 1, 'Burkina Faso'),
(36, 1, 'Burundi'),
(37, 1, 'Cabo Verde'),
(38, 1, 'Cambodia'),
(39, 1, 'Cameroon'),
(40, 1, 'Canada'),
(41, 1, 'Cayman Islands'),
(42, 1, 'Central African Republic'),
(43, 1, 'Chad'),
(44, 1, 'Chile'),
(45, 1, 'China'),
(46, 1, 'Christmas Island'),
(47, 1, 'Cocos (Keeling) Islands'),
(48, 1, 'Colombia'),
(49, 1, 'Comoros'),
(50, 1, 'Congo'),
(51, 1, 'Congo (Democratic Republic of the)'),
(52, 1, 'Cook Islands'),
(53, 1, 'Costa Rica'),
(54, 1, 'Croatia'),
(55, 1, 'Cuba'),
(56, 1, 'Curaçao'),
(57, 1, 'Cyprus'),
(58, 1, 'Czech Republic'),
(59, 1, 'Côte d''Ivoire'),
(60, 1, 'Denmark'),
(61, 1, 'Djibouti'),
(62, 1, 'Dominica'),
(63, 1, 'Dominican Republic'),
(64, 1, 'Ecuador'),
(65, 1, 'Egypt'),
(66, 1, 'El Salvador'),
(67, 1, 'Equatorial Guinea'),
(68, 1, 'Eritrea'),
(69, 1, 'Estonia'),
(70, 1, 'Ethiopia'),
(71, 1, 'Falkland Islands (Malvinas)'),
(72, 1, 'Faroe Islands'),
(73, 1, 'Fiji'),
(74, 1, 'Finland'),
(75, 1, 'France'),
(76, 1, 'French Guiana'),
(77, 1, 'French Polynesia'),
(78, 1, 'French Southern Territories'),
(79, 1, 'Gabon'),
(80, 1, 'Gambia'),
(81, 1, 'Georgia'),
(82, 1, 'Germany'),
(83, 1, 'Ghana'),
(84, 1, 'Gibraltar'),
(85, 1, 'Greece'),
(86, 1, 'Greenland'),
(87, 1, 'Grenada'),
(88, 1, 'Guadeloupe'),
(89, 1, 'Guam'),
(90, 1, 'Guatemala'),
(91, 1, 'Guernsey'),
(92, 1, 'Guinea'),
(93, 1, 'Guinea-Bissau'),
(94, 1, 'Guyana'),
(95, 1, 'Haiti'),
(96, 1, 'Heard Island and McDonald Islands'),
(97, 1, 'Holy See'),
(98, 1, 'Honduras'),
(99, 1, 'Hong Kong'),
(100, 1, 'Hungary'),
(101, 1, 'Iceland'),
(102, 1, 'India'),
(103, 1, 'Indonesia'),
(104, 1, 'Iran (Islamic Republic of)'),
(105, 1, 'Iraq'),
(106, 1, 'Ireland'),
(107, 1, 'Isle of Man'),
(108, 1, 'Israel'),
(109, 1, 'Italy'),
(110, 1, 'Jamaica'),
(111, 1, 'Japan'),
(112, 1, 'Jersey'),
(113, 1, 'Jordan'),
(114, 1, 'Kazakhstan'),
(115, 1, 'Kenya'),
(116, 1, 'Kiribati'),
(117, 1, 'Korea (Democratic People''s Republic of)'),
(118, 1, 'Korea (Republic of)'),
(119, 1, 'Kuwait'),
(120, 1, 'Kyrgyzstan'),
(121, 1, 'Lao People''s Democratic Republic'),
(122, 1, 'Latvia'),
(123, 1, 'Lebanon'),
(124, 1, 'Lesotho'),
(125, 1, 'Liberia'),
(126, 1, 'Libya'),
(127, 1, 'Liechtenstein'),
(128, 1, 'Lithuania'),
(129, 1, 'Luxembourg'),
(130, 1, 'Macao'),
(131, 1, 'Macedonia (the former Yugoslav Republic of)'),
(132, 1, 'Madagascar'),
(133, 1, 'Malawi'),
(134, 1, 'Malaysia'),
(135, 1, 'Maldives'),
(136, 1, 'Mali'),
(137, 1, 'Malta'),
(138, 1, 'Marshall Islands'),
(139, 1, 'Martinique'),
(140, 1, 'Mauritania'),
(141, 1, 'Mauritius'),
(142, 1, 'Mayotte'),
(143, 1, 'Mexico'),
(144, 1, 'Micronesia (Federated States of)'),
(145, 1, 'Moldova (Republic of)'),
(146, 1, 'Monaco'),
(147, 1, 'Mongolia'),
(148, 1, 'Montenegro'),
(149, 1, 'Montserrat'),
(150, 1, 'Morocco'),
(151, 1, 'Mozambique'),
(152, 1, 'Myanmar'),
(153, 1, 'Namibia'),
(154, 1, 'Nauru'),
(155, 1, 'Nepal'),
(156, 1, 'Netherlands'),
(157, 1, 'New Caledonia'),
(158, 1, 'New Zealand'),
(159, 1, 'Nicaragua'),
(160, 1, 'Niger'),
(161, 1, 'Nigeria'),
(162, 1, 'Niue'),
(163, 1, 'Norfolk Island'),
(164, 1, 'Northern Mariana Islands'),
(165, 1, 'Norway'),
(166, 1, 'Oman'),
(167, 1, 'Pakistan'),
(168, 1, 'Palau'),
(169, 1, 'Palestine, State of'),
(170, 1, 'Panama'),
(171, 1, 'Papua New Guinea'),
(172, 1, 'Paraguay'),
(173, 1, 'Peru'),
(174, 1, 'Philippines'),
(175, 1, 'Pitcairn'),
(176, 1, 'Poland'),
(177, 1, 'Portugal'),
(178, 1, 'Puerto Rico'),
(179, 1, 'Qatar'),
(180, 1, 'Republic of Kosovo'),
(181, 1, 'Romania'),
(182, 1, 'Russian Federation'),
(183, 1, 'Rwanda'),
(184, 1, 'Réunion'),
(185, 1, 'Saint Barthélemy'),
(186, 1, 'Saint Helena, Ascension and Tristan da Cunha'),
(187, 1, 'Saint Kitts and Nevis'),
(188, 1, 'Saint Lucia'),
(189, 1, 'Saint Martin (French part)'),
(190, 1, 'Saint Pierre and Miquelon'),
(191, 1, 'Saint Vincent and the Grenadines'),
(192, 1, 'Samoa'),
(193, 1, 'San Marino'),
(194, 1, 'Sao Tome and Principe'),
(195, 1, 'Saudi Arabia'),
(196, 1, 'Senegal'),
(197, 1, 'Serbia'),
(198, 1, 'Seychelles'),
(199, 1, 'Sierra Leone'),
(200, 1, 'Singapore'),
(201, 1, 'Sint Maarten (Dutch part)'),
(202, 1, 'Slovakia'),
(203, 1, 'Slovenia'),
(204, 1, 'Solomon Islands'),
(205, 1, 'Somalia'),
(206, 1, 'South Africa'),
(207, 1, 'South Georgia and the South Sandwich Islands'),
(208, 1, 'South Sudan'),
(209, 1, 'Spain'),
(210, 1, 'Sri Lanka'),
(211, 1, 'Sudan'),
(212, 1, 'Suriname'),
(213, 1, 'Svalbard and Jan Mayen'),
(214, 1, 'Swaziland'),
(215, 1, 'Sweden'),
(216, 1, 'Switzerland'),
(217, 1, 'Syrian Arab Republic'),
(218, 1, 'Taiwan'),
(219, 1, 'Tajikistan'),
(220, 1, 'Tanzania, United Republic of'),
(221, 1, 'Thailand'),
(222, 1, 'Timor-Leste'),
(223, 1, 'Togo'),
(224, 1, 'Tokelau'),
(225, 1, 'Tonga'),
(226, 1, 'Trinidad and Tobago'),
(227, 1, 'Tunisia'),
(228, 1, 'Turkey'),
(229, 1, 'Turkmenistan'),
(230, 1, 'Turks and Caicos Islands'),
(231, 1, 'Tuvalu'),
(232, 1, 'Uganda'),
(233, 1, 'Ukraine'),
(234, 1, 'United Arab Emirates'),
(235, 1, 'United Kingdom of Great Britain and Northern Ireland'),
(236, 1, 'United States Minor Outlying Islands'),
(237, 1, 'United States of America'),
(238, 1, 'Uruguay'),
(239, 1, 'Uzbekistan'),
(240, 1, 'Vanuatu'),
(241, 1, 'Venezuela (Bolivarian Republic of)'),
(242, 1, 'Viet Nam'),
(243, 1, 'Virgin Islands (British)'),
(244, 1, 'Virgin Islands (U.S.)'),
(245, 1, 'Wallis and Futuna'),
(246, 1, 'Western Sahara'),
(247, 1, 'Yemen'),
(248, 1, 'Zambia'),
(249, 1, 'Zimbabwe'),
(250, 1, 'Åland Islands'),
(251, 0, 'Czechoslovakia'); -- invalid country


INSERT INTO `nationality` (`natid`, `country_valid`, `nationality_name`)
SELECT *
FROM passport;


-- user
-- Passwords are set programmatically
INSERT INTO `user` (`userid`, `email`, `password_hash`, `date_of_birth`, `gender`, `f_name`, `l_name`, `undo_redo_error`, `is_admin`, `creation_date`) VALUES
(1, 'admin@admin.com', '$2a$10$v/y6du0Ngrm8k5Wp8Xu4c.TwxHgVQKjoyN98lsBcx.xXS2SSXY7MO', '2019-02-18', 'male', 'admin', 'admin', 0, 0, '2019-08-20 20:44:25'),
(2, 'testuser1@uclive.ac.nz', '$2a$10$EUMIDn.2bVaFxPGq5IdlXuX.aafZUY4DqmL6gYEHWM4t1Lp.t//kK', '1998-08-23', 'Male', 'Gavin', 'Ong', 0, 0, '2019-08-20 20:44:25'),
(3, 'testuser2@uclive.ac.nz', '$2a$10$HnwLNh5oKC1go2H5kOJOleu/oLszMIpYVzlq1/dkNAK/w/BhkTzdG', '1960-12-25', 'Female', 'Caitlyn', 'Jenner', 0, 0, '2019-08-20 20:44:25'),
(4, 'testuser3@uclive.ac.nz', '$2a$10$rjJrAFqLnTdb.Riy6VCMSOSdHcF5IF..tfkiW6Z2lm2jqYVRslQ7C', '2006-06-09', 'Male', 'John', 'Smith', 0, 0, '2019-08-20 20:44:25'),
(5, 'noel.bisson@gmail.com', '$2a$10$HqfW8ovh02QgqlKL1IUFMORpIBoBP/Dcpqx5rrrcj0/B..Twy9Cx6', '1998-03-06', 'Male', 'Noel', 'Bisson', 0, 0, '2019-08-20 20:46:28'),
(6, 'ben.duke@gmail.com', '$2a$10$EUMIDn.2bVaFxPGq5IdlXuX.aafZUY4DqmL6gYEHWM4t1Lp.t//kK', '1994-08-08', 'Male', 'Ben', 'Duke', 0, 0, '2019-08-20 20:47:47'),
(7, 'jack.orchard@gmail.com', '$2a$10$vQAR2DFztO1vdOAD6vC17.wrCZRW5/I66c/tixpDN47KuQ1AtkA3O', '1997-05-16', 'Male', 'Jack', 'Orchard', 0, 0, '2019-08-20 20:48:44'),
(8, 'logan.shaw@gmail.com', '$2Fa$10$gxxqY2u7DYUEQVcRK6jHJuYVMSRhViIekaHbMjh79kpqOrS135rXq', '1999-02-12', 'Male', 'Logan ', 'Shaw', 0, 0, '2019-08-20 20:49:17'),
(9, 'jason.little@gmail.com', '$2a$10$Kv59.oawFqlGJEVa3wyMfengJOxDZb.JDzHXeFn/Bhw5zqQR.mSUa', '1994-07-13', 'Male', 'Jason', 'Little', 0, 0, '2019-08-20 20:50:10'),
(10, 'priyesh.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'Priyesh', 'Shah', 0, 0, '2019-08-20 20:50:48'),
(11, 'michael.shannon@gmail.com', '$2a$10$i0GWnSrk2HSU4ZoFeV5em.kplOnwRDI18RgvGQqFEJ5KMMNNAW66i', '1998-01-15', 'Male', 'Michael', 'Shannon', 0, 0, '2019-08-20 20:51:22'),
(12, 'test.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'test', 'Shah', 0, 0, '2019-08-20 20:50:48'),
(13, 'test2.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'test2', 'Shah', 0, 0, '2019-08-20 20:50:48'),
(14, 'test3.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'test3', 'Shah', 0, 0, '2019-08-20 20:50:48'),
(15, 'test4.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'test4', 'Shah', 0, 0, '2019-08-20 20:50:48'),
(16, 'test5.shah@gmail.com', '$2a$10$GbadXoPxGghyCW6lPDTjIeNzS/JfiCIqmlauPNiDIyQFivees1oRq', '1996-06-13', 'Other', 'test5', 'Shah', 0, 0, '2019-08-20 20:50:48');
--




-- follow
INSERT INTO `follow`(`follow_id`, `follower`, `followed`, `state`) VALUES
(NULL,1,6,NULL ),
(NULL,2,3,NULL ),
(NULL,2,4,NULL ),
(NULL,2,5,NULL ),
(NULL,2,7,NULL ),
(NULL,2,8,NULL ),
(NULL,2,9,NULL ),
(NULL,2,10,NULL ),
(NULL,2,11,NULL ),
(NULL,2,12,NULL ),
(NULL,2,13,NULL ),
(NULL,2,14,NULL ),
(NULL,2,15,NULL ),
(NULL,2,16,NULL ),
(NULL,2,6,NULL ),
(NULL,3,6,NULL ),
(NULL,4,6,NULL ),
(NULL,5,6,NULL ),
(NULL,7,6,NULL ),
(NULL,8,6,NULL ),
(NULL,9,6,NULL ),
(NULL,10,6,NULL ),
(NULL,11,6,NULL ),
(NULL,12,6,NULL ),
(NULL,13,6,NULL ),
(NULL,14,6,NULL ),
(NULL,15,6,NULL ),
(NULL,3,2,NULL ),
(NULL,4,2,NULL );

-- Admin
INSERT INTO `admin` (`user_id`, `user_id_to_edit`, `is_default`) VALUES
(1, NULL, 1);

-- Destination
-- for public destinations, user col (2nd) should be NULL or 1 (default admin)
INSERT INTO `destination` (`destid`, `user`, `is_public`, `dest_name`, `dest_type`, `district`, `country`,
                           `is_country_valid`, `latitude`, `longitude`, `dest_is_public`, `primary_photo_media_id`) VALUES
(1, NULL, 1, 'Christchurch', 'Town', 'Canterbury', 'New Zealand', 1, -43.5321, 172.6362, 1, NULL),
(2, NULL, 1, 'Wellington', 'Town', 'Wellington', 'New Zealand', 1, -41.2866, 174.7756, 1, NULL),
(3, NULL, 1, 'The Wok', 'Cafe/Restaurant', 'Canterbury', 'New Zealand', 1, -43.523593, 172.582971, 1, NULL),
(4, NULL, 1, 'Hanmer Springs Thermal Pools', 'Attraction', 'North Canterbury', 'New Zealand', 1, -42.522791, 172.828944, 1, NULL),
(5, NULL, 1, 'Le Mans 24 hour race', 'Event', 'Le Mans', 'France', 1, 47.956221, 0.207828, 0, NULL),
(6, NULL, 1, 'Great Pyramid of Giza', 'Attraction', 'Giza', 'Egypt', 1, 29.979481, 31.134159, 1, NULL),
(7, NULL, 1, 'Niagara Falls', 'Natural Spot', 'New York', 'United States', 0, 29.979481, 31.134159, 0, NULL),
(8, NULL, 1, 'Vatican City', 'Country', 'Rome', 'Vatican City', 0, 41.903133, 12.454341, 0, NULL),
(9, NULL, 1, 'Lincoln Memorial', 'Monument', 'Washington DC', 'United States', 0, 38.889406, -77.050155, 1, NULL),
(10, NULL, 1, 'Auckland', 'Town', 'Northland', 'New Zealand', 1, -36.857977621977284, 174.76421311439333, 1, NULL),
(11, NULL, 1, 'Dunedin', 'Town', 'Otago', 'New Zealand', 1, -45.8933166311672, 170.50647588515426, 1, NULL),
(12, NULL, 1, 'Bluff', 'Town', 'Southland', 'New Zealand', 1, -46.60075957529487, 168.34655027490953, 1, NULL),
(13, NULL, 1, 'Wanaka', 'Town', 'Queenstown Lakes', 'New Zealand', 1, -44.703242638828584, 169.1327502437765, 1, NULL),
(14, NULL, 1, 'Masterton', 'Town', 'Wairarapa', 'New Zealand', 1, -40.9549210178137, 175.6605914889867, 1, NULL),
(15, NULL, 1, 'Castlepoint', 'Town', 'Wairarapa', 'New Zealand', 1, -40.91199513908064, 176.2265855109639, 1, NULL),
(16, NULL, 1, 'Sydney', 'Town', 'New South Wales', 'New Zealand', 1, -33.918608452325465, 151.22067000223797, 1, NULL),
(17, NULL, 0, 'Gisborne', 'Town', 'Hawke''s Bay', 'New Zealand', 1, -38.64201030447191, 178.05072585611822, 0, NULL),
(18, NULL, 0, 'Lake Pukaki', 'Attraction', 'MacKenzie District', 'New Zealand', 1, -44.190364079904214, 170.1398079316018, 0, NULL),
(19, NULL, 0, 'Queenstown', 'Town', 'Queenstown Lakes', 'New Zealand', 1, -45.04401028410226, 168.70883298914157, 0, NULL),
(20, NULL, 0, 'Cromwell', 'Town', 'Central Otago', 'New Zealand', 1, -45.057368951338255, 169.22068037212568, 0, NULL);

-- Traveller Types
-- Order will look a bit weird on phpMyAdmin as it sorts alphabetically by default
-- The tests are hard coded to this order
INSERT INTO `traveller_type` (`ttypeid`, `traveller_type_name`) VALUES
(1, 'Groupie'),
(2, 'Thrillseeker'),
(3, 'Gap Year'),
(4, 'Frequent Weekender'),
(5, 'Holidaymaker'),
(6, 'Business Traveller'),
(7, 'Backpacker');

-- Destination_Traveller_Type
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

-- treasure_hunt
INSERT INTO `treasure_hunt` (`thuntid`, `title`, `riddle`, `destination_destid`, `start_date`, `end_date`, `user`) VALUES
(1, 'Surprise', 'The garden city', 1, '2019-04-17', '2019-12-25', 2),
(2, 'Surprise2', 'Prime example of inflation', 3, '2019-04-17', '2019-12-25', 3),
(3, 'Closed Treasure Hunt', 'You should not be able to view this', 4, '2019-04-17', '2019-04-25', 4),
(4, 'Home sweet home (for me)', 'Lakes, mountains and far too many tourists', 13, '2019-08-07', '2019-12-06', 2),
(5, 'A fruity tale', 'Home of the giant fruit', 20, '2019-08-23', '2019-12-21', 2),
(6, 'Gardens and a hill if you''re lucky', 'The garden city', 1, '2019-08-09', '2019-11-22', 2),
(7, 'A city by the sea', 'Percieved as cold and wet but the burning couches will keep you warm', 11, '2019-08-23', '2019-11-23', 2),
(8, 'The big apple (For NZ)', 'A tower that touches the sky among traffic congestions', 10, '2019-08-07', '2019-09-01', 2),
(9, 'Sunny place across the ditch', 'Home to Google and Atlassian', 16, '2019-08-08', '2021-07-03', 2),
(10, 'A lake below the tallest mountain', 'Blue and pure below Aoraki', 18, '2019-07-30', '2020-10-21', 2),
(11, 'A town by the beach', 'Beside Castlerock, a lighthouse and a submarine shaped rock', 15, '2019-07-30', '2020-10-20', 2),
(12, 'A holiday destination near Christchurch', 'Hot pools and mountainous country', 4, '2019-08-08', '2020-02-20', 3),
(13, 'The most beautiful town in New Zealand?', 'Home of the golden shears', 14, '2019-08-05', '2019-11-28', 3);


-- trip
INSERT INTO `trip` (`tripid`, `user`, `is_public`, `trip_name`, `removed_visits`) VALUES
(1, 2, 1, 'Trip to New Zealand', 0),
(2, 2, 0, 'Christchurch to Wellington, to The Wok and back', 1),
(3, 3, 1, 'World Tour', 0),
(4, 3, 0, 'Pyramid to Race and back again', 0),
(5, 4, 1, 'See the pope, the president and come back', 0),
(6, 4, 0, 'Waterfall walk and see the president', 0),
(7, 2, 0, 'Length of NZ Trip', 0),
(11, 2, 0, 'Trip Home to Wanaka', 0),
(13, 2, 0, 'Atlassian Interview in Sydney', 0),
(14, 2, 0, 'Surfing trip in Gisborne', 0),
(15, 2, 0, 'Swim Meet at Moana Pool', 0),
(16, 2, 0, 'Stay at Bluff Marae', 0),
(17, 2, 0, 'Triathlon in Cromwell', 0);


-- user_nationality
INSERT INTO `user_nationality` (`user_userid`, `nationality_natid`) VALUES
(1, 1),
(1, 2),
(2, 251),
(3, 71),
(3, 72),
(4, 51),
(5, 1),
(5, 37),
(6, 59),
(7, 182),
(8, 183),
(9, 206),
(10, 180),
(11, 175);


-- user_passport
INSERT INTO `user_passport` (`user_userid`, `passport_passid`) VALUES
(2, 251),
(3, 71),
(3, 72),
(5, 1),
(5, 44),
(6, 44),
(7, 244),
(8, 9),
(9, 245),
(10, 204),
(11, 188);


-- user_traveller_type
INSERT INTO `user_traveller_type` (`user_userid`, `traveller_type_ttypeid`) VALUES
(1, 5),
(2, 3),
(3, 2),
(4, 1),
(4, 2),
(5, 1),
(5, 2),
(5, 3),
(6, 3),
(6, 5),
(7, 1),
(7, 2),
(8, 1),
(8, 3),
(9, 5),
(9, 7),
(10, 5),
(10, 7),
(11, 2),
(11, 6);

-- user_treasure_hunt
-- no data


-- user_photo
-- set in code



-- user_photo_destination
-- no data

-- Event
INSERT INTO `event` (`event_id`, `external_id`, `name`, `address`, `type`, `image_url`, `url`, `latitude`, `longitude`, `description`, `start_time`, `end_time`) VALUES
(1, 589921, 'Mel Parsons', '3 Garlands Road, Woolston, Christchurch', 'Folk', 'http://cdn.eventfinda.co.nz/uploads/events/transformed/1329367-589921-7.jpg?v=3',
 'https://www.eventfinda.co.nz/2019/mel-parsons/christchurch', -43.5568, 172.6803,
 'Needing little introduction on these shores, award-winning singer songwriter Mel Parsons is hitting the road with her new album ''Glass Heart'' for an intimate solo tour of some her favourite New Zealand venues. ...', '2019-09-26 08:00:00', '2019-09-26 11:59:59'),
(2, 547427, 'Songwriters In the Round', '185 Manchester St, Christchurch',
 'Singer-Songwriter', 'http://cdn.eventfinda.co.nz/uploads/events/transformed/1228501-547427-7.jpg', 'https://www.eventfinda.co.nz/2019/songwriters-in-the-round/christchurch',
 -43.5326, 172.6395, 'Three singer-songwriters share their original songs acoustically and the stories behind their music.',
 '2019-02-28 06:00:00', '2019-11-28 09:00:00');

-- Event responses
INSERT INTO `event_response` (`event_response_id`, `response_type`, `user_userid`, `event_event_id`, `response_date_time`) VALUES
(1, '2', 5, 2, '2019-09-25 04:04:15'),
(2, '2', 6, 2, '2019-09-25 05:08:34'),
(3, '1', 7, 2, '2019-09-25 05:08:50'),
(4, '0', 8, 2, '2019-09-25 05:11:21'),
(5, '1', 9, 2, '2019-09-25 05:11:56'),
(6, '2', 10, 2, '2019-09-25 05:08:34'),
(7, '1', 11, 2, '2019-09-25 05:08:50'),
(8, '0', 12, 2, '2019-09-25 05:11:21'),
(9, '2', 13, 2, '2019-09-25 05:08:34'),
(10, '1', 14, 2, '2019-09-25 05:08:50'),
(11, '0', 15, 2, '2019-09-25 05:11:21'),
(12, '0', 16, 2, '2019-09-25 05:11:21'),
(13, '2', 3, 2, '2019-09-25 05:08:34'),
(14, '1', 4, 2, '2019-09-25 05:08:50'),
(15, '0', 2, 2, '2019-09-25 04:04:15');


-- tags
INSERT INTO `tag` (`tag_id`, `name`) VALUES
(7, '#Culture'),
(6, '#LengthOfNZ'),
(4, 'Best trip ever'),
(1, 'Fun place to stay'),
(3, 'Top Rated'),
(2, 'Vacation spot');

-- pending users
INSERT INTO `tag_user`(`tag_tag_id`,`user_userid`) VALUES
(2,2);

-- destination_tag
INSERT INTO `destination_tag`(`destination_destid`, `tag_tag_id`) VALUES
(1,1),
(2,2),
(1,3);

-- trip_tag
INSERT INTO `trip_tag` (`trip_tripid`, `tag_tag_id`) VALUES
(1, 4),
(7, 6),
(16, 7);

-- visit
INSERT INTO `visit` (`visitid`, `visitorder`, `destination`, `trip`, `arrival`, `departure`, `visit_name`) VALUES
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
(17, 2, 9, 6, NULL, NULL, 'Lincoln Memorial'),
(18, 0, 12, 7, NULL, NULL, 'Bluff'),
(19, 2, 18, 7, NULL, NULL, 'Lake Pukaki'),
(20, 3, 2, 7, NULL, NULL, 'Wellington'),
(21, 4, 14, 7, NULL, NULL, 'Masterton'),
(22, 5, 10, 7, '2019-08-31', '2019-09-01', 'Auckland'),
(23, 6, 1, 7, NULL, NULL, 'Christchurch'),
(28, 0, 1, 11, NULL, NULL, 'Christchurch'),
(29, 2, 18, 11, '2019-08-31', '2019-08-31', 'Lake Pukaki'),
(30, 3, 13, 11, '2019-08-31', NULL, 'Wanaka'),
(32, 0, 13, 13, NULL, NULL, 'Wanaka'),
(33, 2, 19, 13, NULL, NULL, 'Queenstown'),
(34, 3, 16, 13, NULL, NULL, 'Sydney'),
(35, 4, 1, 13, NULL, NULL, 'Christchurch'),
(36, 0, 1, 14, NULL, NULL, 'Christchurch'),
(37, 2, 17, 14, NULL, NULL, 'Gisborne'),
(38, 3, 10, 14, NULL, NULL, 'Auckland'),
(39, 4, 1, 14, NULL, NULL, 'Christchurch'),
(40, 0, 13, 15, NULL, NULL, 'Wanaka'),
(41, 2, 11, 15, NULL, NULL, 'Dunedin'),
(42, 3, 13, 15, NULL, NULL, 'Wanaka'),
(43, 0, 13, 16, NULL, NULL, 'Wanaka'),
(44, 2, 19, 16, NULL, NULL, 'Queenstown'),
(45, 3, 12, 16, NULL, NULL, 'Bluff'),
(46, 4, 13, 16, '', NULL, 'Wanaka'),
(47, 0, 13, 17, NULL, NULL, 'Wanaka'),
(48, 2, 20, 17, NULL, NULL, 'Cromwell'),
(49, 3, 13, 17, NULL, NULL, 'Wanaka');

-- -------------------------------------------------


# --- !Downs
-- Delete in reverse order to the order data was added to avoid violating
-- foreign key constraints




delete from follow;

delete from visit;

delete from destination_tag;

delete from media_tag;

delete from trip_tag;

delete from tag_user;

delete from tag;

delete from event_response;

delete from event;

delete from album_media;

delete from destination_media;

delete from media;

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

delete from album;

delete from event_response;

delete from event;

delete from destination;

delete from admin;

delete from user;

delete from nationality;

delete from passport;