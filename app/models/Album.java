package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Album extends Model {

    @Id
    public Integer albumId;

    @ManyToMany
    private List<Media> media;

    public Album() {
        media = new ArrayList<>();
    }
    public Album(Media media) {
        this.media = new ArrayList<>();
        this.media.add(media);
    }
    public Album(List<Media> media) { this.media = media; }


    public Integer getAlbumId() { return albumId; }
    public List<Media> getMedia() { return media; }

    public void addMedia(Media media) {
        this.media.add(media);
    }

    public static Finder<Integer, Album> find = new Finder<>(Album.class);


}
