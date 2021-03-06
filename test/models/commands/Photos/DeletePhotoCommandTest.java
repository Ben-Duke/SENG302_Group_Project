package models.commands.Photos;

import accessors.AlbumAccessor;
import models.Destination;
import models.UserPhoto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.*;

public class DeletePhotoCommandTest  extends BaseTestWithApplicationAndDatabase {

    @Test
    public void execute() {
        UserPhoto userPhoto = UserPhoto.find().byId(1);
        assertNotNull(userPhoto);
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(userPhoto);
        deletePhotoCommand.execute();
        UserPhoto afterDeletePhoto = UserPhoto.find().byId(1);
        assertNull(afterDeletePhoto);
    }

    @Test
    public void undo() {

        UserPhoto userPhoto1 = UserPhoto.find().byId(1);
        Destination christchurch = Destination.find().byId(1);
        Destination wellington = Destination.find().byId(2);
        christchurch.getPrimaryAlbum().addMedia(userPhoto1);
        AlbumAccessor.update(christchurch.getPrimaryAlbum());
        wellington.getPrimaryAlbum().addMedia(userPhoto1);
        AlbumAccessor.update(wellington.getPrimaryAlbum());

        UserPhoto userPhoto = UserPhoto.find().byId(1);
        String url = userPhoto.getUrl();
        assertEquals(2, userPhoto.getAlbums().size());
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(userPhoto);
        deletePhotoCommand.execute();
        UserPhoto beforeUndoPhoto = UserPhoto.find().query().where().eq("url",url).findOne();
        assertNull(beforeUndoPhoto);
        deletePhotoCommand.undo();
        UserPhoto afterUndoPhoto = UserPhoto.find().query().where().eq("url",url).findOne();
        assertNotNull(afterUndoPhoto);
        assertEquals(2, afterUndoPhoto.getAlbums().size());
    }

    @Test
    public void redo() {
        UserPhoto userPhoto = UserPhoto.find().byId(1);
        String url = userPhoto.getUrl();
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(userPhoto);
        deletePhotoCommand.execute();
        deletePhotoCommand.undo();
        deletePhotoCommand.redo();
        UserPhoto afterRedoPhoto = UserPhoto.find().query().where().eq("url",url).findOne();
        assertNull(afterRedoPhoto);
    }
}