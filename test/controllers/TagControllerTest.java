package controllers;

import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class TagControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void getPhotoTagSuccessCheckResponse() {
        Result result = getPhotoTagHelper(1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagSuccessCheckData() {
        Result result = getPhotoTagHelper(1, 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Shrek\"}]", contentAsString(result));
    }

    @Test
    public void getPhotoTagMultipleCheckResponse() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        //Add a second tag
        addRemovePhotoTagHelper(PUT, "Second Tag", 2, 2);

        Result result = getPhotoTagHelper(2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagMultipleCheckData() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        //Add a second tag
        addRemovePhotoTagHelper(PUT, "Second Tag", 2, 2);

        Result result = getPhotoTagHelper(2, 2);
        assertEquals(
            "[{\"tagId\":2,\"name\":\"UC\"},{\"tagId\":3,\"name\":\"Second Tag\"}]",
                contentAsString(result));
    }

    private Result getMultipleTagHelper() {

    }

    @Test
    public void getPhotoTagNoTagsCheckResponse() {
        Result result = getPhotoTagHelper(2, 2)
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagNoTagsCheckData(){
        Result result = getPhotoTagHelper(2, 2);
        assertEquals("[]", contentAsString(result));
    }

    @Test
    public void getPhotoTagNotLoggedIn() {
        Result result = getPhotoTagHelper(2, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getPhotoTagLoggedInAsWrongUser() {
        Result result = getPhotoTagHelper(2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getPhotoLoggedInAsAdmin() {
        Result result = getPhotoTagHelper(2, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagNoTagExisting() {
        Result result = addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        assertEquals(CREATED, result.status());
    }

    @Test
    public void addPhotoTagTagExisting() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        Result result = addRemovePhotoTagHelper(PUT, "UC", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagTagAlreadyLinked() {
        addRemovePhotoTagHelper(PUT, "UC", 2, 2);
        Result result = addRemovePhotoTagHelper(PUT, "UC", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagEmptyTag() {
        Result result = addRemovePhotoTagHelper(PUT, "", 1, 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void addPhotoTagNoPhoto() {
        Result result = addRemovePhotoTagHelper(PUT, "Unknown photo", 90, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void addPhotoTagNotLoggedIn() {
        Result result = addRemovePhotoTagHelper(PUT, "NZ", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addPhotoTagWrongUser() {
        Result result = addRemovePhotoTagHelper(PUT, "Self Portrait", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void addPhotoTagAdmin() {
        Result result = addRemovePhotoTagHelper(PUT, "New Photo", 1, 1);
        assertEquals(CREATED, result.status());
    }

    @Test
    public void removePhotoTag() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagTagNotLinked() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagTagNotExists() {
        Result result = addRemovePhotoTagHelper(DELETE, "Finland", 1, 2);
        assertEquals(NOT_FOUND, result.status());

    }

    @Test
    public void removePhotoTagAsAdmin() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagNoSuchPhoto() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 900, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void removePhotoTagWrongUser() {
        Result result = addRemovePhotoTagHelper(DELETE,"Shrek", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void removePhotoTagNotLoggedIn() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    private Result addRemovePhotoTagHelper(String method, String tagName, Integer photoId, Integer userId) {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", tagName);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(method)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/" + photoId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result getPhotoTagHelper(Integer photoId, Integer userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/" + photoId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

}
