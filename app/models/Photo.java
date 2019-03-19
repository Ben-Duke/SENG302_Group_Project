package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Photo extends Model {
    @Id
    public int photoId;
    public String url;
    public boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    public User user;


    public Photo(String url, boolean isPublic, User user) {
        this.url = url;
        this.isPublic = isPublic;
        this.user = user;
    }


    public int getPhotoId() {
        return photoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public User getUser() {
        return user;
    }
}