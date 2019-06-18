package accessors;

import models.Media;

public class MediaAccessor {

    public static Media getMediaById(int id) {
        return Media.find.byId(id);
    }


}
