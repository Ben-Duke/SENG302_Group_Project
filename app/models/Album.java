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
    private List<Media> media;

    /** The user who owns the photo */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    public Album(User user) {
        media = new ArrayList<>();
        this.user = user;
    }
    public Album(Media media, User user) {
        this.media = new ArrayList<>();
        this.media.add(media);
        this.user = user;
    }
    public Album(List<Media> media, User user) {
        this.media = media;
        this.user = user;
    }


    public Integer getAlbumId() { return albumId; }
    public List<Media> getMedia() { return media; }

    public void addMedia(Media media) {
        this.media.add(media);
    }

    public static Finder<Integer, Album> find = new Finder<>(Album.class);


}
