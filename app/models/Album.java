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

    private String title;

    private Boolean isPublic = false;

    public Album(User user, String title) {
        media = new ArrayList<>();
        this.user = user;
        this.title = title;
    }
    public Album(Media media, User user, String title) {
        this.media = new ArrayList<>();
        this.media.add(media);
        this.user = user;
        this.title = title;
        setVisibility();
    }
    public Album(List<Media> media, User user, String title) {
        this.media = media;
        this.user = user;
        this.title = title;
        setVisibility();
    }


    public Integer getAlbumId() { return albumId; }
    public List<Media> getMedia() { return media; }
    public User getUser() { return user; }
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public void addMedia(Media media) {
        this.media.add(media);
        setVisibility();
    }
    public void removeMedia(Media media) {
        this.media.remove(media);
        setVisibility();
    }
    public void removeAllMedia() {
        this.media = new ArrayList<>();
        setVisibility();
    }

    public boolean userIsOwner(User user) {
        return this.getUser().getUserid() == user.getUserid();
    }

    public boolean isPublic() {
        // Loop through all media item testing for
        // publicity. If all private then return false
        // AC7
        return true;
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
            if(media.isMediaPublic) {
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
     * Iterate over all media in the album checking the visibility, setting the album visibility accordingly
     */
    public void setVisibility() {
        Boolean containsPublicMedia = false;
        for(Media media: this.media) {
            if(media.isMediaPublic) {
                containsPublicMedia = true;
            }
        }
        if (containsPublicMedia) {
            this.isPublic = true;
        } else {
            this.isPublic = false;
        }
    }

    public String toString() {
        return "Album title: "+this.getTitle()+
                " ID: "+this.getAlbumId()+
                " Owner userId: "+this.getUser().getUserid()+
                " Size: " +this.getMedia().size();
    }


}
