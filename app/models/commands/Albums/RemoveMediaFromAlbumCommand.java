package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.UserPhoto;
import models.UserVideo;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;

public class RemoveMediaFromAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> medias;
    private List<Media> deletedMedia;

    public RemoveMediaFromAlbumCommand(Album album, List<Media> medias) {
        super(CommandPage.ALBUM);
        this.album = album;
        this.medias = medias;
        this.deletedMedia = new ArrayList<>();
    }

    /**
     * Remove the given media from the given album.
     * If the media belongs to no other album, it is
     * deleted, Albums AC10.
     * Deleted media is recorded so it can be resurrected
     * upon undo.
     */
    public void execute() {

        for (Media media : medias) {
            album.removeMedia(media);
            AlbumAccessor.update(album);

            media = MediaAccessor.getMediaById(media.getMediaId());

            if (media.getAlbums().size() == 0) {
                deletedMedia.add(media);
                MediaAccessor.delete(media);
            }
        }

    }

    /**
     * Adds the removed media back to the album.
     * If the media was deleted then it is recreated.
     * Media that is re-added to the album is recorded
     * so the next redo will work.
     */
    public void undo() {

        List<Media> resurrectedMedia = new ArrayList<>();

        for (Media media : medias) {

            if (deletedMedia.contains(media)) {
                if (media instanceof UserPhoto) {
                    UserPhoto photo = (UserPhoto)media;
                    media = new UserPhoto(photo);
                } else if (media instanceof UserVideo) {
                    UserVideo video = (UserVideo)media;
                    media = new UserVideo(video);
                }
                MediaAccessor.insert(media);
                album.addMedia(media);

            } else {
                album.addMedia(media);
            }
            AlbumAccessor.update(album);

            resurrectedMedia.add(media);
        }
        this.medias = resurrectedMedia;

    }

    /**
     * Resets the deleted media, then calls
     * execute to remove the given media again.
     */
    public void redo() {
        this.deletedMedia = new ArrayList<>();
        execute();
    }


}
