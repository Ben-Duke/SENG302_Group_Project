package accessors;

import models.commands.general.CommandManager;

import java.util.HashMap;
import java.util.Map;

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
        commandManagers.put(email, commandManager);
        return commandManager;
    }

    public static void update(CommandManager commandManager) {
        commandManager.update();
    }
}
