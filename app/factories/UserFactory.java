package factories;
import controllers.routes;
import formdata.UpdateUserFormData;
import formdata.UserFormData;
import io.ebean.Update;
import models.Nationality;
import models.Passport;
import models.TravellerType;
import io.ebean.ExpressionList;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class UserFactory {
    private static Logger logger = LoggerFactory.getLogger("application");

    @Inject
    static FormFactory formFactory;

    public UserFactory(){//Just used to instanciate
         }


    public boolean checkpassword(String email, String password) {
        ExpressionList<User> usersExpressionList = User.find.query()
                .where().eq("username", email).and().eq("password", password);

        return usersExpressionList.findCount() == 1;
    }

    /**
     * adds all of the following traveller types to the database
     * @throws io.ebean.DuplicateKeyException if a type has already been added to the database
     */
    public static void addTravelTypes() throws io.ebean.DuplicateKeyException {
        (new TravellerType("Groupie")).save();
        (new TravellerType("Thrillseeker")).save();
        (new TravellerType("Gap Year")).save();
        (new TravellerType("Frequent Weekender")).save();
        (new TravellerType("Holidaymaker")).save();
        (new TravellerType("Business Traveller")).save();
        (new TravellerType("Backpacker")).save();
    }

    /**Tells model to create a user based on the data it is being passed
     *
     * @param userForm formdata containing primitive data type like int, String etc which has been passed from
     *                 the front end
     * @return returns the user Id that has been created
     */
    public int createUser(UserFormData userForm){

        String username = userForm.username;
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


        if(checkUsername(username)!=1){
        User user = new User(username, password, firstName, lastName, date, gender);



            user.save();
            for (int i = 0; i < tType.size(); i++) {

                int tTypeId = getTTypeId(tType.get(i));
                UpdateTravellerType(user, tTypeId);
            }
            //Passport loop
            for (int j = 0; j < passports.size(); j++) {

                int passportId = getPassportId(passports.get(j));
                UpdatePassport(user, passportId);
            }

            for (int k = 0; k < nationalities.size(); k++) {

                int natId = getNatId(nationalities.get(k));
                UpdateNationality(user, natId);
            }


            return user.getUserid();
        }
        return -1;
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
     * @return
     */
    public void UpdatePassport(User user, int passportId){
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
     * @return
     */
    public void UpdateNationality(User user, int natId){
        if (user != null) {
            Nationality nationality = Nationality.find.byId(natId);
            if(natId != -1){
                try {
                    user.addNationality(nationality);  ;
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
     * @return none
     */
    public void UpdateTravellerType(User user, int travellerId){
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

    /**
     * adds all of the following traveller types to the database
     * @throws io.ebean.DuplicateKeyException if a type has already been added to the database
     */
    public static  void addNatandPass() throws io.ebean.DuplicateKeyException {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            Nationality nationality = new Nationality(obj.getDisplayCountry());
            nationality.save();
            Passport passport = new Passport(obj.getDisplayCountry());
            passport.save();
        }
    }

    /**Returns 1 if in the database and 0 if not in the database
     *
     * @param username
     * @return 0 if username is not present or 1 if username is present.
     */
    public static int checkUsername(String username) {

        List<User> users = User.find.all();

        int present = 0;
        String userName;
        for (int i = 0; i < users.size(); i++) {

            userName = users.get(i).getUsername();
            logger.debug(userName + " " + "Username is " + userName + " " + userName.toLowerCase().equals(username.toLowerCase()));
            if(userName.toLowerCase().equals(username.toLowerCase())){
                present = 1;
            }
        }

        return present;

    }

    /** Returns a user id if they exist any number less than zero indicates the username is not in the database
     *
     * @param request
     * @return an int -1 indicates there are no entries in the database that have that user.
     */
    public static int getCurrentUserById(Http.Request request) {
        return User.getCurrentUserById(request);
    }

    public static int deleteNationalilty(){
        return 1;
    }

    public static UpdateUserFormData getUpdateUserFormDataForm(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            UpdateUserFormData updateUserFormDataForm = new UpdateUserFormData(user);
            return updateUserFormDataForm;
//            Form<UpdateUserFormData> updateUserForm = formFactory.form(UpdateUserFormData.class).fill(updateUserFormDataForm);
//            return updateUserForm;
        } else {
            return null;
        }
    }
}
