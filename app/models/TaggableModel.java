package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Model;

import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import java.util.Set;
import java.util.TreeSet;

@MappedSuperclass
public abstract class TaggableModel extends Model {

    @JsonIgnore
    @ManyToMany
    protected Set<Tag> tags = new TreeSet<>();

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
