package accessors;

import io.ebean.DuplicateKeyException;
import models.Destination;
import models.Tag;

import java.util.List;
import java.util.logging.Logger;


public class TagAccessor {

    /** Return the destination matching the id passed */
    public static Tag getTagById(int id) {
        return Tag.find.query().where().eq("tagId", id).findOne();
    }

    /** Insert the tag */
    public static void insert(Tag tag) {
        try{
            tag.save();
        }
        catch (DuplicateKeyException e){
            System.out.println("Tag already exist");
        }
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
