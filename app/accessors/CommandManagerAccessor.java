package accessors;

import models.commands.CommandManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandManagerAccessor {
    private static Map<String, CommandManager> commandManagers = new HashMap<>();

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
}
