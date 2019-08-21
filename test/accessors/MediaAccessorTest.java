package accessors;


import models.Tag;
import models.UserPhoto;
import org.junit.Assert;
import org.junit.Test;

import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.validation.constraints.AssertTrue;

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

}
