package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.nio.file.Paths;

/**
 * A class to hold information a user photograph.
 */
@Entity
public class UserPhoto extends Model {
    @Id //The photos primary key
    public int photoId;
    public String url;
    public boolean isPublic;
    public boolean isProfile;

    // Creating  the relation to User
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    // Creating  the relation to Destination
    @ManyToOne
    @JoinColumn(name = "destination", referencedColumnName = "destid")
    public Destination destination;

    public static Finder<Integer,UserPhoto> find = new Finder<>(UserPhoto.class);

    /**
     * Constructor method for UserPhoto.
     *
     * @param url A String representing the relative path to the photo resource.
     * @param isPublic A boolean, true if the photo is visible to everybody, false otherwise.
     * @param user The User who owns this photograph.
     */
    public UserPhoto(String url, boolean isPublic, boolean isProfile, User user) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
        this.isProfile = isProfile;
    }

    /**
     * Method to return if the photo is the profile picture
     * @return the boolean for if the photo is the profile picture
     */
    public boolean isProfile() {
        return isProfile;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
    /**
     * Method to set the photo as profile picture (or not)
     * @param isProfile the boolean showing if the picture is the profile picture
     */
    public void setProfile(boolean isProfile) {
        this.isProfile = isProfile;
    }

    /**
     * Method to get the autogenerated Id for this UserPhoto.
     *
     * @return An int, representing the UserPhoto's id in the database.
     */
    public int getPhotoId() {
        return photoId;
    }

    /**
     * Method to get the url to the photo resource.
     *
     * @return A String representing the relative path to the photo resource.
     */
    public String getUrl() {
        return url;
    }


    /**
     * Method to get the url for a photo with its full path
     * @return the full path string for the file
     */
    public String getUrlWithPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/" + url;
    }

    /**
     * Method to set the url to the photo resource.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Method to get whether the photo is public or private.
     *
     * @return A boolean, true if photo is public, false otherwise.
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * Method to set the privacy of the photo.
     *
     * @param aPublic A boolean, true for publicly visible, false for private.
     */
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    /**
     * Gets the User who owns this photo.
     *
     * @return A User object, the owner of the photo.
     */
    public User getUser() {
        return user;
    }

}