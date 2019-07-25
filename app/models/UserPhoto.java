package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * A class to hold information a user photograph.
 */
@Entity

@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public class UserPhoto extends Model {
    @Id //The photos primary key
    public int photoId;

    @Column(name = "url")
    public String url;
    public boolean isPublic;
    public boolean isProfile;

    // Creating  the relation to User
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    // Creating  the relation to Destination
    @JsonIgnore
    @ManyToMany
    public List<Destination> destinations;

    @JsonIgnore
    @OneToMany(mappedBy = "primaryPhoto")
    public List<Destination> primaryPhotoDestinations;

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
     * Create a UserPhoto with all filled in fields
     * @param url A String representing the relative path to the photo resource.
     * @param isPublic A boolean, true if the photo is visible to everybody, false otherwise.
     * @param user The User who owns this photograph.
     * @param destinations the photos linked destinations
     * @param primaryPhotoDestinations the photos linked primary photo destinations
     */
    public UserPhoto(String url, boolean isPublic, boolean isProfile, User user, List<Destination> destinations,
                     List<Destination> primaryPhotoDestinations) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
        this.isProfile = isProfile;
        this.destinations = destinations;
        this.primaryPhotoDestinations = primaryPhotoDestinations;
    }

    /**
     * Create a userPhoto using another userPhoto objects and it's attributes
     * @param userPhoto The userPhoto object being used
     */
    public UserPhoto(UserPhoto userPhoto){
        this.url = userPhoto.getUrl();
        this.isPublic = userPhoto.getIsPhotoPublic();
        this.user = userPhoto.getUser();
        this.isProfile = userPhoto.getIsProfilePhoto();
        this.destinations = userPhoto.getDestinations();
        this.primaryPhotoDestinations = userPhoto.getPrimaryPhotoDestinations();
    }

    public boolean getIsProfile(){
        return this.isProfile;
    }


    public boolean getIsPhotoPublic(){
        return this.isPublic;
    }

    public boolean getIsProfilePhoto(){
        return this.isProfile;
    }

    /**
     * Gets an unused user photo url.
     *
     * Checks for duplicate photo file names and increments the file name index until a non duplicate file name
     * is found.
     *
     * @return A String representing the unused url of the photo
     */
    public String getUnusedUserPhotoFileName(){
        int count = 0;
        UserPhoto userPhoto = this;
        String unusedUrl = "";
        while(userPhoto != null) {
            count += 1;
            unusedUrl = count + "_" + this.url;
            userPhoto = UserPhoto.find.query().where().eq("url", unusedUrl).findOne();
        }

        return unusedUrl;
    }

    /**
     * Calling this function will delete a user photo that has that photoId does nothing if the photoId doesn't
     * match a photo in the database
     * @param idOfPhoto The Id of the photo being deleted
     */
    public static void deletePhoto(int idOfPhoto){
        UserPhoto.find.query().where().eq("photoId",idOfPhoto).delete();
    }

    /**
     * Method to return if the photo is the profile picture
     * @return the boolean for if the photo is the profile picture
     */
    public boolean isProfile() {
        return isProfile;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }

    /**
     * Unlink the photo from the given destination
     * @param destination the destination to unlink from
     * @return true if the removal changed the list, else false
     */
    public boolean removeDestination(Destination destination) {
        return this.destinations.remove(destination);
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
        return Paths.get(".").toAbsolutePath().normalize().toString() + ApplicationManager.getUserPhotoPath() + user.getUserid() + "/" + url;
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

    /**
     * Get the primary photo destinations of the photo
     * @return the primary photo list
     */
    public List<Destination> getPrimaryPhotoDestinations() {
        return primaryPhotoDestinations;
    }

    /**
     * Returns this userPhoto objects as a string with it's parameters
     * @return The userPhoto objects attributes as a string
     */
    @Override
    public String toString() {
        return "url is " + this.url + " Id is " + this.getPhotoId();
    }
}