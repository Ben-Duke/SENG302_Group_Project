package accessors;

import models.Tag;
import models.User;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class TagAccessorTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void findPendingTagsFromUserId() {

        String tagName = "pendingTagsTest";
        Integer userId = 2;

        int pendingTagsSizeBefore = TagAccessor.
                findPendingTagsFromUserId(userId).size();

        addRawTagHelper(tagName, userId).status();

        int pendingTagsSizeAfter = TagAccessor.
                findPendingTagsFromUserId(userId).size();

        assertEquals(pendingTagsSizeBefore + 1, pendingTagsSizeAfter);

    }

    @Test
    public void removePendingTagsCheckDelete() {
        int userId = 2;
        User user = UserAccessor.getById(userId);

        Tag tag = new Tag("Tag One");
        tag.getPendingUsers().add(user);
        TagAccessor.insert(tag);
        int tagId = tag.getTagId();
        TagAccessor.removePendingTagsFromUserId(userId);

        Tag refreshedTag = TagAccessor.getTagById(tagId);
        assertNull(refreshedTag);
    }

    @Test
    public void removePendingTagsCheckRemovesOnlyGivenUser() {
        int userId = 2;
        User user1 = UserAccessor.getById(userId);
        User user2 = UserAccessor.getById(userId + 1);

        Tag tag = new Tag("Tag One");
        tag.getPendingUsers().add(user1);
        tag.getPendingUsers().add(user2);
        TagAccessor.insert(tag);
        int tagId = tag.getTagId();
        TagAccessor.removePendingTagsFromUserId(userId);

        Tag refreshedTag = TagAccessor.getTagById(tagId);
        assertEquals(tag, refreshedTag);
    }

    @Test
    public void removePendingTagsWithNoUsersOrItems() {
        Tag tag = new Tag("Empty tag");
        TagAccessor.insert(tag);
        int tagId = tag.getTagId();
        TagAccessor.removePendingTagsFromUserId(0);

        Tag refreshedTag = TagAccessor.getTagById(tagId);
        assertNull(refreshedTag);
    }

    @Test
    public void clearUnneededTagsWithOneUnneededTag() {
        Tag tag = new Tag("Empty tag");
        TagAccessor.insert(tag);
        int tagId = tag.getTagId();
        TagAccessor.clearUnneededTags();

        Tag refreshedTag = TagAccessor.getTagById(tagId);
        assertNull(refreshedTag);
    }

    private Result addRawTagHelper(String tagName, Integer userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/tags");
        if (tagName != null) {
            Map<String, String> data = new HashMap<>();
            data.put("tag", tagName);
            request.bodyJson(Json.toJson(data));
        }
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }
}