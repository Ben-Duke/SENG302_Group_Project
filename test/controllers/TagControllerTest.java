package controllers;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class TagControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void getPhotoTagSuccessCheckResponse() {
        Result result = getPhotoTagHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagSuccessCheckTag() {
        Result result = getPhotoTagHelper();
        assertEquals("", contentAsString(result));
    }

    private Result getPhotoTagHelper() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/1/tags")
                .session("connected", "2");
        return route(app, request);
    }
}
