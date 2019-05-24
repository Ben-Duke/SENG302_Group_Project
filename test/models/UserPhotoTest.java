package models;

import org.junit.Test;

import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class UserPhotoTest extends BaseTestWithApplicationAndDatabase{

    /**
     * Tests the blue sky's version of this function being called.
     */
    @Test
    public void checkDeletionOfUserPhoto (){
        UserPhoto photo = new UserPhoto("/test",false,false,null);
        photo.save();
        int photoId = photo.getPhotoId();
        int beforeDelete = photo.find.all().size();
        photo.deletePhoto(photoId);

        int afterDelete = photo.find.all().size();
        assertEquals(afterDelete,beforeDelete-1);
    }

    /**
     * Checks that when given a bad index nothing gets deleted.
     */
    @Test
    public void checkDeletionOfUserPhotoBadIndex (){
        UserPhoto photo = new UserPhoto("/test",false,false,null);

        int beforeDelete = photo.find.all().size();
        photo.deletePhoto(10000);

        int afterDelete = photo.find.all().size();
        assertEquals(afterDelete,beforeDelete);
    }
}
