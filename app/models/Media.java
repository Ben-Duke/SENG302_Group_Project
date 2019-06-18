package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.ApplicationManager;
import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.*;
import java.nio.file.Paths;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"url"})})
public abstract class Media extends Model {

    @Id
    public Integer mediaId;

    /** A String representing the relative path to the photo resource. */
    @Column(name = "url")
    private String url;

    /** The user who owns the photo */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;

    public boolean isMediaPublic;

    public Media(String url, boolean isPublic, User user) {
        this.url = url;
        this.isMediaPublic = isPublic;
        this.user = user;
    }

    public static Finder<Integer,Media> find = new Finder<>(Media.class);

    public Integer getMediaId() { return mediaId; }
    public String getUrl() { return url; }
    public boolean getIsPublic() { return isMediaPublic; }
    public User getUser() { return user; }

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

    public void setUrl(String url) {this.url = url; }
    public void setPublic(boolean isPublic) { this.isMediaPublic = isPublic; }

}
