package accessors;
import models.*;
import models.Tag;
import models.UserPhoto;
import org.junit.Assert;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;
import java.time.LocalDateTime;
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
        MediaAccessor.insert(photo);
        List<Media> urls = MediaAccessor.getAllMediaForUser(user);
        assertEquals(2, urls.size());
    }

    @Test
    public void testGetFollowingMedia(){
        User user = UserAccessor.getById(2);
        List<Media> beforeMedia = MediaAccessor.getFollowingMedia(user, 0, 2, LocalDateTime.now().plusDays(1));
        UserPhoto photo = new UserPhoto("Test1", true, true, UserAccessor.getById(3), null,
                null);
        MediaAccessor.insert(photo);
        UserPhoto photo1 = new UserPhoto("Test2", true, true, UserAccessor.getById(4), null,
                null);
        MediaAccessor.insert(photo1);
        user.addToFollowing(FollowAccessor.getFollowById(3));
        user.addToFollowing(FollowAccessor.getFollowById(4));
        UserAccessor.update(user);
        user = UserAccessor.getById(user.getUserid());

        List<Media> media = MediaAccessor.getFollowingMedia(user, 0, 2, LocalDateTime.now().plusDays(1));

        assertEquals(beforeMedia.size() + 2, media.size());
    }
}
