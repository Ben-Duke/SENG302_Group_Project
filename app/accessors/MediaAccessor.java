package accessors;

import models.Album;
import models.Destination;
import models.Media;
import models.Tag;

import java.util.*;

public class MediaAccessor {

    public static Media getMediaById(int id) {
        return Media.find.byId(id);
    }

    public static void insert(Media media) { media.save(); }

    public static void delete(Media media) {
        ArrayList<Tag> tags = new ArrayList<Tag>() ;

        for(Tag tag : media.getTags()){
            tags.add(tag);
        }

        for(Tag tag : tags){
            media.removeTag(tag);
            TagAccessor.update(tag);
        }
        MediaAccessor.update(media);
        media.delete();
        }

    public static void update(Media media) { media.update(); }

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
