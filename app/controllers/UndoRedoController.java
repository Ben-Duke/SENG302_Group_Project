package controllers;

import models.User;
import models.commands.General.CommandManager;
import org.slf4j.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import scala.collection.concurrent.Debug;
import utilities.UtilityFunctions;

/** Controller to handle undo/redo of actions */
public class UndoRedoController extends Controller {
    private final Logger logger = UtilityFunctions.getLogger();
    private CommandManager commandManager;

    public Result undo(Http.Request request) {

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        commandManager = user.getCommandManager();
        commandManager.isUndoStackEmpty();
        String result = commandManager.undo();

        return ok(result);
    }

    public Result redo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        commandManager = user.getCommandManager();
        String result = commandManager.redo();

        return ok(result);
    }
}
