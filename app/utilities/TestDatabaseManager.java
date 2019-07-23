package utilities;

import accessors.AlbumAccessor;
import controllers.ApplicationManager;
import models.*;
import models.commands.Albums.CreateAlbumCommand;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Test Database Manager class. Populates the database. NOTE: Does not create the database, so it requires the database to already be running.
 * Visit https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/Test-database-structure
 * for information on the layout of the test database.
 */
public class TestDatabaseManager {

    public TestDatabaseManager(){

    }

    public static Database getTestDatabase() {
        Database database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));

        return database;
    }

    public static void shutdownTestDatabase(Database database) {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    /**
     * Method to populate the database when the application is first started.
     *
     *
     * @param initCompleteLatch A CountDownLatch to call back and unlock when the
     *                          database has been populated.
     */
    public void populateDatabase(CountDownLatch initCompleteLatch) {

        populateDatabase();
        initCompleteLatch.countDown();
    }

    /**
     * Populates the database. Call this method at the before section of each unit test.
     */
    public void populateDatabase() {

        boolean isInSuccessState = true;

        UtilityFunctions util = new UtilityFunctions();


        if(TravellerType.find.all().isEmpty()) {
            boolean successFullyAddedTravellerTypes = util.addTravellerTypes();

            if (! successFullyAddedTravellerTypes) {
                isInSuccessState = false;
            }
        }

        if (isInSuccessState && Nationality.find.all().isEmpty()) {

            boolean successfullyAddedAllNationalities = util.addAllNationalities();

            if (!successfullyAddedAllNationalities) {

                isInSuccessState = false;
            }
        }


        if (isInSuccessState && Passport.find.all().isEmpty()) {
            boolean successfullyAddedAllPassorts =  util.addAllPassports();
            if (! successfullyAddedAllPassorts) {
                isInSuccessState = false;
            }
        }

        if (isInSuccessState) {
            boolean successfullyAddedAdmin = this.createDefaultAdmin();
            if (! successfullyAddedAdmin) {
                isInSuccessState = false;
            }
        }

        if (isInSuccessState) {
            boolean successfullyAddedAllUsers = this.populateNormalUsers();
            if (! successfullyAddedAllUsers) {
                isInSuccessState = false;
            }
        }

        if (isInSuccessState) {
            boolean successfullyAddedAllTrips =  this.addTrips();
            if (! successfullyAddedAllTrips) {
                isInSuccessState = false;
            }
        }

        if (isInSuccessState) {
            boolean successfullyAddedDestTrips = this.addDestinationsAndVisits();
            if (! successfullyAddedDestTrips) {
                isInSuccessState = false;
            }

        }

        if (isInSuccessState) {
            this.addTreasureHunts();
        }

        if (isInSuccessState) {
            if(ApplicationManager.getUserMediaPath().equalsIgnoreCase("/test/resources/test_photos/user_")){
                this.addUserPhotos();
            }
        }

        CountryUtils.validateUsedCountries();

    }

    /**
     * Populates the database with users. (requires addTravellerTypes and addNationalitiesAndPassports to be called beforehand)
     *
     * @return A boolean, true if successfully added all normal users, else false
     */
    public boolean populateNormalUsers(){

        boolean isInSuccessState = true;
        try {
            //Groupie
            TravellerType travellerType1 = null;
            TravellerType travellerType2 = null;
            TravellerType travellerType3 = null;

            travellerType1 = TravellerType.find.query().where().eq("travellerTypeName","Groupie").findOne();
            //Thrillseeker
            travellerType2 = TravellerType.find.query().where().eq("travellerTypeName","Thrillseeker").findOne();
            //Gap year
            travellerType3 = TravellerType.find.query().where().eq("travellerTypeName","Gap Year").findOne();

            String natPassName1 = "New Zealand";
            String natPassName2 = "Singapore";
            String natPassName3 = "Australia";
            String invalidNatPassName1 = "Czechoslovakia";

            // Insert invalid nationality since normal insertions only include valid ones
            Nationality invalidNationality = new Nationality(invalidNatPassName1);
            invalidNationality.save();

            Passport invalidPassport = new Passport(invalidNatPassName1);
            invalidPassport.save();

            // --------------------fleshing out the sql query to debug


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
            LocalDate birthDate1 = LocalDate.parse("1998-08-23", formatter);
            LocalDate birthDate2 = LocalDate.parse("1960-12-25", formatter);
            LocalDate birthDate3 = LocalDate.parse("2006-06-09", formatter);

            User user = new User("testuser1@uclive.ac.nz", "test", "Gavin", "Ong", birthDate1, "Male");
            User user2 = new User("testuser2@uclive.ac.nz", "test", "Caitlyn", "Jenner", birthDate2, "Female");
            User user3 = new User("testuser3@uclive.ac.nz", "test", "John", "Smith", birthDate3, "Male");


            user.addTravellerType(travellerType3);


//            user.setNationality(Nationality.find.all().subList(0, 2));
            List<Nationality> nats = new ArrayList<>();
            nats.add(invalidNationality);
            user.setNationality(nats);

//            user.setPassport(Passport.find.all().subList(0, 2));
            List<Passport> pass = new ArrayList<>();
            pass.add(invalidPassport);
            user.setPassport(pass);

            try {
                user.save();
            }catch(Exception err){
                isInSuccessState = false;
                //System.out.printf("User1 failed");
                err.printStackTrace();
            }

//            addAlbums();
            user2.getTravellerTypes().add(travellerType2);

            user2.setNationality(Nationality.find.all().subList(70, 72));

            user2.setPassport(Passport.find.all().subList(70, 72));

            try{
                user2.save();
            }catch(Exception err){
                isInSuccessState = false;
                System.out.printf("User2 failed");
            }
            user3.getTravellerTypes().add(travellerType1);
            user3.getTravellerTypes().add(travellerType2);

            user3.setNationality(Nationality.find.all().subList(50, 51));

            try{
                user3.save();
            }catch(Exception err){
                isInSuccessState = false;
                System.out.printf("User1 failed");
            }


        } catch (Exception e) {

            System.out.println(e);

            isInSuccessState = false;
            System.out.println("Failed to create all users");
        }

        return isInSuccessState;
    }

    /**
     * Creates a default admin.
     *
     * @return A boolean, true if successfully created the admin, false otherwise
     */
    public boolean createDefaultAdmin(){
        boolean isInSuccessState = true;

        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        user.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically

        try {
            user.save();
        } catch (Exception e) {
            isInSuccessState = false;
            System.out.println("Error making admin: User is already in db");
        }

        if (isInSuccessState) {
            Admin admin = new Admin(user.userid, true);
            try {
                admin.save();
            } catch (Exception e) {
                isInSuccessState = false;
                System.out.println("Error making admin: Admin is already in db");
            }
        }

        return isInSuccessState;
    }


    /**
     * Populates the database with destinations added to users 2,3 and 4.
     * The destinations are then used to create visits for trips 1,2,3,4,5 and 6.
     *
     * @return A boolean, true if successfully added all destinations and visits.
     */
    public boolean addDestinationsAndVisits() {
        boolean isInSuccessState = true;

        // Adds destinations for user2
        Destination destination1 = new Destination(
                "Christchurch", "Town", "Canterbury",
                "New Zealand", -43.5321, 172.6362,
                User.find.byId(2));
        destination1.setIsPublic(true);
        destination1.addTravellerType(TravellerType.find.byId(1));

        Destination destination2 = new Destination(
                "Wellington", "Town", "Wellington",
                "New Zealand", -41.2866, 174.7756,
                User.find.byId(2));

        Destination destination3 = new Destination(
                "The Wok", "Cafe/Restaurant",
                "Canterbury", "New Zealand", -43.523593,
                172.582971, User.find.byId(2));
        destination3.setIsPublic(true);
        destination3.addTravellerType(TravellerType.find.byId(1));
        destination3.addTravellerType(TravellerType.find.byId(3));


        // Adds destinations for user3
        Destination destination4 = new Destination(
                "Hanmer Springs Thermal Pools", "Attraction",
                "North Canterbury", "New Zealand", -42.522791,
                172.828944, User.find.byId(3));
        destination4.setIsPublic(true);
        destination4.addTravellerType(TravellerType.find.byId(5));
        destination4.addTravellerType(TravellerType.find.byId(7));

        Destination destination5 = new Destination(
                "Le Mans 24 hour race", "Event",
                "Le Mans", "France", 47.956221,
                0.207828, User.find.byId(3));
        Destination destination6 = new Destination(
                "Great Pyramid of Giza", "Attraction",
                "Giza", "Egypt", 29.979481,
                31.134159, User.find.byId(3));
        destination6.setIsPublic(true);
        destination6.addTravellerType(TravellerType.find.byId(7));

        //Adds destinations for user4
        Destination destination7 = new Destination(
                "Niagara Falls", "Natural Spot",
                "New York", "United States", 29.979481,
                31.134159, User.find.byId(4));
        destination7.addTravellerType(TravellerType.find.byId(2));
        Destination destination8 = new Destination(
                "Vatican City", "Country", "Rome",
                "Vatican City", 41.903133, 12.454341,
                User.find.byId(4));
        Destination destination9 = new Destination(
                "Lincoln Memorial", "Monument",
                "Washington DC", "United States", 38.889406,
                -77.050155, User.find.byId(4));
        destination9.setIsPublic(true);
        destination9.addTravellerType(TravellerType.find.byId(1));
        destination9.addTravellerType(TravellerType.find.byId(4));
        destination9.addTravellerType(TravellerType.find.byId(6));


        // saving the destinations
        List<Destination> destinations = new ArrayList<Destination>();
        destinations.add(destination1);
        destinations.add(destination2);
        destinations.add(destination3);
        destinations.add(destination4);
        destinations.add(destination5);
        destinations.add(destination6);
        destinations.add(destination7);
        destinations.add(destination8);
        destinations.add(destination9);

        for (Destination destination: destinations) {
            try {
                destination.save();
            } catch (Exception e) {
                isInSuccessState = false;
                System.out.println(String.format("Failed to save destination " +
                                "(%s) due to uniqueness constraint fail",
                        destination.getDestName()));
            }
        }
        if (isInSuccessState) {
            //saving the destination albums
            AlbumAccessor.createAlbumFromDestination(destination1);
            AlbumAccessor.createAlbumFromDestination(destination2);
            AlbumAccessor.createAlbumFromDestination(destination3);
            AlbumAccessor.createAlbumFromDestination(destination4);
            AlbumAccessor.createAlbumFromDestination(destination5);
            AlbumAccessor.createAlbumFromDestination(destination6);
            AlbumAccessor.createAlbumFromDestination(destination7);
            AlbumAccessor.createAlbumFromDestination(destination8);
            AlbumAccessor.createAlbumFromDestination(destination9);
        }

        if (isInSuccessState) {
            // Gets the first trip with that name. This may have bad side effects.
            Trip trip1 = Trip.find.query().where().eq("tripName", "Trip to New Zealand").findList().get(0);
            Trip trip2 = Trip.find.query().where().eq("tripName", "Christchurch to Wellington, to The Wok and back").findList().get(0);
            Trip trip3 = Trip.find.query().where().eq("tripName", "World Tour").findList().get(0);
            Trip trip4 = Trip.find.query().where().eq("tripName", "Pyramid to Race and back again").findList().get(0);
            Trip trip5 = Trip.find.query().where().eq("tripName", "See the pope, the president and come back").findList().get(0);
            Trip trip6 = Trip.find.query().where().eq("tripName", "Waterfall walk and see the president").findList().get(0);

            // creating all the visits
            Visit visit1 =  new Visit("2018-05-04", "2018-05-06", trip1, destination1,1);
            Visit visit2 =  new Visit("2018-05-06", "2018-05-08", trip1, destination2,2);

            Visit visit3 =  new Visit(null, null, trip2, destination1,1);
            Visit visit4 =  new Visit(null, null, trip2, destination2,2);
            Visit visit5 =  new Visit(null, null, trip2, destination3,3);
            Visit visit6 =  new Visit( null, null, trip2, destination1,4);

            Visit visit7 =  new Visit("2003-08-12", null, trip3, destination4,1);
            Visit visit8 =  new Visit(null, null, trip3, destination5,2);
            Visit visit9 =  new Visit( null, null, trip3, destination6,3);

            Visit visit10 =  new Visit(null, "2019-04-05", trip4, destination6,1);
            Visit visit11 =  new Visit(null, null, trip4, destination5,2);
            Visit visit12 =  new Visit( null, null, trip4, destination6,3);

            Visit visit13 =  new Visit(null, null, trip5, destination8,1);
            Visit visit14 =  new Visit(null, null, trip5, destination9,2);
            Visit visit15 =  new Visit( null, null, trip5, destination8,3);

            Visit visit16 =  new Visit(null, null, trip6, destination7,1);
            Visit visit17 =  new Visit(null, null, trip6, destination9,2);

            // saving all visits to database
            List<Visit> visits = new ArrayList<Visit>();
            visits.add(visit1);
            visits.add(visit2);
            visits.add(visit3);
            visits.add(visit4);
            visits.add(visit5);
            visits.add(visit6);
            visits.add(visit7);
            visits.add(visit8);
            visits.add(visit9);
            visits.add(visit10);
            visits.add(visit11);
            visits.add(visit12);
            visits.add(visit13);
            visits.add(visit14);
            visits.add(visit15);
            visits.add(visit16);
            visits.add(visit17);

            for (Visit visit: visits) {
                try {
                    visit.save();
                } catch (Exception e) {
                    isInSuccessState = false;
                    String visitSaveErrorFormat = "Failed to save visit (%s) due to" +
                            " uniqueness constraint fail";
                    String errorStr;
                    errorStr = String.format(visitSaveErrorFormat, visit.getVisitName());

                    System.out.println(errorStr);
                }
            }
        } else {
            isInSuccessState = false;
        }

        return isInSuccessState;
    }

    /**
     * Populates the databsae with trips added to users 2,3 and 4.
     *
     * @return A boolean, true if successfully added all trips, false otherwise.
     */
    public boolean addTrips(){
        boolean isInSuccessState = true;

        //Add trips for user2
        Trip trip1 = new Trip("Trip to New Zealand", true, User.find.byId(2));
        Trip trip2 = new Trip("Christchurch to Wellington, to The Wok and back", false, User.find.byId(2));

        // Add trips to user 3
        Trip trip3 = new Trip("World Tour", true, User.find.byId(3));
        Trip trip4 = new Trip("Pyramid to Race and back again", false, User.find.byId(3));

        //Add trips to user 4
        Trip trip5 = new Trip("See the pope, the president and come back", true, User.find.byId(4));
        Trip trip6 = new Trip("Waterfall walk and see the president", false, User.find.byId(4));

        List<Trip> trips = new ArrayList<Trip>();
        trips.add(trip1);
        trips.add(trip2);
        trips.add(trip3);
        trips.add(trip4);
        trips.add(trip5);
        trips.add(trip6);

        for (Trip trip: trips) {
            try {
                trip.save();
            } catch (Exception e) {
                isInSuccessState = false;
                System.out.println("Failed to save trip for user: " +
                        trip.getUser().getEmail() + " with trip name: " +
                        trip.getTripName() + " due to uniqueness constraint fail");
            }
        }

        return isInSuccessState;
    }

    public void addUserPhotos(){
        UserPhoto userPhoto1 = new UserPhoto("shrek.jpeg", true, true, User.find.byId(2));
        UserPhoto userPhoto2 = new UserPhoto("placeholder.png", false, false, User.find.byId(2));
//        Destination christchurch = Destination.find.byId(1);
//        Destination wellington = Destination.find.byId(2);
//        userPhoto1.addDestination(christchurch);
//        userPhoto1.addDestination(wellington);
        try {
            userPhoto1.save();
        } catch (Exception e) {
            System.out.println("Failed to add user1 photos");
        }

        try {
            userPhoto2.save();
        } catch (Exception e) {
            System.out.println("Failed to add user2 photos");
        }
    }

    public void addAlbums(){
        UserPhoto userPhoto1 = new UserPhoto("card.PNG", true, false, User.find.byId(1));
        UserPhoto userPhoto2 = new UserPhoto("Capture.PNG", false, false, User.find.byId(1));
        Album album1 = new Album(User.find.byId(1), "myAlbum");
        try {
            userPhoto1.save();
            userPhoto2.save();

            album1.addMedia(User.find.byId(1).getUserPhotos().get(0));
            album1.addMedia(User.find.byId(1).getUserPhotos().get(1));
            System.out.println(album1.media);
            album1.save();
        } catch (Exception e) {
            System.out.println("Failed to add album1 photos");
        }

    }

    public void addTreasureHunts(){
        TreasureHunt treasureHunt1 = new TreasureHunt("Surprise", "The garden city", Destination.find.byId(1), "2019-04-17", "2019-12-25", User.find.byId(2));
        treasureHunt1.save();
        TreasureHunt treasureHunt2 = new TreasureHunt("Surprise2", "Prime example of inflation", Destination.find.byId(3), "2019-04-17", "2019-12-25", User.find.byId(3));
        treasureHunt2.save();
        TreasureHunt treasureHunt3 = new TreasureHunt("Closed Treasure Hunt", "You should not be able to view this", Destination.find.byId(4), "2019-04-17", "2019-04-25", User.find.byId(4));
        treasureHunt3.save();
    }



}
