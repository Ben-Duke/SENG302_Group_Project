package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;

import java.util.ArrayList;
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
    }
    public Album(List<Media> media, User user, String title) {
        this.media = media;
        this.user = user;
        this.title = title;
    }


    public Integer getAlbumId() { return albumId; }
    public List<Media> getMedia() { return media; }
    public User getUser() { return user; }
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public void addMedia(Media media) { this.media.add(media); }
    public void removeMedia(Media media) { this.media.remove(media); }
    public void removeAllMedia() { this.media = new ArrayList<>(); }

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

    public String toString() {
        return "Album title: "+this.getTitle()+
                " ID: "+this.getAlbumId()+
                " Owner userId: "+this.getUser().getUserid()+
                " Size: " +this.getMedia().size();
    }


}
