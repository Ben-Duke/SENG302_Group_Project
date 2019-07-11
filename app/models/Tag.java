package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Entity
public class Tag extends Model implements Comparable{

    public static Finder<Integer,Tag> find = new Finder<>(Tag.class);
    @JsonIgnore
    @ManyToMany
    public List<Destination> destinations;
    /**
     * The ID of the Tag. This is the primary key.
     */
    @Id
    public int tagId;

    @Constraints.Required
    @Column(name="name", unique=true)
    public String name;

    public Tag(String name){
        if (name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    /**
     * Returns the name of the tag as a String.
     * @return
     */
    public String getName(){
        return name;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public void addDestination(Destination destination) {
        this.destinations.add(destination);
    }

    public void addDestinationById(Integer destId) {
        Destination destination = Destination.find.byId(destId);
        addDestination(destination);
    }
    public boolean removeDestination(Destination destination) {
        return this.destinations.remove(destination);
    }
    /**
     * Sets the name of a tag based on the passed string.
     * @param name
     */
    public void setName(String name) {
        if (name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        Tag tagOther = (Tag)o;
        return name.compareTo(tagOther.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + tagId;
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        return  hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    public String toString(){
        return this.name;
    }
}
