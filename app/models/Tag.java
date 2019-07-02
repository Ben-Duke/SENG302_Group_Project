package models;

public class Tag {

    String name;

    public Tag(String name){
        this.name = name;
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
}
