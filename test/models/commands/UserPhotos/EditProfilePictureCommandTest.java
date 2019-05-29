package models.commands.UserPhotos;

import accessors.UserAccessor;
import akka.stream.impl.io.OutputStreamSourceStage;
import factories.UserFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;

public class EditProfilePictureCommandTest extends BaseTestWithApplicationAndDatabase {

    @Before
    public void checkNoCurrentProfilePicture() {
        assertNull(UserAccessor.getUserProfilePictureByUserId(3));
    }

    @Test
    public void editProfilePictureCommandCheckResponse() {
        Result editResult = editProfilePictureRequest();
        assertEquals(SEE_OTHER, editResult.status());
    }

    @Test
    public void editProfilePictureCommandCheckPicture() {
        editProfilePictureRequest();
        assertEquals(3, UserAccessor.getUserProfilePictureByUserId(3).getPhotoId());
    }

    @Test
    public void undoEditProfilePictureCommandCheckResponse() {
        editProfilePictureRequest();
        Result undoResult = undoEditProfilePictureRequest();
        assertEquals(OK, undoResult.status());
    }

    @Test
    public void undoEditProfilePictureCommandCheckProfilePicture() {
        editProfilePictureRequest();
        undoEditProfilePictureRequest();
        assertNull(UserAccessor.getUserProfilePictureByUserId(3));
    }

    @Test
    public void redoEditProfilePictureCommandCheckResponse() {
        editProfilePictureRequest();
        undoEditProfilePictureRequest();
        Result redoResult = redoEditProfilePictureRequest();
        assertEquals(OK, redoResult.status());
    }

    @Test
    public void redoEditProfilePictureCommandCheckProfilePicture() {
        editProfilePictureRequest();
        undoEditProfilePictureRequest();
        redoEditProfilePictureRequest();
        assertEquals(3, UserAccessor.getUserProfilePictureByUserId(3).getPhotoId());
    }

    private Result editProfilePictureRequest() {
        Http.RequestBuilder editRequest = Helpers.fakeRequest()
                .method(Helpers.PUT)
                .uri("/users/home/setProfilePicture/3")
                .session("connected", "3");
        return Helpers.route(app, editRequest);
    }

    private Result undoEditProfilePictureRequest() {
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(Helpers.PUT)
                .uri("/undo")
                .session("connected", "3");
        return Helpers.route(app, undoRequest);
    }

    private Result redoEditProfilePictureRequest() {
        Http.RequestBuilder redoRequest = Helpers.fakeRequest()
                .method(Helpers.PUT)
                .uri("/redo")
                .session("connected", "3");
        return Helpers.route(app, redoRequest);
    }
}
