import controllers.ProfileController;
import controllers.TravellerTypeController;
import models.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDatabaseManager {

    public void populateDatabase(){
        addTravellerTypes();
        addNationalitiesAndPassports();
        populateUsers();
    }

    public void populateUsers(){
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

    public void addTravellerTypes(){
        TravellerTypeController tTC = new TravellerTypeController();
        tTC.addTravelTypes();
    }

    public void createDefaultAdmin(){
        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        user.save();
        Admin admin = new Admin(user.userid, true);
        admin.save();
    }

    public void addNationalitiesAndPassports(){
        ProfileController pC = new ProfileController();
        pC.addNatandPass();
    }
}
