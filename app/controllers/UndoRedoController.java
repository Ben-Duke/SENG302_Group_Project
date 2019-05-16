package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import views.html.responses.unauthorizedPage;

/** Controller to handle undo/redo of actions */
public class UndoRedoController extends Controller {

    public Result undo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized(unauthorizedPage.render());
        }

        return ok();
    }

    public Result redo(Http.Request request) {
        return ok();
    }
}
