package accessors;

import models.Album;
import models.Media;

import java.util.List;

public class AlbumAccessor {

    public static Album getAlbumById(int id) {
        return Album.find.byId(id);
    }

    public static void insert(Album album) { album.save(); }

    public static void delete(Album album) { album.delete(); }

    public static void update(Album album) { album.update(); }

    public static List<Album> getAll() {
        return Album.find.all();
    }

}
