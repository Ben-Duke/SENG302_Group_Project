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
public class UserPhoto extends Model implements Media {

    @Id
    private Integer photoId;

    /** A String representing the relative path to the photo resource. */
    @Column(name = "url")
    private String url;

    private boolean isPublic;

    /** The user who owns the photo */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private User user;

    /** The destinations the media is related to. */
    @JsonIgnore
    @ManyToMany
    private List<Destination> destinations;

    public boolean isProfile;

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
        this.primaryPhotoDestinations = primaryPhotoDestinations;
    }


    public UserPhoto(UserPhoto userPhoto) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
        this.isProfile = userPhoto.getIsProfilePhoto();
        this.primaryPhotoDestinations = userPhoto.getPrimaryPhotoDestinations();
    }

    public Integer getMediaId() { return photoId; }
    public String getUrl() { return url; }
    public boolean getIsPublic() { return isPublic; }
    public User getUser() { return user; }
    public List<Destination> getDestinations() { return destinations; }

    /**
     * Get the url for the media with its full path
     * @return the full path string for the file
     */
    public String getUrlWithPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getUserMediaPath()
                + user.getUserid()
                + "/"
                + url;
    }


    public void setUrl(String url) { this.url = url; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }

    /**
     * Unlink the media from the given destination
     * @param destination the destination to unlink from
     * @return true if the removal changed the list, else false
     */
    public boolean removeDestination(Destination destination) {
        return this.destinations.remove(destination);
    }


    @Override
    public String toString() {
        return "url is " + this.getUrl() + " Id is " + this.getMediaId();
    }

    public boolean getIsProfile() {
        return this.isProfile;
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
        String url = "";
        while(userPhoto != null) {
            count += 1;
            url = count + "_" + this.getUrl();
            userPhoto = UserPhoto.find.query().where().eq("url", url).findOne();
        }

        return url;
    }

    /**
     * Calling this function will delete a user photo that has that photoId does nothing if the photoId doesn't
     * match a photo in the database
     * @param idOfPhoto
     * @return
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


    /**
     * Method to set the photo as profile picture (or not)
     * @param isProfile the boolean showing if the picture is the profile picture
     */
    public void setProfile(boolean isProfile) {
        this.isProfile = isProfile;
    }


    /**
     * Get the primary photo destinations of the photo
     * @return the primary photo list
     */
    public List<Destination> getPrimaryPhotoDestinations() {
        return primaryPhotoDestinations;
    }

}