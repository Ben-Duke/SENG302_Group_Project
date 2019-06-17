package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Model;

import javax.inject.Inject;
import javax.persistence.*;
import java.nio.file.Paths;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public abstract class Media extends Model {

    @Id
    private Integer mediaId;

    /** A String representing the relative path to the photo resource. */
    @Column(name = "url")
    private String url;

    private boolean isPublic;

    /** The user who owns the media */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    private User user;

    /** The destinations the media is related to. */
    @JsonIgnore
    @ManyToMany
    private List<Destination> destinations;

    public Media(String url, boolean isPublic, User user) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
    }

    public Media(String url, boolean isPublic, User user, List<Destination> destinations) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
        this.destinations = destinations;
    }

    public Integer getMediaId() { return mediaId; }
    public String getUrl() { return url; }
    public boolean getIsPublic() { return isPublic; }
    public User getUser() { return user; }
    public List<Destination> getDestinations() { return destinations; }

    /**
     * Get the url for the media with its full path
     * @return the full path string for the file
     */
    public String getUrlWithPath() {
        return Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getUserMediaPath()
                + user.getUserid()
                + "/"
                + url;
    }


    public void setUrl(String url) { this.url = url; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }

    /**
     * Unlink the media from the given destination
     * @param destination the destination to unlink from
     * @return true if the removal changed the list, else false
     */
    public boolean removeDestination(Destination destination) {
        return this.destinations.remove(destination);
    }


    @Override
    public String toString() {
        return "url is " + this.getUrl() + " Id is " + this.getMediaId();
    }

}
