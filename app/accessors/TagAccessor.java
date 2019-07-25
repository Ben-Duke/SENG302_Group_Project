package accessors;

import io.ebean.RawSql;
import io.ebean.SqlQuery;
import models.Destination;
import models.Tag;

import java.util.List;
import java.util.Set;


public class TagAccessor {

    /**
     * Return the destination matching the id passed
     * @param id the id of the tag to retrieve
     * @return the matching tag from the database if it exists, else null
     */
    public static Tag getTagById(int id) {
        return Tag.find.byId(id);
    }

    /**
     * Return the tag matching the name passed
     * @param name the name of the tag to find
     * @return the first tag matching the name in the database if it exists. else null
     */
    public static Tag getTagByName(String name) {
        return Tag.find.query().where().ieq("name", name).findOne();
    }

    /**
     * Finds if a given tag is already in the database
     * @param tag the tag to check for existence
     * @return true if the tag is already in the database, else false
     */
    public static boolean exists(Tag tag) {
        return getTagByName(tag.getName()) != null;
    }

    /**
     * Insert the tag
     * @param tag the tag to insert
     */
    public static void insert(Tag tag) {
            tag.save();
        }

    /**
     * Delete the tag
     * @param tag the tag to delete
     */
    public static void delete(Tag tag) {tag.delete(); }

    /**
     * Update the tag
     * @param tag the tag to update
     */
    public static void update(Tag tag) {
        if(tag.getTagId() != null){
            tag.update();
        }

    }

    /**
     * Searches through the database and finds the set of tags that have a substring matching the query
     * @param searchQuery the substring to search for
     * @return the Set of tags with names matching the substring
     */
    public static Set<Tag> searchTags(String searchQuery) {
        return Tag.find.query().where().ilike("name", "%" + searchQuery + "%").findSet();
    }

    public static Set<Destination> searchTaggedDestination(Tag tag) {
        String sql = "SELECT destination_destid FROM destination_tag WHERE tag_tag_id = " + tag.getTagId();
        RawSql sqlQuery = RawSqlBuilder
        Set<Destination> destinations = Destination.find.query().findSet();

        System.out.println(destinations);
        return destinations;
    }

}
