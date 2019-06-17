package models;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Album extends Model {

    @Id
    private Integer albumId;


    private List<Media> media;


    public Album() {
        media = new ArrayList<>();
    }

    public List<Media> getMedia() { return media; }

    public void addMedia(Media media) {
        this.media.add(media);
    }


}
