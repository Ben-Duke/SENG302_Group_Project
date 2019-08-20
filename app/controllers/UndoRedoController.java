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


    /** Handles undo actions and sends information about the
     * corresponding action back to view
     *
     * @param request The HTTP request
     * @return If undo action is successful shows message.
     */
    public Result undo(Http.Request request) {

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        commandManager = user.getCommandManager();
        if(commandManager.isUndoStackEmpty()){
            return noContent();
        }
        String result = commandManager.undo();
        return ok(result);
    }

    /**
     * Handles redo actions and sends information about the
     * corresponding action back to view
     *
     * @param request The HTTP request
     * @return If redo action is successful shows message.
     */
    public Result redo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        commandManager = user.getCommandManager();
        if(commandManager.isRedoStackEmpty()){
            return noContent();
        }
        String result = commandManager.redo();

        return ok(result);
    }
}
