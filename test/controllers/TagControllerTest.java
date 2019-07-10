package controllers;

import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void getPhotoTagSuccessCheckTag() {
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
    @Ignore
    public void getPhotoLoggedInAsAdmin() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/2/tags")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    @Test
    @Ignore
    public void addPhotoTagNoTagExisting() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/photos/2/tags")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


}
