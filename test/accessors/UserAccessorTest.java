package accessors;

import models.User;
import models.UserPhoto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Class to JUnit test the UserAccessor class.
 */
public class UserAccessorTest extends BaseTestWithApplicationAndDatabase {

    @Before
    public void setup() {
        TestDatabaseManager.clearAllData();

        User user = new User("gon12_2@uclive.ac.nz", "hunter22");
        user.save();
    }

    /**
     * Checks the getUsersFromEmail method returns an empty list when checking
     * for users with the empty string as an email address.
     */
    @Test
    public void getUsersFromEmail_checkHasNoUsers_emptyEmailAddress() {
        List<User> users = UserAccessor.getUsersFromEmail("");
        assertEquals(0, users.size());
    }

    /**
     * Checks the getUsersFromEmail method returns an empty list when searching
     * for users with an email address none own.
     */
    @Test
    public void getUsersFromEmail_checkHasNoUsers_NonExistantEmail() {
        List<User> users = UserAccessor.getUsersFromEmail("never@an.email");
        assertEquals(0, users.size());
    }

    /**
     * Checks the getUsersFromEmail method returns a list of length one when
     * searching for users with an email that one user has.
     */
    @Test
    public void getUsersFromEmail_checkHasOneUser_ExistingEmail() {
        List<User> users = UserAccessor.getUsersFromEmail("gon12_2@uclive.ac.nz");
        assertEquals(1, users.size());
    }

    /**
     * Checks the getProfilePhoto method gets the correct url of their profile photo.
     */
    @Test
    public void getProfilePhoto_withProfilePhoto_checkURL() {
        String photoURL = "/test/url";
        UserPhoto profilePic = new UserPhoto(photoURL, true,
                                            true, User.find.byId(1));
        profilePic.save();

        UserPhoto photo = UserAccessor.getProfilePhoto(User.find.byId(1));
        assertTrue(photo.getUrl().equals(photoURL));
    }

    /**
     * Checks the getProfilePhoto method gets the correct url of their profile photo,
     * when they have other non profile photos.
     */
    @Test
    public void getProfilePhoto_withProfilePhotoAndOtherPhotos_checkURL() {
        String profilePhotoURL = "/test/url";
        UserPhoto profilePic = new UserPhoto(profilePhotoURL, true,
                true, User.find.byId(1));
        profilePic.save();

        UserPhoto nonProfilePic = new UserPhoto("/not/profile/pic", true,
                false, User.find.byId(1));
        nonProfilePic.save();

        UserPhoto nonProfilePicPrivate = new UserPhoto("/not/profile/pic/2",
                false, false, User.find.byId(1));
        nonProfilePicPrivate.save();

        UserPhoto photo = UserAccessor.getProfilePhoto(User.find.byId(1));
        assertTrue(photo.getUrl().equals(profilePhotoURL));
    }

    /**
     * Checks the getProfilePhoto method gets the correct url of their profile photo,
     * when they have other non profile photos.
     */
    @Test
    public void getProfilePhoto_withNoProfilePicButOtherPhotos_checkIsNull() {
        UserPhoto nonProfilePic = new UserPhoto("/not/profile/pic", true,
                false, User.find.byId(1));
        nonProfilePic.save();

        UserPhoto nonProfilePicPrivate = new UserPhoto("/not/profile/pic/2",
                false, false, User.find.byId(1));
        nonProfilePicPrivate.save();

        UserPhoto photo = UserAccessor.getProfilePhoto(User.find.byId(1));
        assertNull(photo);
    }

    /**
     * Checks the getProfilePhoto method returns null when a user has no profile
     * photo.
     */
    @Test
    public void getProfilePhoto_without_checkIsNull() {
        UserPhoto photo = UserAccessor.getProfilePhoto(User.find.byId(1));
        assertNull(photo);
    }
}