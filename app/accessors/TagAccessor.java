package accessors;

import models.Tag;



public class TagAccessor {

    /** Return the destination matching the id passed */
    public static Tag getTagById(int id) {
        return Tag.find.byId(id);
    }

    /** Return the tag matching the name passed */
    public static Tag getTagByName(String name) {
        return Tag.find.query().where().eq("name", name).findOne();
    }

    /** Insert the tag */
    public static void insert(Tag tag) {
            tag.save();
        }

    /** delete the tag */
    public static void delete(Tag tag) {tag.delete(); }

    /** Update the tag */
    public static void update(Tag tag) {
        if(tag.getTagId() != null){
            tag.update();
        }

    }

}
