package accessors;

import io.ebean.Query;
import io.ebean.Ebean;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import io.ebean.Query;
import models.*;
import utilities.UtilityFunctions;

import javax.jws.soap.SOAPBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * A class to handle accessing Users from the database
 */
public class UserAccessor {
    static int QUERY_SIZE =  2;

    private static final String TRAVELLER_TYPE_COLUMN_NAME = "travellerTypes";
    private static final String NATIONALITY_COLUMN_NAME = "nationality";
    private static final String GENDER_COLUMN_NAME = "gender";


    public enum ColumnNames {
        TRAVELLER_TYPE,
        NATIONALITY,
    }

    public static void insert(User user) { user.save(); }


    /** Hides the implicit public constructor */
    private UserAccessor() {
        throw new IllegalStateException("Utility class");
    }

    public static User getDefaultAdmin(){
        throw new UnsupportedOperationException();
    }

    public static List<User> getAll() {
        return User.find().all();
    }

    public static Passport getPassport(int id) {
        return Passport.find().query().where().eq("passid", id).findOne();
    }

    /** Return a list of all passports
     * @return List of passports
     */
    public static List<Passport> getAllPassports() {
        return Passport.find().all();
    }

    /** Return a list of all nationalities
     * @return List of nationalities
     */
    public static List<Nationality> getAllNationalities() {
        return Nationality.find().all();
    }

    public static List<Album> getAlbums() {
        return Album.find.all();
    }

    /**
     * Gets a List of Users with a specific email.
     *
     * It should be a List of length 0 or 1, but you should still check
     * for 2 or more users in case our database is in an inconsistent state again.
     *
     * @param email String of the users email to search for.
     * @return A List of User objects with a matching email address.
     */
    public static List<User> getUsersFromEmail(String email) {
        return  User.find().query()
                    .where().eq("email", email.toLowerCase()).findList();
    }

    /** Return a list of users with an email in emails
     *
     * @param emails a collection of string emails
     * @return a list of users
     */
    public static List<User> getUsersByEmails(Collection<String> emails) {
        return User.find().query().where().in("email", emails).findList();
    }

    /**
     * Return the User matching the id passed
     * @param id the id of the user
     * @return User
     */
    public static User getById(int id) {
        return User.find().byId(id);
    }

    /**
     * Return the User matching the email passed
     * @param email the email of the user
     * @return User
     */
    public static User getUserByEmail(String email) {
        List<User> users = getUsersFromEmail(email);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    /** Update the user
     * @param user User to update in the database
     */
    public static void update(User user) { user.update(); }

    /**
     * Gets the profile picture for a User. Returns null if they have no profile
     * photo.
     *
     * @param user The User to get the photo of.
     * @return A UserPhoto representing the users profile picture.
     */
    public static UserPhoto getProfilePhoto(User user) {
        List<UserPhoto> userProfilePhotoList = UserPhoto.find().query()
                .where().eq("user", user)
                .and().eq("isProfile", true)
                .findList();

        if (userProfilePhotoList.isEmpty()) {
            return null;
        } else if (1 == userProfilePhotoList.size()) {
            if (userProfilePhotoList.get(0).getUser() != null) {
                return userProfilePhotoList.get(0);
            } else {
                return null;
            }
        } else {
            throw new io.ebean.DuplicateKeyException("Multiple profile photos.",
                    new Throwable("Multiple profile photos."));
        }
    }


    /**
     * Gets a paginated List of Users, with an offset and quantity to fetch.
     *
     * @param offset an integer representing the number of Users to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return A List<User> of Users.
     */
    public static List<User> getPaginatedUsers(int offset, int quantity){
        Query<User> query = User.find().query()
                .setFirstRow(offset).setMaxRows(quantity);
        return query.findList();
    }

    public static Set<User> getUsersWithAgeRange (String agerange1, String agerange2) {
        Date date1 = null;
        Date date2 = null;
        Boolean parseDate = (agerange1 != null && agerange2 != null) && (!agerange1.equals("") || !agerange2.equals(""));
        try {
            if (parseDate && agerange1.equals("") && !agerange2.equals("")) {
                date1 = new Date(Long.MIN_VALUE);
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
            } else if (parseDate && agerange2.equals("") && !agerange1.equals("")) {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
                date2 = new Date();
            } else if (parseDate && !agerange1.equals("") && !agerange2.equals("")) {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
                date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
            }
        } catch (ParseException e) {
            //Do Nothing
        }
        if(date1 != null && date2 != null){
            return User.find().query().where().gt("dateOfBirth", date1).lt("dateOfBirth", date2).findSet();
        }
        return new HashSet<>();
    }

    /**
     * Sends a request to ebeans to get a list of users based on pass parameters
     * @param travellerType a String which can be can be used to search the travellerType
     * @param offset an integer of how many users to skip in returned users
     * @param quantity an integer of how many users to be returned
     * @param queryNationality a String which can be can be used to search the nationality
     * @return a list of users returns empty if none are found
     */
   public static Set<User> getUsersByQuery(String travellerType,
                                           int offset, int quantity, String queryNationality,
                                           String agerange1, String agerange2, String gender){
        TravellerType type = null;
        Nationality nationality = null;
        List<List<Object>> equalsFields = new ArrayList<>();

       if(quantity < 1) {
           return new HashSet<User>();
       }

       if(offset < 0) {
           offset = 0;
       }

       List<Object> queryValues = new ArrayList<>();
       if (travellerType != null) {
           travellerType = toTitleCase(travellerType);
           queryValues.add(TRAVELLER_TYPE_COLUMN_NAME);
           type = TravellerType.find().query().where().eq("travellerTypeName", travellerType).findOne();
           queryValues.add(type);
       }
       else{
           queryValues.add("1");
           queryValues.add("1");
       }

       equalsFields.add(queryValues);
       queryValues = new ArrayList<>();
       if (queryNationality != null) {
           queryValues.add("nationality");
           queryNationality = toTitleCase(queryNationality);
           nationality = Nationality.find().query().where().eq("nationalityName", queryNationality).findOne();
           queryValues.add(nationality);
       }
       else{
           queryValues.add("1");
           queryValues.add("1");
       }

       equalsFields.add(queryValues);


       Date date1 = null;
       Date date2 = null;
       Boolean parseDate = (agerange1 != null && agerange2 != null) && (!agerange1.equals("") || !agerange2.equals(""));
       try {
           if (parseDate && agerange1.equals("") && !agerange2.equals("")) {
               date1 = new Date(Long.MIN_VALUE);
               date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
           } else if (parseDate && agerange2.equals("") && !agerange1.equals("")) {
               date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
               date2 = new Date();
           } else if (parseDate && !agerange1.equals("") && !agerange2.equals("")) {
               date1 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange1);
               date2 = new SimpleDateFormat("yyyy-MM-dd").parse(agerange2);
           }
       } catch (ParseException e) {
           //Do Nothing
       }
       Query<User> query = null;
       if(date1 != null && date2 != null){
           query = //Ebean.find(User.class)
                   User.find().query()
                           .where().gt("dateOfBirth", date1)
                           .lt("dateOfBirth", date2)
                           .select("uesrid")
                           //Use this to get connected traveller types
                           .fetch(TRAVELLER_TYPE_COLUMN_NAME,"*")

                           .where()

                           .eq((String) equalsFields.get(0).get(0),  equalsFields.get(0).get(1))
                           .select("userid")
                           .fetch(NATIONALITY_COLUMN_NAME, "*")
                           .where()

                           .eq((String) equalsFields.get(1).get(0), equalsFields.get(1).get(1))
                           .setFirstRow(offset).setMaxRows(quantity);
       }
       else {
           query = //Ebean.find(User.class)
                   User.find().query()
                           //Use this to get connected traveller types
                           .fetch(TRAVELLER_TYPE_COLUMN_NAME,"*")

                           .where()

                           .eq((String) equalsFields.get(0).get(0),  equalsFields.get(0).get(1))
                           .select("userid")
                           .fetch(NATIONALITY_COLUMN_NAME, "*")
                           .where()
                           .eq((String) equalsFields.get(1).get(0), equalsFields.get(1).get(1))
                           .setFirstRow(offset).setMaxRows(quantity);
       }
       return query.findSet();
   }

    /**
     * Converts the first letter of each word in a sentence to a capital letter.
     * @param givenString the sentence to convert
     * @return the reformatted string
     */
    private static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private static List<List<Object>> updateEqualsFields(String queryValue,
                                                         String columnName,
                                                         List<List<Object>> equalsFieldsToBeUpdated) {
        List<Object> queryValues = new ArrayList<>();
        if (queryValue != null) {
            queryValue = toTitleCase(queryValue);

            queryValues.add(columnName);
            Object convertedQueryValue = null;
            switch (columnName) {
                case TRAVELLER_TYPE_COLUMN_NAME:
                    convertedQueryValue = TravellerType.find().query().where().eq("travellerTypeName", queryValue).findOne();
                    break;
                case NATIONALITY_COLUMN_NAME:
                    convertedQueryValue = Nationality.find().query().where().eq("nationalityName", queryValue).findOne();
                    break;
                case GENDER_COLUMN_NAME:
                    convertedQueryValue = Nationality.find().query().where().eq("nationalityName", queryValue).findOne();
                    break;
            }
            queryValues.add(convertedQueryValue);
        }
        else{
            queryValues.add("1");
            queryValues.add("1");
        }

        equalsFieldsToBeUpdated.add(queryValues);
        return equalsFieldsToBeUpdated;
    }
}
