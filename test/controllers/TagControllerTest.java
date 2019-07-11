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
        Result result = getPhotoTagHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagSuccessCheckData() {
        Result result = getPhotoTagHelper();
        assertEquals("[{\"tagId\":1,\"name\":\"Shrek\"}]", contentAsString(result));
    }

    private Result getPhotoTagHelper() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/1/tags")
                .session("connected", "2");
        return route(app, request);
    }

    @Test
    public void getPhotoTagMultipleCheckResponse() {
        Result result = getMultipleTagHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagMultipleCheckData() {
        Result result = getMultipleTagHelper();
        assertEquals(
            "[{\"tagId\":2,\"name\":\"UC\"},{\"tagId\":3,\"name\":\"Second Tag\"}]",
                contentAsString(result));
    }

    private Result getMultipleTagHelper() {
        addPhotoTagHelper();

        //Add a second tag
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Second Tag");
        Http.RequestBuilder putRequest = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/2/tags")
                .session("connected", "2");
        route(app, putRequest);

        Http.RequestBuilder getRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags")
                .session("connected", "2");
        return route(app, getRequest);
    }

    @Test
    public void getPhotoTagNoTagsCheckResponse() {
        Result result = getPhotoTagNoTagHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagNoTagsCheckData(){
        Result result = getPhotoTagNoTagHelper();
        assertEquals("[]", contentAsString(result));
    }

    private Result getPhotoTagNoTagHelper(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags")
                .session("connected", "2");
        return route(app, request);
    }

    @Test
    public void getPhotoTagNotLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getPhotoTagLoggedInAsWrongUser() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags")
                .session("connected", "3");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getPhotoLoggedInAsAdmin() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagNoTagExisting() {
        Result result = addPhotoTagHelper();
        assertEquals(CREATED, result.status());
    }

    @Test
    public void addPhotoTagTagExisting() {
        addPhotoTagHelper();
        // Add tag to different photo
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "UC");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/1/tags")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagTagAlreadyLinked() {
        addPhotoTagHelper();
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "UC");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/2/tags")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    private Result addPhotoTagHelper() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "UC");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/2/tags")
                .session("connected", "2");
        return route(app, request);
    }

    @Test
    public void addPhotoTagEmptyTag() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/1/tags")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void addPhotoTagNoPhoto() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Unknown photo");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/90/tags")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void addPhotoTagNotLoggedIn() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "NZ");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/1/tags");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addPhotoTagWrongUser() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Self Portrait");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/2/tags")
                .session("connected", "3");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void addPhotoTagAdmin() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "New Photo");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/1/tags")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(CREATED, result.status());
    }

}
