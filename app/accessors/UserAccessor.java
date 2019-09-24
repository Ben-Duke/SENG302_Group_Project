package accessors;

import io.ebean.ExpressionList;
import io.ebean.Query;
import models.*;
import play.libs.Json;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    private static final String FOLLOWERS_COLUMN_NAME = "followers";
    private static final String FOLLOWING_COLUMN_NAME = "following";

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

    /**
     * Get a json ready string of the user that can be converted into json
     * @param userId
     * @return a String of user details
     */
    public static String getJsonReadyStringOfUser(int userId){
        User user = getById(userId);

        return "{'userId':'" + user.getUserid() + "','firstname':'" + user.getFName() +"','lastname':'" + user.getLName()+"'}";
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
     * Gets a list of users by gender, used for unit tests.
     * The gender can be either "male", "female" or "other". Returns null otherwise.
     * @param gender the gender as a string
     * @return a list of users by gender
     */
    public static Set<User> getUsersFromGender(String gender) {
        Set<User> users = null;
        if (gender.equalsIgnoreCase("male")) {
            users = User.find().query().where().eq(GENDER_COLUMN_NAME, "Male").findSet();
        }
        if (gender.equalsIgnoreCase("female")) {
            users = User.find().query().where().eq(GENDER_COLUMN_NAME, "Female").findSet();
        }
        if (gender.equalsIgnoreCase("other")) {
            users = User.find().query().where().eq(GENDER_COLUMN_NAME, "Other").findSet();
        }
        return users;
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


    private static ExpressionList<User> getUserQueryFromParams(String queryName, String travellerType,
                                               String queryNationality,
                                               String agerange1, String agerange2,
                                               String gender1, String gender2, String gender3) {
        List<List<Object>> equalsFields = new ArrayList<>();

        equalsFields = updateEqualsFields(travellerType, TRAVELLER_TYPE_COLUMN_NAME, equalsFields);
        equalsFields = updateEqualsFields(queryNationality, NATIONALITY_COLUMN_NAME, equalsFields);
        equalsFields = updateEqualsFields(gender1, GENDER_COLUMN_NAME, equalsFields);
        equalsFields = updateEqualsFields(gender2, GENDER_COLUMN_NAME, equalsFields);
        equalsFields = updateEqualsFields(gender3, GENDER_COLUMN_NAME, equalsFields);

        String[] splittedName = new String[] {queryName, queryName};
        if (queryName.split("\\s+").length  > 1) {
            splittedName = queryName.split("\\s+");
        }

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

        ExpressionList<User> query = null;
        if(date1 != null && date2 != null){
            query = //Ebean.find(User.class)
                    User.find().query()
                            .where()
                            .gt("dateOfBirth", date1)
                            .lt("dateOfBirth", date2)
                            .or()
                            .eq((String) equalsFields.get(2).get(0), equalsFields.get(2).get(1))
                            .eq((String) equalsFields.get(3).get(0), equalsFields.get(3).get(1))
                            .eq((String) equalsFields.get(4).get(0), equalsFields.get(4).get(1))
                            .endOr()
                            .where()
                            .or()
                            .ilike("f_name", "%" + splittedName[0] + "%")
                            .ilike("l_name", "%" + splittedName[1] + "%")
                            .select("userid")
                            //Use this to get connected traveller types
                            .fetch(TRAVELLER_TYPE_COLUMN_NAME,"*")
                            .where()
                            .eq((String) equalsFields.get(0).get(0),  equalsFields.get(0).get(1))

                            .select("userid")
                            .fetch(NATIONALITY_COLUMN_NAME, "*")
                            .where()
                            .eq((String) equalsFields.get(1).get(0), equalsFields.get(1).get(1));

        }
        else {
            query = //Ebean.find(User.class)
                    User.find().query()
                            .where()
                            .or()
                            .eq((String) equalsFields.get(2).get(0), equalsFields.get(2).get(1))
                            .eq((String) equalsFields.get(3).get(0), equalsFields.get(3).get(1))
                            .eq((String) equalsFields.get(4).get(0), equalsFields.get(4).get(1))
                            .endOr()
                            .where()
                            .or()
                            .ilike("f_name", "%" + splittedName[0] + "%")
                            .ilike("l_name", "%" + splittedName[1] + "%")
                            .endOr()                            .select("userid")
                            //Use this to get connected traveller types
                            .fetch(TRAVELLER_TYPE_COLUMN_NAME,"*")

                            .where()

                            .eq((String) equalsFields.get(0).get(0),  equalsFields.get(0).get(1))
                            .select("userid")
                            .fetch(NATIONALITY_COLUMN_NAME, "*")
                            .where()
                            .eq((String) equalsFields.get(1).get(0), equalsFields.get(1).get(1));
        }
        return query;
    }

    /**
     * Sends a request to ebeans to get a list of users based on pass parameters
     * Usage examples:
     *
     * Example 1: Search for a user born after 22nd August 1998 and before 24th August 1998
     * where their gender can be either male or female
     * /users/profile/searchprofiles?bornafter=1998-08-22&bornbefore=1998-08-24&gender1=male&gender2=female
     *
     *
     * Example 2: Search for a user with a nationality of Afghanistan and gender of male
     * /users/profile/searchprofiles?nationality=afghanistan&gender1=male
     *
     * Example 3: Search for a user with a nationality of Czechoslovakia
     * and gender of male or other and Traveller Type of gap year
     * /users/profile/searchprofiles?nationality=czechoslovakia&gender1=male&gender2=other&travellertype=gap%20year
     *
     * @param travellerType a String which can be can be used to search the travellerType
     * @param offset an integer of how many users to skip in returned users
     * @param quantity an integer of how many users to be returned
     * @param queryNationality a String which can be can be used to search the nationality
     * @param agerange1 the lower bound of age range, passed in as a string with the format yyyy-MM-dd
     * @param agerange2 the upper bound of age range, passed in as a string with the format yyyy-MM-dd
     * @param gender1 The first gender union to search for: Valid values are Male, Female, Other
     * @param gender2 The second gender union to search for: Valid values are Male, Female, Other
     * @param gender3 The third gender union to search for: Valid values are Male, Female, Other
     * @return a list of users, returns empty if none are found
     */
   @SuppressWarnings("Duplicates")
   public static Set<User> getUsersByQuery(String travellerType,
                                           int offset, int quantity, String queryName, String queryNationality,
                                           String agerange1, String agerange2, String gender1,
                                           String gender2, String gender3) {

       if(quantity < 1) {
           return new HashSet<>();
       }

       if(offset < 0) {
           offset = 0;
       }

        Query<User> query = getUserQueryFromParams(queryName, travellerType, queryNationality,
                                                   agerange1, agerange2,
                                                   gender1, gender2, gender3)
                .setFirstRow(offset).setMaxRows(quantity);

       return query.findSet();
   }

   public static int getUserQueryCount(String travellerType, String queryNationality,
                                       String agerange1, String agerange2, String gender1,
                                       String gender2, String gender3) {

       int userCount = getUserQueryFromParams(travellerType, queryNationality,
               agerange1, agerange2,
               gender1, gender2, gender3).findCount();

       return userCount;
   }


   public static Set<User> getFollowingQuery(User currentUser, int offSet, int quantity) {

       Set<Follow> followSet = Follow.find().query().where().eq("followed", currentUser)
               .setFirstRow(offSet).setMaxRows(quantity).findSet();
       System.out.println(followSet);

       Set<User> followedUser = new HashSet<>();
       for (Follow follow: followSet) {
           followedUser.add(follow.getFollower());
       }

       return followedUser;
   }

   public static int getFollowingCount(User currentUser) {

       int followingCount = Follow.find().query().where().eq("followed", currentUser).findCount();

       return followingCount;
   }

    public static Set<User> getFollowedQuery(User currentUser, int offSet, int quantity) {

        Set<Follow> followSet = Follow.find().query().where().eq("follower", currentUser)
                .setFirstRow(offSet).setMaxRows(quantity).findSet();

        Set<User> followedUser = new HashSet<>();
        for (Follow follow: followSet) {
            followedUser.add(follow.getFollowed());
        }

        return followedUser;
    }

    public static int getFollowedCount(User currentUser) {
        int followedCount = Follow.find().query().where().eq("follower", currentUser).findCount();

        return followedCount;
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

    /**
     * Updates and returns the updated equal fields. The equal fields are used by the sql query
     * to give optional fields the value of [1,1] (which evaluates to true) when they are not used,
     * or [Column name, value] when the optional field is used to search for something.
     * This is then processed by the getUsersByQuery method to filter.
     *
     * @param queryValue the query value to search for (ie male or female)
     * @param columnName the column (attribute) to filter by, eg gender or nationality
     * @param equalsFieldsToBeUpdated the equalField object to update
     * @return
     */
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
                    convertedQueryValue = queryValue;
                    break;
            }
            if(convertedQueryValue != null) {
                queryValues.add(convertedQueryValue);
            }
            else {
                //If the searched entity is invalid, return no results.
                queryValues.set(0, "0");
                queryValues.add("1");
            }
        }
        else{
            if(! columnName.equalsIgnoreCase(GENDER_COLUMN_NAME)) {
                queryValues.add("1");
                queryValues.add("1");
            }
            else {
                queryValues.add("0");
                queryValues.add("1");
            }
        }

        equalsFieldsToBeUpdated.add(queryValues);
        return equalsFieldsToBeUpdated;
    }
}
