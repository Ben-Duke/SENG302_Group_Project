# !Ups

insert into traveller_type (traveller_type_name)
values ('Groupie'),
        ('Thrillseeker'),
        ('Gap Year'),
        ('Frequent Weekender'),
        ('Holidaymaker'),
        ('Business Traveller'),
        ('Backpacker');

insert into nationality (country_valid, nationality_name)
values ('1', 'Test');

# !Downs

delete from admin;

delete from destination;

delete from destination_traveller_type;

delete from destination_modification_request;

delete from destination_modification_request_traveller_type;

delete from nationality;

delete from passport;

delete from traveller_type;

delete from treasure_hunt;

delete from trip;

delete from user;

delete from user_nationality;

delete from user_passport;

delete from user_traveller_type;

delete from user_treasure_hunt;

delete from user_photo;

delete from user_photo_destination;

delete from visit;
