package controllers;

import models.User;
import models.commands.CommandManager;
import org.slf4j.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import utilities.UtilityFunctions;
import views.html.responses.*;

/** Controller to handle undo/redo of actions */
public class UndoRedoController extends Controller {
    private final Logger logger = UtilityFunctions.getLogger();
    private CommandManager commandManager;

    public Result undo(Http.Request request) {
        logger.debug("undo controller called");

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        commandManager = user.getCommandManager();
        commandManager.undo();

        return ok();
    }

    public Result redo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        commandManager = user.getCommandManager();
        commandManager.redo();

        return ok();
    }
}