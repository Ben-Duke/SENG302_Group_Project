package accessors;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import io.ebean.Query;
import models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class to handle accessing Users from the database
 */
public class UserAccessor {

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
     * Gets the urls and creation datetime from media and returns an ArrayList of these pairings
     * @param user
     * @return
     */
    public static ArrayList<String> getUserMediaData(User user){
        ArrayList<String> urls = new ArrayList<>();
        List<Media> media = MediaAccessor.getAllMediaForUser(user);
        for(int i =0; i < media.size(); i++){
            urls.add( media.get(i).getUrl());
        }
        return urls;
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
}
