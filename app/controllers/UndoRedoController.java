package controllers;

import models.User;
import models.commands.CommandManager;
import org.slf4j.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import utilities.UtilityFunctions;
import views.html.responses.unauthorizedPage;

/** Controller to handle undo/redo of actions */
public class UndoRedoController extends Controller {
    private final Logger logger = UtilityFunctions.getLogger();
    private CommandManager commandManager;

    public Result undo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized(unauthorizedPage.render());
        }

        logger.debug("user id" + Integer.toString(user.getUserid()));

        logger.debug(Integer.toString(CommandManager.find.all().size()));
        commandManager = user.getCommandManager();
        logger.debug(user.toString());
        logger.debug(Boolean.toString(commandManager == null));
        logger.debug("command manager " + commandManager);
        commandManager.undo();

        return ok();
    }

    public Result redo(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized(unauthorizedPage.render());
        }

        commandManager = user.getCommandManager();
        commandManager.redo();

        return ok();
    }
}
