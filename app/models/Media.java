package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;
import models.media.MediaOwner;

import javax.persistence.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public abstract class Media extends TaggableModel {

    @Id
    public Integer mediaId;

    /** A String representing the relative path to the photo resource. */
    @Column(name = "url")
    private String url;

    /** The user who owns the media */
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    @ManyToMany(mappedBy = "mediaList")
    private List<Destination> destinations;

    private List<MediaOwner> mediaOwners;

    @JsonIgnore
    @ManyToMany(mappedBy = "media")
    public List<Album> albums;

    private String caption = "";

    public Media(String url, boolean isPublic, User user, String caption) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
        this.caption = caption;
    }

    public Media(String url, boolean isPublic, User user) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
    }

    public static Finder<Integer,Media> find = new
            Finder<>(Media.class, ApplicationManager.getDatabaseName());

    /**
     * Default constructor for caption edit commands
     */
    protected Media() {
    }

    public Integer getMediaId() { return mediaId; }
    public String getUrl() { return url; }

    /**
     * Removes media from an album
     * @param album
     */
    public void removeAlbum(Album album){
        this.albums.remove(album);
    }

    /**
     * Adds media to an album
     * @param album
     */
    public void addAlbum(Album album){
        this.albums.add(album);
    }


    /**
     * Required for failing test. If deleted, ViewPublicAlbum test will print:
     * java.lang.NoSuchMethodError: models.Media.getIsMediaPublic()Z
     * @return the isMediaPublic attribute
     */
    public User getUser() { return user; }
    public List<Album> getAlbums() { return albums; }
    public String getCaption() { return caption; }

    /**
     * Gets the list of destinations that has an album linked to the photo
     * @return the list of destinations that has an album linked to the photo
     */
    @Deprecated
    public List<Destination> getDestinations() {
        List<Destination> destinations = new ArrayList<>();
        for (Album album : albums) {
            if (album.getOwner() instanceof Destination) {
                destinations.add((Destination) album.getOwner());
            }
        }
        return destinations;
    }

    /**
     * Sets the media owner depending on the type of the owner.
     * @param mediaOwner the media owner
     */
    private void addMediaOwnerDetails(MediaOwner mediaOwner) {
        this.mediaOwners.add(mediaOwner);

        if(mediaOwner instanceof User) {
            this.user = (User) mediaOwner;
        } else if(mediaOwner instanceof Destination) {
            this.destinations.add((Destination) mediaOwner);
        } else {
            throw new IllegalArgumentException("Invalid MediaOwner type");
        }
    }

    /**
     * Get the url for the media with its full path
     * @return the full path string for the file
     */
    public String getUrlWithPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getMediaPath()
                + "/"
                + this.getUrl();
    }

    /**
     * Checks if a MediaOwner owns this media item.
     *
     * @param mediaOwner The MediaOwner to check owns this media item
     *
     * @return A boolean, true if mediaOwner does own it, otherwise false.
     */
    public boolean isOwner(MediaOwner mediaOwner) {
        if(mediaOwner instanceof User) {
            if (this.user != null) {
                return ((User) mediaOwner).getUserid() == this.user.getUserid();
            }
        } else if(mediaOwner instanceof Destination) {
            Destination destMaybeOwner = (Destination)mediaOwner;
            for (Destination destination: this.destinations) {
                if (destination.getDestId() == destMaybeOwner.getDestId()) {
                    return true;
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid MediaOwner type");
        }

        return false;
    }

    public void setUrl(String url) {this.url = url; }
    public void setCaption(String caption) { this.caption = caption; }

//    public void addAlbum(Album album) { this.albums.add(album); }

    /**
     * Set's the albums for a media item.
     *
     * @param albums A List of all albums that contain the media item.
     */
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

}
