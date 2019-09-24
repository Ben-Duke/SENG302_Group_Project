package accessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Album;
import models.Destination;
import models.Media;
import models.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MediaAccessor {

    public static Media getMediaById(int id) {
        return Media.find.byId(id);
    }

    public static void insert(Media media) { media.save(); }

    public static void delete(Media media) {
        // remove tags
        media.getTags().clear();
        MediaAccessor.update(media);

        // remove the media from the albums it is in
        for (Album album : media.getAlbums()) {
            album.getMedia().remove(media);
            AlbumAccessor.update(album);
        }

        media.delete();
    }

    public static void update(Media media) { media.update(); }

    /**
     * Gets the creation date of the passed media
     * @param media
     * @return Local Date
     */
    public static LocalDateTime getMediaCreationDate(Media media){
        return media.getDate_added();

    }

    /**
     * gets all the Media for a given user
     * @param user the user that media is needed for
     * @return a list of Media the user has
     */
    public static List<Media> getAllMediaForUser(User user){

        return Media.find.query().where().select("*").fetch("user").where().eq("user", user).and().eq("isPublic",true).findList();

    }

    /**
     * Gets a list of media of all the media belong to users that the given user is following.
     * Gets records earlier than the given date time up to the given limit, based on the given offset (pagination).
     * @param user the given user
     * @param offset the offset
     * @param limit the limit
     * @param dateTime the date time
     * @return a list of media
     */
    public static List<Media> getFollowingMedia(User user, int offset, int limit, LocalDateTime dateTime){

        List<Integer> list = new ArrayList<Integer>();
        List<Follow> userFollowing=  user.getFollowing();
        for(int i = 0; i < userFollowing.size() ;i++){
            list.add(userFollowing.get(i).getFolowedUserId());
        }

        return Media.find.query().where()
                .select("*")
                .fetch("user")
                .where()
                .in("userid", list)

                .and()
                .eq("isPublic",true)
                .lt("date_added", dateTime)
                .order().desc("date_added")
                .setFirstRow(offset)
                .setMaxRows(limit)

                .findList();
    }

    /**
     * Returns an ArrayNode of a media item, which is an node which
     * contains a User, url and date_created
     * @param media the media item
     * @return an ArrayNode of a media item
     */
    public static ArrayNode getUserMediaData(Media media){
        ArrayNode userData = (new ObjectMapper()).createArrayNode();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


            User user = UserAccessor.getById(media.user.getUserid());
            ObjectNode mediaNode = (new ObjectMapper()).createObjectNode();
            mediaNode.put("User", (user.getFName() + " " + user.getLName()));
            mediaNode.put("url", (media.getUrl()));
            mediaNode.put("date_created", (media.getDate_added().format(formatter)));
            userData.add(mediaNode);


        return userData;
    }

    /**
     * Gets the list of destinations that has an album linked to the media
     * @return the list of destinations that has an album linked to the media
     */
    public static List<Destination> getDestinations(Media media) {
        List<Destination> destinations = new ArrayList<>();
        for (Album album : media.getAlbums()) {
            if (album.getOwner() instanceof Destination) {
                destinations.add((Destination) album.getOwner());
            }
        }
        return destinations;
    }

}
