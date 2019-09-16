package accessors;

import io.ebean.Ebean;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import io.ebean.Query;
import models.*;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class to handle accessing Users from the database
 */
public class UserAccessor {
    static int QUERY_SIZE =  2;

    private static String TRAVELLER_TYPE_COLUMN_NAME = "travellerTypes";

    public enum QueryParameters {
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
     * Sends a request to ebeans to get a list of users based on pass parameters
     * @param travellerType a String which can be can be used to search the travellerType
     * @param offset an integer of how many users to skip in returned users
     * @param quantity an integer of how many users to be returned
     * @param queryNationality a String which can be can be used to search the nationality
     * @return a list of users returns empty if none are found
     */
   public static List<User> getUsersByQuery(String travellerType, int offset, int quantity, String queryNationality){
        TravellerType type = null;
        Nationality nationality = null;
        List<List<Object>> equalsfields = new ArrayList<>();

       if(quantity < 1) {
           return new ArrayList<User>();
       }

       if(offset < 0) {
           offset = 0;
       }

       List<Object> queryValues = new ArrayList<>();
       if (! (travellerType == null)) {
           queryValues.add(TRAVELLER_TYPE_COLUMN_NAME);
           type = TravellerType.find().query().where().eq("travellerTypeName", travellerType).findOne();
           queryValues.add(type);
       }
       else{
           queryValues.add("1");
           queryValues.add("1");
       }
       equalsfields.add(queryValues);
       queryValues = new ArrayList<>();
       if (! (nationality == null)) {
           queryValues.add("nationality");
           nationality = Nationality.find().query().where().eq("nationalityName", queryNationality).findOne();
           queryValues.add(nationality);
       }
       else{
           queryValues.add("1");
           queryValues.add("1");
       }
       equalsfields.add(queryValues);
       Query<User> query = //Ebean.find(User.class)
               User.find().query()

               .select("userid")
               //Use this to get connected traveller types
               .fetch(TRAVELLER_TYPE_COLUMN_NAME,"*")
                       .fetch("nationality", "*")
               .where()

               .eq((String) equalsfields.get(0).get(0),  equalsfields.get(0).get(1))


                       .and()
                       .eq((String) equalsfields.get(1).get(0), equalsfields.get(1).get(1))
               .setFirstRow(offset).setMaxRows(quantity);

       System.out.println("Query is "+query.toString());
       return query.findList();
   }
}
