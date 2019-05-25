package models.commands.UserPhotos;

import controllers.ApplicationManager;

import models.User;
import models.UserPhoto;
import models.commands.UploadPhotoCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.libs.Files;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.io.File;
import java.nio.file.Paths;

import static org.apache.commons.io.FileUtils.getFile;
import static org.junit.Assert.assertEquals;


public class UploadPhotoCommandTest extends BaseTestWithApplicationAndDatabase {
    private Database database;
    private UploadPhotoCommand uploadPhotoCommand;
    private UserPhoto userPhoto;
    private User user;
    private Files.TemporaryFile temporaryFile;


    @Override
    @Before
    public void setUpDatabase() {
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();
        user = User.find.byId(1);
        userPhoto =  new UserPhoto("imagetest.png", false, false, user);
        String unusedPhotoUrl = userPhoto.getUnusedUserPhotoFileName();
        userPhoto.setUrl(unusedPhotoUrl);
        File file = getFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/test/resources/imagetest.png");
        Files.TemporaryFileCreator creator = Files.singletonTemporaryFileCreator();
        temporaryFile = creator.create(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/test/resources/imagetest.png"));
        uploadPhotoCommand = new UploadPhotoCommand(userPhoto, temporaryFile);
    }

    @Override
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Test
    public void testExecute() {
        int beforeSize = user.getUserPhotos().size();
        user.getCommandManager().executeCommand(uploadPhotoCommand);
        User updatedUser = User.find.byId(1);
        int afterSize = updatedUser.getUserPhotos().size();
        assertEquals(beforeSize + 1, afterSize);
    }

    @Test
    public void testUndo() {
        int beforeSize = user.getUserPhotos().size();
        user.getCommandManager().executeCommand(uploadPhotoCommand);
        user.getCommandManager().undo();
        User updatedUser = User.find.byId(1);
        int afterSize = updatedUser.getUserPhotos().size();
        assertEquals(beforeSize, afterSize);
    }

    @Test
    public void testRedo() {
        int beforeSize = user.getUserPhotos().size();
        user.getCommandManager().executeCommand(uploadPhotoCommand);
        user.getCommandManager().undo();
        user.getCommandManager().redo();
        User redoneUser = User.find.byId(1);
        int afterSize = redoneUser.getUserPhotos().size();
        assertEquals(beforeSize + 1, afterSize);
    }
}

