package utilities;

import models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Test Database Manager class. Populates the database. NOTE: Does not create the database, so it requires the database to already be running.
 * Visit https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/Test-database-structure
 * for information on the layout of the test database.
 */
public class TestDatabaseManager {

    public TestDatabaseManager(){

    }

    /**
     * Populates the database. Call this method at the before section of each unit test.
     */
    public static void populateDatabase(){
        addTravellerTypes();
        addNationalitiesAndPassports();
        populateUsers();
        addTrips();
        addDestinationsAndVisits();
    }

    /**
     * Populates the database with users. (requires addTravellerTypes and addNationalitiesAndPassports to be called beforehand)
     */
    public static void populateUsers(){
        createDefaultAdmin();
        //Groupie
        TravellerType travellerType1 = TravellerType.find.query().where().eq("travellerTypeName","Groupie").findOne();
        //Thrillseeker
        TravellerType travellerType2 = TravellerType.find.query().where().eq("travellerTypeName","Thrillseeker").findOne();
        //Gap year
        TravellerType travellerType3 = TravellerType.find.query().where().eq("travellerTypeName","Gap Year").findOne();

        String natPassName1 = "New Zealand";
        String natPassName2 = "Singapore";
        String natPassName3 = "Australia";

        Nationality nationality1 = Nationality.find.query().where().eq("nationalityName",natPassName1).findOne();
        Nationality nationality2 = Nationality.find.query().where().eq("nationalityName",natPassName2).findOne();
        Nationality nationality3 = Nationality.find.query().where().eq("nationalityName",natPassName3).findOne();

        Passport passport1 = Passport.find.query().where().eq("passportName",natPassName1).findOne();
        Passport passport2 = Passport.find.query().where().eq("passportName",natPassName2).findOne();
        Passport passport3 = Passport.find.query().where().eq("passportName",natPassName3).findOne();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        LocalDate birthDate1 = LocalDate.parse("1998-08-23", formatter);
        LocalDate birthDate2 = LocalDate.parse("1960-12-25", formatter);
        LocalDate birthDate3 = LocalDate.parse("2006-06-09", formatter);

        User user = new User("testuser1@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate1, "Male");
        User user2 = new User("testuser2@uclive.ac.nz", "hunter22", "Caitlyn", "Jenner", birthDate2, "Female");
        User user3 = new User("testuser3@uclive.ac.nz", "hunter22", "John", "Smith", birthDate3, "Male");

        user.addTravellerType(travellerType3);

        user.getNationality().add(nationality1);
        user.getNationality().add(nationality2);

        user.getPassport().add(passport1);
        user.getPassport().add(passport2);

        user.save();

        user2.getTravellerTypes().add(travellerType2);

        user2.getNationality().add(nationality3);

        user2.getPassport().add(passport3);

        user2.save();

        user3.getTravellerTypes().add(travellerType1);
        user3.getTravellerTypes().add(travellerType2);

        user3.getNationality().add(nationality1);

        user3.save();
    }

    /**
     * Populates the database with traveller types.
     */
    public static void addTravellerTypes(){
        UtilityFunctions.addTravellerTypes();
    }

    /**
     * Creates a default admin.
     */
    public static void createDefaultAdmin(){
        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        user.save();
        Admin admin = new Admin(user.userid, true);
        admin.save();
    }

    /**
     * Populates the database with nationalities and pasports.
     */
    public static void addNationalitiesAndPassports(){
        UtilityFunctions.addNatAndPass();
    }


    /**
     * Populates the database with destinations added to users 2,3 and 4.
     * The destinations are then used to create visits for trips 1,2,3,4,5 and 6.
     */
    public static void addDestinationsAndVisits() {
        // Adds destinations for user2
        Destination destination1 = new Destination(
                "Christchurch", "Town", "Canterbury", "New Zealand", -43.5321, 172.6362, User.find.byId(2));
        destination1.save();

        Destination destination2 = new Destination(
                "Wellington", "Town", "Wellington", "New Zealand", -41.2866, 174.7756, User.find.byId(2));
        destination2.save();

        Destination destination3 = new Destination(
                "The Wok", "Cafe/Restaurant", "Canterbury", "New Zealand", -43.523593, 172.582971, User.find.byId(2));
        destination3.save();


        // Adds destinations for user3
        Destination destination4 = new Destination(
                "Hanmer Springs Thermal Pools", "Attraction", "North Canterbury", "New Zealand", -42.522791, 172.828944, User.find.byId(3));
        destination4.save();

        Destination destination5 = new Destination(
                "Le Mans 24 hour race", "Event", "Le Mans", "France", 47.956221, 0.207828, User.find.byId(3));
        destination5.save();
        Destination destination6 = new Destination(
                "Great Pyramid of Giza", "Attraction", "Giza", "Egypt", 29.979481, 31.134159, User.find.byId(3));
        destination6.save();

        //Adds destinations for user4
        Destination destination7 = new Destination(
                "Niagara Falls", "Natural Spot", "New York", "United States", 29.979481, 31.134159, User.find.byId(4));
        destination7.save();
        Destination destination8 = new Destination(
                "Vatican City", "Country", "Rome", "Vatican City", 41.903133, 12.454341, User.find.byId(4));
        destination8.save();
        Destination destination9 = new Destination(
                "Lincoln Memorial", "Monument", "Washington DC", "United States", 38.889406, -77.050155, User.find.byId(4));
        destination9.save();


        Trip trip1 = Trip.find.query().where().eq("tripName", "Trip to New Zealand").findOne();
        Trip trip2 = Trip.find.query().where().eq("tripName", "Christchurch to Wellington, to The Wok and back").findOne();
        Trip trip3 = Trip.find.query().where().eq("tripName", "World Tour").findOne();
        Trip trip4 = Trip.find.query().where().eq("tripName", "Pyramid to Race and back again").findOne();
        Trip trip5 = Trip.find.query().where().eq("tripName", "See the pope, the president and come back").findOne();
        Trip trip6 = Trip.find.query().where().eq("tripName", "Waterfall walk and see the president").findOne();

        new Visit("2018-05-04", "2018-05-06", trip1, destination1).save();
        new Visit("2018-05-06", "2018-05-08", trip1, destination2).save();

        new Visit(null, null, trip2, destination1).save();
        new Visit(null, null, trip2, destination2).save();
        new Visit(null, null, trip2, destination3).save();
        new Visit( null, null, trip2, destination1).save();


        new Visit("2003-08-12", null, trip3, destination4).save();
        new Visit(null, null, trip3, destination5).save();
        new Visit( null, null, trip3, destination6).save();

        new Visit(null, "2019-04-05", trip4, destination6).save();
        new Visit(null, null, trip4, destination5).save();
        new Visit( null, null, trip4, destination6).save();

        new Visit(null, null, trip5, destination8).save();
        new Visit(null, null, trip5, destination9).save();
        new Visit( null, null, trip5, destination8).save();

        new Visit(null, null, trip6, destination7).save();
        new Visit(null, null, trip6, destination9).save();



    }

    /**
     * Populates the databsae with trips added to users 2,3 and 4.
     */
    public static void addTrips(){
        //Add trips for user2
        Trip trip1 = new Trip("Trip to New Zealand", true, User.find.byId(2));
        trip1.save();
        Trip trip2 = new Trip("Christchurch to Wellington, to The Wok and back", false, User.find.byId(2));
        trip2.save();

        // Add trips to user 3
        Trip trip3 = new Trip("World Tour", true, User.find.byId(3));
        trip3.save();
        Trip trip4 = new Trip("Pyramid to Race and back again", false, User.find.byId(3));
        trip4.save();

        //Add trips to user 4
        Trip trip5 = new Trip("See the pope, the president and come back", true, User.find.byId(4));
        trip5.save();
        Trip trip6 = new Trip("Waterfall walk and see the president", false, User.find.byId(4));
        trip6.save();

    }

}
