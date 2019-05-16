package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

/** Controller to handle undo/redo of actions */
public class UndoRedoController extends Controller {

    public Result undo(Http.Request request) {
        return ok();
    }

    public Result redo(Http.Request request) {
        return ok();
    }
}
