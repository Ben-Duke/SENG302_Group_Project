package models;

import io.ebean.Finder;
import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag extends Model implements Comparable{

    @Id
    private Integer tagId;

    private String name;

    public Tag(String name){
        this.name = name;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    /**
     * Returns the name of the tag as a String.
     * @return
     */
    public String getName(){
        return name;
    }

    /**
     * Sets the name of a tag based on the passed string.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        Tag tagOther = (Tag)o;
        return name.compareTo(tagOther.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            return this.name.equals(other.name);
        }
        return false;
    }

    public String toString(){
        return this.name;
    }

    public static final Finder<Integer,Tag> find = new Finder<>(Tag.class);
}
