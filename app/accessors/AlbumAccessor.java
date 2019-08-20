package accessors;

import models.*;
import models.commands.Albums.CreateAlbumCommand;

import java.util.ArrayList;
import java.util.List;

public class AlbumAccessor {

    public static Album getAlbumById(int id) {
        return Album.find.byId(id);
    }

    public static Album getAlbumByTitle(String title) {
        List<Album> albumList = Album.find.query().where().eq("title", title).findList();
        if (albumList.size() == 0) {
            return null;
        } else {
            return albumList.get(0);
        }
    }

    public static void insert(Album album) { album.save(); }

    public static void delete(Album album) { album.delete(); }

    public static void update(Album album) { album.update(); }

    public static List<Album> getAll() {
        return Album.find.all();
    }

    public static void createAlbumFromDestination(AlbumOwner owner) {
        if(owner instanceof Destination) {
            Destination dest = (Destination) owner;
            CreateAlbumCommand cmd = new CreateAlbumCommand(
                    dest.getDestName(),
                    dest,
                    null);
            cmd.execute();
        }
        else{
            throw new IllegalArgumentException("Argument is not of type destination.");
        }
    }

    /**
     * Gets all albums that belong to the AlbumOwner
     * @param owner the album owner
     * @return a list of albums belonging to the owner
     */
    public static List<Album> getAlbumsByOwner(AlbumOwner owner) {
        List<Album> albums = new ArrayList<>();
        if(owner instanceof Destination) {
            Destination dest = (Destination) owner;
            albums = getAlbumsFromDestination(dest);
        }
        else if(owner instanceof User) {
            User user = (User) owner;
            albums = getAlbumsFromUser(user);
        }
        return albums;
    }

    /**
     * Gets a list of albums from a destination
     * @param dest the destination
     * @return the list of albums belonging to the destination
     */
    private static List<Album> getAlbumsFromDestination(Destination dest) {
        List<Album> albums = Album.find.query()
                .where().eq("destination", dest).findList();
        for (Album album : albums) {
            album.setOwner(dest);
        }
        return albums;
    }

    /**
     * Gets a list of albums belonging to a user
     * @param user the user
     * @return the list of albums belonging to the user
     */
    private static List<Album> getAlbumsFromUser(User user) {
        List<Album> albums = Album.find.query()
                .where().eq("user", user).findList();
        for (Album album : albums) {
            album.setOwner(user);
        }
        return albums;
    }

}
