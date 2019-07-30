package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public abstract class Media extends Model {

    @Id
    public Integer mediaId;

    /** A String representing the relative path to the photo resource. */
    @Column(name = "url")
    private String url;

    /** The user who owns the media */
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    @JsonIgnore
    @ManyToMany(mappedBy = "media")
    public List<Album> albums;

    public boolean isMediaPublic;

    private String caption = "";

    public Media(String url, boolean isPublic, User user, String caption) {
        this.url = url;
        this.isMediaPublic = isPublic;
        this.user = user;
        this.caption = caption;
    }

    public Media(String url, boolean isPublic, User user) {
        this.url = url;
        this.isMediaPublic = isPublic;
        this.user = user;
    }

    public static Finder<Integer,Media> find = new Finder<>(Media.class);

    /**
     * Default constructor for caption edit commands
     */
    protected Media() {
    }

    public Integer getMediaId() { return mediaId; }
    public String getUrl() { return url; }
    public boolean getIsPublic() { return isMediaPublic; }

    /**
     * Required for failing test. If deleted, ViewPublicAlbum test will print:
     * java.lang.NoSuchMethodError: models.Media.getIsMediaPublic()Z
     * @return the isMediaPublic attribute
     */
    public boolean getIsMediaPublic() { return isMediaPublic; }
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
     * Get the url for the media with its full path
     * @return the full path string for the file
     */
    public String getUrlWithPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getUserMediaPath()
                + this.getUser().getUserid()
                + "/"
                + this.getUrl();
    }

    public boolean userIsOwner(User user) {
        return user.getUserid() == this.getUser().getUserid();
    }

    public void setUrl(String url) {this.url = url; }
    public void setPublic(boolean isPublic) { this.isMediaPublic = isPublic; }
    public void setCaption(String caption) { this.caption = caption; }
    protected void setUser(User user) { this.user = user; }

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
