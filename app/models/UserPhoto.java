package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.nio.file.Paths;
import java.util.List;

/**
 * A class to hold information a user photograph.
 */
@Entity

@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public class UserPhoto extends BaseModel {
    @Id //The photos primary key
    public int photoId;

    @Column(name = "url")
    public String url;

    private boolean isPublic;
    private boolean isProfile;

    private String caption = "";

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
    private List<Destination> primaryPhotoDestinations;

    public static final Finder<Integer,UserPhoto> find = new Finder<>(UserPhoto.class, ApplicationManager.getDatabaseName());

    /**
     * Default constructor for caption edit commands
     */
    public UserPhoto() {}

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

    public boolean getIsProfile(){
        return this.isProfile;
    }

    public UserPhoto(UserPhoto userPhoto) {
        this.url = userPhoto.getUrl();
        this.isPublic = userPhoto.getIsPhotoPublic();
        this.user = userPhoto.getUser();
        this.isProfile = userPhoto.getIsProfilePhoto();
        this.destinations = userPhoto.getDestinations();
        this.primaryPhotoDestinations = userPhoto.getPrimaryPhotoDestinations();
        this.caption = userPhoto.getCaption();
    }


    public boolean getIsPhotoPublic(){
        return this.isPublic;
    }

    public boolean getIsProfilePhoto(){
        return this.isProfile;
    }

    /**
     * Gets an unused user photo filename.
     *
     * Checks for duplicate photo file names and increments the file name index until a non duplicate file name
     * is found.
     *
     * @return A String representing the unused filename of the photo
     */
    public String getUnusedUserPhotoFileName(){
        int count = 0;
        UserPhoto userPhoto = this;
        String filename = "";
        while(userPhoto != null) {
            count += 1;
            filename = count + "_" + this.url;
            userPhoto = UserPhoto.find.query().where().eq("url", filename).findOne();
        }

        return filename;
    }

    /**
     * Calling this function will delete a user photo that has that photoId does nothing if the photoId doesn't
     * match a photo in the database
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    @Override
    public String toString() {
        return "url is " + this.url + " Id is " + this.getPhotoId();
    }

    /**
     * Modifies the fields of this UserPhoto which are included in the
     * UserPhoto editing form to be equal to those fields of the UserPhoto
     * passed in
     * */
    public void applyEditChanges(UserPhoto editedPhoto) {
        this.url = editedPhoto.getUrl();
        this.isPublic = editedPhoto.getIsPhotoPublic();
        this.isProfile = editedPhoto.getIsProfile();
        this.caption = editedPhoto.getCaption();
        this.user = editedPhoto.getUser();
        this.destinations = editedPhoto.getDestinations();
        this.primaryPhotoDestinations = editedPhoto.getPrimaryPhotoDestinations();
    }
}