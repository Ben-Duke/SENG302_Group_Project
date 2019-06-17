package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;

import javax.persistence.*;
import java.nio.file.Paths;
import java.util.List;

/**
 * A class to hold information a user photograph.
 */
@Entity

public class UserPhoto extends Media {

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
        super(url, isPublic, user);
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
        super(url, isPublic, user, destinations);
        this.isProfile = isProfile;
        this.primaryPhotoDestinations = primaryPhotoDestinations;
    }


    public UserPhoto(UserPhoto userPhoto) {
        super(userPhoto.getUrl(), userPhoto.getIsPublic(), userPhoto.getUser(), userPhoto.getDestinations());
        this.isProfile = userPhoto.getIsProfilePhoto();
        this.primaryPhotoDestinations = userPhoto.getPrimaryPhotoDestinations();
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