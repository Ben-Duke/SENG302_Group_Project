package accessors;

import models.commands.CommandManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandManagerAccessor {

    private static Map<String, CommandManager> commandManagers = new HashMap<>();

    public static Map<String, CommandManager> getCommandManagers() {
        return commandManagers;
    }

    public static CommandManager getCommandManagerByEmail(String email) {
        if (commandManagers.containsKey(email)) {
            return commandManagers.get(email);
        } else {
            return getNewCommandManager(email);
        }
    }

    private static CommandManager getNewCommandManager(String email) {
        CommandManager commandManager = new CommandManager();
        commandManagers.put(email, commandManager);
        return commandManager;
    }

    /**
     * The method to reset the Command Manager so that all the undo redo actions are cleared.
     * @param email
     */
    public static void resetCommandManager(String email) {
        CommandManager newCommandManager = new CommandManager();
        if (commandManagers.containsKey(email)) {
            commandManagers.replace(email, newCommandManager);
        } else {
            commandManagers.put(email, newCommandManager);
        }
    }

    public static void update(CommandManager commandManager) {
        commandManager.update();
    }
}
