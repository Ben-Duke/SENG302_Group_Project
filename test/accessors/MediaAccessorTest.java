package accessors;

import com.fasterxml.jackson.databind.node.ArrayNode;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        MediaAccessor.insert(photo);
        List<Media> urls = MediaAccessor.getAllMediaForUser(user);
        assertEquals(2, urls.size());
    }

    //Will be deleted after using this for quick testing and will be used for the sorting the json return
    @Test
    public void Scrap(){
        User user = UserAccessor.getById(2);
        UserPhoto photo = new UserPhoto("Test1", true, true, user, null,
                null);
        MediaAccessor.insert(photo);
        ArrayNode urls = MediaAccessor.getUserMediaData(user);
        System.out.println(urls);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(int i =0; i < urls.size(); i++){
            LocalDateTime date = LocalDateTime.parse(urls.get(0).get("date_created").asText(),formatter);
        }

    }
}
