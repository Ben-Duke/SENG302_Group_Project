package factories;

import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import formdata.UpdateUserFormData;
import formdata.UserFormData;
import models.*;
import models.commands.General.UndoableCommand;
import models.commands.Photos.EditPhotoCaptionCommand;
import play.mvc.Http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class UserFactory {

    public UserFactory(){//Just used to instantiate
    }

    /**Returns 1 if in the database and 0 if not in the database
     *
     * @return 0 if email is not present or 1 if email is present.
     */
    public static int checkEmail(String email) {

        List<User> users = User.find.all();

        int present = 0;
        String userEmail;
        for (User user : users) {

            userEmail = user.getEmail();
            if (userEmail.equalsIgnoreCase(email)) {
                present = 1;
            }
        }

        return present;

    }

    public void deletePhoto(int photoId){
         UserPhoto.deletePhoto(photoId);
    }

    public static void deleteNatsOnUser(int id, String nationalityId) {
        User user = UserAccessor.getById(id);
        if (user == null) {
            return;
        }

        try {
            Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityId));
            user.deleteNationality(nationality);
            user.update();
        } catch (NumberFormatException e) {
            // Not sure why we're catching this
        }
    }
    /** Returns a User object from a userId int.
     *
     * @param userId An int representing the userId to search for.
     * @return A User object with the userId, or null  if doesn't exist.
     */
    static User getUserFromId(int userId) {
        return UserAccessor.getById(userId);
    }

    /**Get a list of all passports.
     *
     * @return a list of a Passports from the backend in a Map<String, Boolean> format
     */
    public static  Map<String, Boolean> getPassports(){
        List<Passport> passports = Passport.find.all();

        SortedMap<String, Boolean> passportList = new TreeMap<>();
        for (Passport passport : passports) {

            String localeName;
            localeName = passport.passportName;
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
        for (TravellerType tType : tTypes) {

            String localeName;
            localeName = tType.getTravellerTypeName();
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
        for (Nationality aNationalityList : nationalityList) {

            String localeName;
            localeName = aNationalityList.getNationalityName();
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
                    // Do nothing, duplicate not inserted
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
                    // Do nothing, duplicate not inserted
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
                    // Do nothing, duplicate not inserted
                }
            }
        }
    }

    /**Returns the id of the Passport with the name passed in.
     *
     * @return Passport id with the name passed in.
     */
    public static int getPassportId(String name){
        List<Passport> passports = Passport.find.all();

        int id = -1;
        String passportName;
        for (Passport passport : passports) {

            passportName = passport.passportName;

            if (passportName.equals(name)) {
                id = passport.getPassportId();
            }
        }

        return id;
    }

    /**Returns the id of the Nationality with the name passed in.
     *
     * @return Nationality id with the name passed in.
     */
    private static int getNatId(String name){
        List<Nationality> nationalities = Nationality.find.all();

        int id = -1;
        String natName;
        for (Nationality nationality : nationalities) {

            natName = nationality.nationalityName;

            if (natName.equals(name)) {
                id = nationality.natid;
            }
        }

        return id;
    }

    /**Returns the id of the Traveller Type with the name passed in.
     *
     * @return Traveller Type id with the name passed in.
     */
    private static int getTTypeId(String name){
        List<TravellerType> tTypes = TravellerType.find.all();

        int id = -1;
        String tName;
        for (TravellerType tType : tTypes) {

            tName = tType.getTravellerTypeName();

            if (tName.equals(name)) {
                id = tType.getTtypeid();
            }
        }

        return id;
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
            for (String aTType : tType) {

                int tTypeId = getTTypeId(aTType);
                updateTravellerType(user, tTypeId);
            }
            //Passport loop
            if (passports != null) {
                for (String passport : passports) {

                    int passportId = getPassportId(passport);
                    updatePassport(user, passportId);
                }
            }


            for (String nationality : nationalities) {

                int natId = getNatId(nationality);
                updateNationality(user, natId);
            }


            return user.getUserid();
        }
        return -1;
    }

    public static int getNatsForUserbyId(int userId){
        int count = 0;
        User user = UserAccessor.getById(userId);
        if (user != null) {
            count = user.nationality.size();
        }
        return count;
    }

    public static List<Passport> getUserPassports(int id){
        return UserAccessor.getById(id).passports;
    }

    public static List<Nationality> getUserNats(int id){
        return UserAccessor.getById(id).nationality;
    }

    public static void addPassportToUser(int id, String passportId){

        Passport passport = Passport.find.byId(Integer.parseInt(passportId));

        try {
            User user = UserAccessor.getById(id);
            if (user == null) {
                return;
            }

            user.addPassport(passport);
            user.update();
        } catch (io.ebean.DuplicateKeyException e) {
            // Do nothing since the duplicate will not be inserted
        }
    }

    public static void deletePassportOnUser(int id, String passportId){
        try {
            Passport passport = Passport.find.byId(Integer.parseInt(passportId));
            User user = UserAccessor.getById(id);
            if (user == null) {
                return;
            }
            user.deletePassport(passport);
            user.update();
        } catch (NumberFormatException e) {
            // Not sure why we are catching this
        }
    }

    public static void addNatsOnUser(int id, String nationalityId){
        User user = UserAccessor.getById(id);
        if (user == null) {
            return;
        }

        try {
            Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityId));
            user.addNationality(nationality);
            user.update();
        } catch (io.ebean.DuplicateKeyException e) {
            // do nothing since the duplicate will not be inserted
        }
    }



    /**
     * Get the user's profile picture if it exists
     * @param userId the user id of the user whose profile picture is to be retrieved
     * @return the UserPhoto that is the profile picture if it exists, otherwise null
     */
    public static UserPhoto getUserProfilePicture(int userId) {
        User user = UserAccessor.getById(userId);
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
    private static void removeExistingProfilePicture(int userId) {
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

    public static UpdateUserFormData getUpdateUserFormDataForm(Http.Request request) {
        List<User> users = User.getCurrentUser(request, true);
        User user = users.get(0);

        boolean isAdmin = false;
        if (users.get(0).getUserid() != users.get(1).getUserid()) {
            isAdmin = true;
        }
        return new UpdateUserFormData(user, isAdmin);
    }

    /**
     * Sets the privacy of the picture given.
     * @param userId the user who is the owner of the picture
     * @param newPhoto the photo who's privacy is to be changed
     * @param setPublic true to make public, false to make private
     */
    public static void makePicturePublic(int userId, UserPhoto newPhoto, boolean setPublic) {
        User user = User.find.byId(userId);
        if (user != null) {
            newPhoto.setPublic(setPublic);
            newPhoto.save();
        }
    }

    /**
     * Changes a photos caption to the caption provided
     * @param userId The id of the user that is trying to change the photo
     * @param photoId The id of the photo with a caption to be changed
     * @param caption the caption that is going ot be the new caption
     */
    public static void editPictureCaption(int userId, int photoId, String caption) {
        User user = UserAccessor.getById(userId);
        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
        if (user != null && photo != null) {
            if (user.equals(photo.getUser()) || user.userIsAdmin()) {
                photo.setCaption(caption);
                UndoableCommand editCaptionCommand = new EditPhotoCaptionCommand(photo);
                user.getCommandManager().executeCommand(editCaptionCommand);
            } else {
                throw new IllegalArgumentException("Forbidden");
            }
        } else {
            throw new IllegalArgumentException("Not Found");
        }
    }
}
