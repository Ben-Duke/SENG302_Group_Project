package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.*;

@MappedSuperclass
public abstract class TaggableModel extends Model {

    @JsonIgnore
    @ManyToMany
    protected Set<Tag> tags = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userid")
    protected User user;

    protected boolean isPublic = true;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds a tag to set of tags.
     * @param tag the tag to add
     * @return true if the tag is not already in the set, else false
     */
    public boolean addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Added Tag cannot be null");
        }
        return tags.add(tag);
    }

    /**
     * Removes a tag from the set of tags.
     * @param tag - tag to be removed
     * @return true if the tag exists and was removed and false otherwise.
     */
    public boolean removeTag(Tag tag){
        return tags.remove(tag);
    }

}
