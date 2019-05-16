package factories;
import controllers.ApplicationManager;
import formdata.UpdateUserFormData;
import formdata.UserFormData;
import models.*;
import io.ebean.ExpressionList;
import play.data.FormFactory;
import play.mvc.Http;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import javax.inject.Inject;

public class UserFactory {

    @Inject
    static FormFactory formFactory;

    public UserFactory(){//Just used to instanciate
    }


    /**Returns 1 if in the database and 0 if not in the database
     *
     * @param email
     * @return 0 if email is not present or 1 if email is present.
     */
    public static int checkEmail(String email) {

        List<User> users = User.find.all();

        int present = 0;
        String userEmail;
        for (int i = 0; i < users.size(); i++) {

            userEmail = users.get(i).getEmail();
            if(userEmail.equalsIgnoreCase(email)){
                present = 1;
            }
        }

        return present;

    }


    public static void deleteNatsOnUser(int id, String nationalityId) {
        User user = User.find.query().where().eq("userid", id).findOne();
        try {
            Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityId));
            user.deleteNationality(nationality);
            user.update();
        } catch (NumberFormatException e) {

        }
    }
    /** Returns a User object from a userId int.
     *
     * @param userId An int representing the userId to search for.
     * @return A User object with the userId, or null  if doesn't exist.
     */
    public static User getUserFromId(int userId) {
        User user = User.find.query().where().eq("userid", userId).findOne();
        return user;
    }

    /**Get a list of all passports.
     *
     * @return a list of a Passports from the backend in a Map<String, Boolean> format
     */
    public static  Map<String, Boolean> getPassports(){
        List<Passport> passports = Passport.find.all();

        SortedMap<String, Boolean> passportList = new TreeMap<>();
        for (int i = 0; i < passports.size(); i++) {

            String localeName;
            localeName = passports.get(i).passportName;
            passportList.put(localeName, false);
        }
        passportList.remove("");
        return passportList;

    }

    /**Get a list of all Traveller Types.
     *
     * @return a list of a Traveller Types from the backend in a Map<String, Boolean> format
     */
    public static Map<String, Boolean> getTTypesList() {
        List<TravellerType> tTypes = TravellerType.find.all();


        SortedMap<String, Boolean> tTypesList = new TreeMap<>();
        for (int i = 0; i < tTypes.size(); i++) {

            String localeName;
            localeName = tTypes.get(i).getTravellerTypeName();
            tTypesList.put(localeName, false);
        }
        tTypesList.remove("");
        return tTypesList;
    }

    /**Get a list of all Nationalities.
     *
     * @return a list of a Nationalities from the backend in a Map<String, Boolean> format
     */
    public static Map<String, Boolean> getNatList() {
        List<Nationality> nationalityList = Nationality.find.all();


        SortedMap<String, Boolean> nationalities = new TreeMap<>();
        for (int i = 0; i < nationalityList.size(); i++) {

            String localeName;
            localeName = nationalityList.get(i).getNationalityName();
            nationalities.put(localeName, false);
        }
        nationalities.remove("");
        return nationalities;
    }

    /**
     * Handles the request from the user to add passports to his profile
     * and.
     * @param user pass in the user that needs to have pass ports added
     * @param passportId this is the id of the pasport that needs to be added
     */
    public void updatePassport(User user, int passportId){
        if (user != null) {
            Passport passport = Passport.find.byId(passportId);
            if(passportId != -1){
                try {
                    user.addPassport(passport);
                    user.update();
                } catch (io.ebean.DuplicateKeyException e) {

                }
            }
        }
    }

    /**
     * Handles the request from the user to add Nationalities to his profile
     * and.
     * @param user pass in the user that needs to have pass ports added
     * @param natId this is the id of the pasport that needs to be added
     */
    public void updateNationality(User user, int natId){
        if (user != null) {
            Nationality nationality = Nationality.find.byId(natId);
            if(natId != -1){
                try {
                    user.addNationality(nationality);
                    user.update();
                } catch (io.ebean.DuplicateKeyException e) {

                }
            }
        }
    }

    /**
     * Handles the request from the user to add traveller types to his profile
     * and.
     * @param user pass in the user that needs to have pass ports added
     * @param  travellerId this is the id of the pasport that needs to be added
     */
    public void updateTravellerType(User user, int travellerId){
        if (user != null) {
            TravellerType travellerType = TravellerType.find.byId(travellerId);

            if( travellerId != -1){
                try {
                    user.addTravellerType(travellerType);
                    user.update();
                } catch (io.ebean.DuplicateKeyException e) {

                }
            }
        }
    }

    /**Returns the id of the Passport with the name passed in.
     *
     * @param name
     * @return Passport id with the name passed in.
     */
    public static int getPassportId(String name){
        List<Passport> passports = Passport.find.all();

        int id = -1;
        String passportName;
        for (int i = 0; i < passports.size(); i++) {

            passportName = passports.get(i).passportName;

            if(passportName.equals( name)){
                id = passports.get(i).getPassportId();
            }
        }

        return id;
    }

    /**Returns the id of the Nationality with the name passed in.
     *
     * @param name
     * @return Nationality id with the name passed in.
     */
    public static int getNatId(String name){
        List<Nationality> nationalities = Nationality.find.all();

        int id = -1;
        String natName;
        for (int i = 0; i < nationalities.size(); i++) {

            natName = nationalities.get(i).nationalityName;

            if(natName.equals( name)){
                id = nationalities.get(i).natid;
            }
        }

        return id;
    }

    /**Returns the id of the Traveller Type with the name passed in.
     *
     * @param name
     * @return Traveller Type id with the name passed in.
     */
    public static int getTTypeId(String name){
        List<TravellerType> tTypes = TravellerType.find.all();

        int id = -1;
        String tName;
        for (int i = 0; i < tTypes.size(); i++) {

            tName = tTypes.get(i).getTravellerTypeName();

            if(tName.equals( name)){
                id = tTypes.get(i).getTtypeid();
            }
        }

        return id;
    }


    public boolean checkpassword(String email, String password) {
        ExpressionList<User> usersExpressionList = User.find.query()
                .where().eq("email", email).and().eq("password", password);

        return usersExpressionList.findCount() == 1;
    }

    /**Tells model to create a user based on the data it is being passed
     *
     * @param userForm formdata containing primitive data type like int, String etc which has been passed from
     *                 the front end
     * @return returns the user Id that has been created
     */
    public int createUser(UserFormData userForm){

        String email = userForm.email;
        String password = userForm.password;
        String firstName = userForm.firstName;
        String lastName = userForm.lastName;
        String gender = userForm.gender;
        List<String> tType = userForm.travellerTypes;
        List<String> passports = userForm.passports;
        List<String> nationalities = userForm.nationalities;
        String dob =  userForm.dob;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dob, formatter);


        if(checkEmail(email)!=1){
            User user = new User(email, password, firstName, lastName, date, gender);


            user.save();
            for (int i = 0; i < tType.size(); i++) {

                int tTypeId = getTTypeId(tType.get(i));
                updateTravellerType(user, tTypeId);
            }
            //Passport loop
            if (passports != null) {
                for (int j = 0; j < passports.size(); j++) {

                    int passportId = getPassportId(passports.get(j));
                    updatePassport(user, passportId);
                }
            }


            for (int k = 0; k < nationalities.size(); k++) {

                int natId = getNatId(nationalities.get(k));
                updateNationality(user, natId);
            }


            return user.getUserid();
        }
        return -1;
    }

    public static int getNatsForUserbyId(int userId){
        int count = 0;
        User user = User.find.query().where().eq("userid", userId).findOne();
        count = user.nationality.size();
        return count;
    }

    public static List<Passport> getUserPassports(int id){
        //System.out.println(Passport.find.all().size());
        //System.out.println(Nationality.find.all().size());
        return User.find.query().where().eq("userid", id).findOne().passports;
    }

    public static List<Nationality> getUserNats(int id){
        return User.find.query().where().eq("userid", id).findOne().nationality;
    }

    public static void addPassportToUser(int id, String passportId){

        Passport passport = Passport.find.byId(Integer.parseInt(passportId));

        try {
            User user = User.find.query().where().eq("userid", id).findOne();
            user.addPassport(passport);
            user.update();
        } catch (io.ebean.DuplicateKeyException e) {
            //return unauthorized("Oops, you have already have this passport");
        }
    }

    public static void deletePassportOnUser(int id, String passportId){


        try {
            Passport passport = Passport.find.byId(Integer.parseInt(passportId));
            User user = User.find.query().where().eq("userid", id).findOne();
            user.deletePassport(passport);
            user.update();
        } catch (NumberFormatException e) {
            //return  unauthorized("Oops, you do not have any passports to delete");
        }
    }

    public static void addNatsOnUser(int id, String nationalityId){
        User user = User.find.query().where().eq("userid", id).findOne();
        try {
            Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityId));
            user.addNationality(nationality);
            user.update();
        } catch (io.ebean.DuplicateKeyException e) {
        }
    }



    /**
     * Get the user's profile picture if it exists
     * @param userId the user id of the user whose profile picture is to be retrieved
     * @return the UserPhoto that is the profile picture if it exists, otherwise null
     */
    public static UserPhoto getUserProfilePicture(int userId) {
        User user = User.find.query().where().eq("userid", userId).findOne();
        UserPhoto userPhoto = UserPhoto.find.query().where().eq("user", user).and().eq("isProfile", true).findOne();
        if(userPhoto != null) {
            return  userPhoto;
        } else {
            return null;
        }
    }

    /**
     * Remove the user's existing profile picture if it exists
     * @param userId the user id of the user whose profile picture is to be removed
     */
    public static void removeExistingProfilePicture(int userId) {
        UserPhoto existingProfile = getUserProfilePicture(userId);
        if (existingProfile != null) {
            existingProfile.setProfile(false);
            existingProfile.save();
        }
    }

    /**
     * Replace the user's existing profile picture with an new photo
     * @param userId the user id of the user whose profile picture is to be replaced
     * @param newPhoto the new photo that is to become the user's profile picture
     */
    public static void replaceProfilePicture(int userId, UserPhoto newPhoto) {
        if (!newPhoto.equals(getUserProfilePicture(userId))) {
            removeExistingProfilePicture(userId);
            newPhoto.setProfile(true);
            newPhoto.save();
        }
    }

    /**
     * Get the path to the User's profile picture
     * @param user the user whose profile picture path is to be retrieved
     * @return the path to the photo
     */
    public static String getProfilePhotoPath(User user) {
        return java.nio.file.Paths.get(".").toAbsolutePath().normalize().toString() + ApplicationManager.getUserPhotoPath() + user.getUserid() + "/profilethumbnail.png";
    }



    public static UpdateUserFormData getUpdateUserFormDataForm(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            return new UpdateUserFormData(user);
        } else {
            return null;
        }
    }

    /**
     * Sets the privacy of the picture given.
     * @param userId the user who is the owner of the picture
     * @param newPhoto the photo who's privacy is to be changed
     * @param setPublic true to make public, false to make private
     */
    public static void makePicturePublic(int userId, UserPhoto newPhoto, boolean setPublic) {
        User user = User.find.byId(userId);
        if (!user.equals(null)) {
            newPhoto.setPublic(setPublic);
            newPhoto.save();
        }
    }
}
