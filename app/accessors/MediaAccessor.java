package accessors;

import models.Album;
import models.Destination;
import models.Media;
import models.Tag;

import java.time.LocalDate;
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
    public static LocalDate getMediaCreationDate(Media media){
        return media.getDate_added();

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
