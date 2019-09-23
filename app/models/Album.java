package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
public class Album extends Model {

    @Id
    public Integer albumId;


    @ManyToMany
    public List<Media> media;

    /** The user who owns the album */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "destination", referencedColumnName = "destid")
    public Destination destination;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "event_id")
    public Event event;

    @ManyToOne
    public UserPhoto primaryPhoto;

    private AlbumOwner owner;

    private Boolean isDefault;

    private String title;

    public Album(AlbumOwner owner, String title, Boolean isDefault) {
        this.isDefault = isDefault;
        this.media = new ArrayList<>();
        setAlbumOwnerDetails(owner);
        this.title = title;
    }
    public Album(Media media, AlbumOwner owner, String title, Boolean isDefault) {
        this.isDefault = isDefault;
        this.media = new ArrayList<>();
        this.media.add(media);
        setAlbumOwnerDetails(owner);
        this.title = title;
    }
    public Album(List<Media> media, AlbumOwner owner, String title, Boolean isDefault) {
        this.isDefault = isDefault;
        this.media = media;
        setAlbumOwnerDetails(owner);
        this.title = title;
    }

    public Album(Album album) {
        setAlbumOwnerDetails(album.getOwner());
        this.media = album.getMedia();
        this.title = album.getTitle();
    }


    /**
     * Sets the album owner depending on the type of the owner.
     * @param owner the album owner
     */
    private void setAlbumOwnerDetails(AlbumOwner owner) {
        this.owner = owner;
        if(owner instanceof User) {
            user = (User) owner;
        }
        else if(owner instanceof Destination) {
            destination = (Destination) owner;
        }
        else if(owner instanceof Event) {
            event = (Event) owner;
        }
        else {
            throw new IllegalArgumentException("Invalid AlbumOwner type");
        }
    }


    public Integer getAlbumId() { return albumId; }
    public List<Media> getMedia() { return media; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public void setOwner(AlbumOwner owner) {
        setAlbumOwnerDetails(owner);
    }

    /**
     * If the destination is null, a user owns the album
     * If the user is null, a destination owns the album
     * @return the album owner
     */
    public AlbumOwner getOwner() {
        if(user != null) {
            return user;
        }
        else if(destination != null) {
            return destination;
        }
        else if (event != null) {
            return event;
        }
        return owner;
    }

    public void setTitle(String title) { this.title = title; }
    public UserPhoto getPrimaryPhoto() {
        return primaryPhoto;
    }
    public void setPrimaryPhoto(UserPhoto primaryPhoto) {
        this.primaryPhoto = primaryPhoto;
    }
    public void addMedia(Media media) {
        this.media.add(media);
    }
    public void removeMedia(Media media) {
        this.media.remove(media);
    }
    public void removeAllMedia() {
        this.media = new ArrayList<>();
    }

    public boolean userIsOwner(User user)
    {
        return this.getUser().getUserid() == user.getUserid();
    }



    public boolean isPublic() {
        return getVisibility();
    }

    public boolean containsMedia(Media testedMedia) {
        for (Media media : this.getMedia()) {
            if (media.getMediaId() == testedMedia.getMediaId()) {
                return true;
            }
        }
        return false;
    }

    public static Finder<Integer, Album> find = new Finder<>(Album.class);

    /**
     * Filter out the private media returning a list with only public media
     * @return a list with only public media
     */
    private List<Media> filterOutPrivateMedia() {
        List<Media> publicList = new ArrayList<Media>();
        for(Media media: this.media) {
            if(media.isPublic()) {
                publicList.add(media);
            }
        }
        return Collections.unmodifiableList(publicList);
    }

    /**
     * View the list of all media in the album
     * @param filterPrivateMedia filter out private media if true
     * @return a list of all media in the album (may be filtered)
     */
    public List<Media> viewAllMedia(Boolean filterPrivateMedia) {
        List<Media> mediaList = Collections.unmodifiableList(this.media);
        if(filterPrivateMedia) {
            mediaList = filterOutPrivateMedia();
        }
        return mediaList;
    }

    /**
     * Iterate over all media in the album checking the visibility, getting the album visibility accordingly
     */
    private Boolean getVisibility() {
        Boolean containsPublicMedia = false;
        for(Media media: this.media) {
            if(media.isPublic()) {
                containsPublicMedia = true;
            }
        }
        if (containsPublicMedia) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "Album title: "+this.getTitle()+
                " ID: "+this.getAlbumId()+
                " Owner: "+this.getOwner()+
                ", primaryPhoto=" + this.getPrimaryPhoto() +
                " Size: " +this.getMedia().size();
    }

    /**
     * Returns whether the album is public or not.
     * An album is private if all the media within the album is private
     * and the user viewing the album does not own the album.
     * @param visitingUser the user viewing the album
     * @return true if public, false if private
     */
    public boolean isPublicAlbum (User visitingUser) {
        //All non-user albums are public
        if (!(getOwner() instanceof User)) {
            return true;
        }
        User albumOwner = (User) getOwner();
        if(albumOwner.getUserid() == visitingUser.getUserid()) {
            return true;
        }
        for (Media media : media) {
            if(media.isPublic()) {
                return true;
            }
        }
        return false;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
