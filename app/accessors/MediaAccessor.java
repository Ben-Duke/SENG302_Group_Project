package accessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Album;
import models.Destination;
import models.Media;
import models.Tag;
import models.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
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

        return Media.find.query().where().select("*").fetch("user").where().eq("user", user).findList();

    }

    /**
     * Returns an ArrayNode list of all media a user
     * @param user the user that media is needed for
     * @return an ArrayNode list of all media a user
     */
    public static ArrayNode getUserMediaData(User user){
        ArrayNode userData = (new ObjectMapper()).createArrayNode();
        List<Media> userMedia = getAllMediaForUser(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for(int i = 0; i < userMedia.size(); i++){
            ObjectNode mediaNode = (new ObjectMapper()).createObjectNode();
            mediaNode.put("User", (user.getFName() + " " + user.getLName()));
            mediaNode.put("url", (userMedia.get(i).getUrl()));
            mediaNode.put("date_created", (userMedia.get(i).getDate_added().format(formatter)));
            userData.add(mediaNode);
        }

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
