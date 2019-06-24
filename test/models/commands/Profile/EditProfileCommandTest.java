package models.commands.Profile;

import controllers.ApplicationManager;
import factories.LoginFactory;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class EditProfileCommandTest extends BaseTestWithApplicationAndDatabase {
    private EditProfileCommand editProfileCommand;
    private Database database;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate birthDate = LocalDate.parse("1995-04-01", formatter);
    private LoginFactory loginFactory = new LoginFactory();

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
        User user = User.find.byId(2);
        user.setFName("Logan");
        user.setLName("Paul");
        user.setGender("Female");
        user.setDateOfBirth(birthDate);
        user.setEmail("loganpaul@gmail.com");
        user.hashAndSetPassword("its everyday bro");
        editProfileCommand =
                new EditProfileCommand(user);
    }

    @Override
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Test
    public void testExecute(){
        editProfileCommand.execute();
        User updatedUser = User.find.byId(2);
        assertEquals("Logan", updatedUser.getFName());
        assertEquals("Paul", updatedUser.getLName());
        assertEquals("Female", updatedUser.getGender());
        assertEquals(birthDate, updatedUser.getDateOfBirth());
        assertEquals("loganpaul@gmail.com", updatedUser.getEmail());
        assertTrue(loginFactory.isPasswordMatch(updatedUser.getEmail(),
                "its everyday bro"));
    }

    @Test
    public void testUndo(){
        testExecute();
        editProfileCommand.undo();
        User undoUser = User.find.byId(2);
        assertEquals("Gavin", undoUser.getFName());
        assertEquals("Ong", undoUser.getLName());
        assertEquals("Male", undoUser.getGender());
        LocalDate expectedBirthdate = LocalDate.parse("1998-08-23", formatter);
        assertEquals(expectedBirthdate, undoUser.getDateOfBirth());
        assertEquals("testuser1@uclive.ac.nz", undoUser.getEmail());
        assertTrue(loginFactory.isPasswordMatch(undoUser.getEmail(),
                "test"));
    }

    @Test
    public void testRedo(){
        editProfileCommand.execute();
        editProfileCommand.undo();
        editProfileCommand.redo();
        User updatedUser = User.find.byId(2);
        assertEquals("Logan", updatedUser.getFName());
        assertEquals("Paul", updatedUser.getLName());
        assertEquals("Female", updatedUser.getGender());
        assertEquals(birthDate, updatedUser.getDateOfBirth());
        assertEquals("loganpaul@gmail.com", updatedUser.getEmail());
        assertTrue(loginFactory.isPasswordMatch(updatedUser.getEmail(),
                "its everyday bro"));
    }
}