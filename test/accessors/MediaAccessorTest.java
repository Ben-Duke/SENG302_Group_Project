package accessors;

import models.*;
import models.Album;
import models.Tag;
import models.UserPhoto;
import org.junit.Assert;
import org.junit.Test;

import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.validation.constraints.AssertTrue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MediaAccessorTest extends BaseTestWithApplicationAndDatabase {

   @Test
   public void testPhotoCanBeDeletedWithTag(){
       UserPhoto photo = UserPhoto.find().byId(1);
       Tag tag = new Tag("test");
       TagAccessor.insert(tag);
       photo.addTag(tag);
       MediaAccessor.update(photo);
       MediaAccessor.delete(photo);
       Assert.assertTrue (UserPhoto.find().byId(1)==null);
   }


    @Test
    public void testCreationDateIsNotNull(){
        UserPhoto photo = new UserPhoto("", true, true, null, null,
                null);
        Assert.assertNotNull(MediaAccessor.getMediaCreationDate(photo));
    }

    @Test
    public void testGetAllMediaForUser(){
        User user = UserAccessor.getById(2);
        UserPhoto photo = new UserPhoto("Test1", true, true, user, null,
                null);
        UserPhoto photo1 = new UserPhoto("Test2", true, true, user, null,
                null);
        MediaAccessor.insert(photo);
        MediaAccessor.insert(photo);
        List<Media> urls = MediaAccessor.getAllMediaForUser(user);
        System.out.println();
        assertEquals(2, urls.size());
    }

}
