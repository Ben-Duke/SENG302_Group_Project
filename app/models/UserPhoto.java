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

public class UserPhoto extends Media {

    public boolean isProfile;

    @JsonIgnore
    @OneToMany(mappedBy = "primaryPhoto")
    public List<Album> primaryPhotoDestinations;

    public static final Finder<Integer,UserPhoto> find = new Finder<>(UserPhoto.class);

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
        super(url, isPublic, user);
        this.isProfile = isProfile;
    }

    /**
     * Create a UserPhoto with all filled in fields
     * @param url A String representing the relative path to the photo resource.
     * @param isPublic A boolean, true if the photo is visible to everybody, false otherwise.
     * @param user The User who owns this photograph.
     * @param albums the albums containing the photo
     * @param primaryPhotoDestinations the photos linked primary photo destinations
     */
    public UserPhoto(String url, boolean isPublic, boolean isProfile, User user, List<Album> albums,
                     List<Album> primaryPhotoDestinations) {
        super(url, isPublic, user);
        this.isProfile = isProfile;
        this.primaryPhotoDestinations = primaryPhotoDestinations;
        this.albums = albums;
    }


    public UserPhoto(UserPhoto userPhoto) {
        super(userPhoto.getUrl(), userPhoto.getIsPublic(), userPhoto.getUser(), userPhoto.getCaption());
        this.isProfile = userPhoto.getIsProfilePhoto();
        this.primaryPhotoDestinations = userPhoto.getPrimaryPhotoDestinations();
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
    public List<Album> getPrimaryPhotoDestinations() {
        return primaryPhotoDestinations;
    }

    /**
     * Modifies the fields of this UserPhoto which are included in the
     * UserPhoto editing form to be equal to those fields of the UserPhoto
     * passed in
     * */
    public void applyEditChanges(UserPhoto editedPhoto) {
        setUrl(editedPhoto.getUrl());
        setPublic(editedPhoto.getIsPublic());
        setProfile(editedPhoto.getIsProfile());
        setCaption(editedPhoto.getCaption());
        setUser(editedPhoto.getUser());
        this.albums = editedPhoto.getAlbums();
        this.primaryPhotoDestinations = editedPhoto.getPrimaryPhotoDestinations();
    }


}