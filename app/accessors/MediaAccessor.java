package accessors;

import models.Media;

public class MediaAccessor {

    public static Media getMediaById(int id) {
        return Media.find.byId(id);
    }

    public static void insert(Media media) { media.save(); }

    public static void delete(Media media) { media.delete(); }

    public static void update(Media media) { media.update(); }

}
