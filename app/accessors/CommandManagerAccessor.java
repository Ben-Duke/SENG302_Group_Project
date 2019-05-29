package accessors;

import models.commands.General.CommandManager;

import java.util.HashMap;
import java.util.Map;

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
        commandManager.setUser(UserAccessor.getUserByEmail(email));
        commandManagers.put(email, commandManager);
        return commandManager;
    }

    public static void update(CommandManager commandManager) {
        commandManager.update();
    }

    public static void resetCommandManagers() {
        commandManagers = new HashMap<>();
    }
}
