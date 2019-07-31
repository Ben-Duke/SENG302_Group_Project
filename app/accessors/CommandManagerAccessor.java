package accessors;

import models.commands.General.CommandManager;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to handle accessing the Command Managers
 */
public class CommandManagerAccessor {

    // Private constructor to hide the implicit public one
    private CommandManagerAccessor() {
        throw new IllegalStateException("Utility class");
    }

    private static Map<String, CommandManager> commandManagers = new HashMap<>();

    /** returns a map of the current Command Managers
     * @return Map
     */
    public static Map<String, CommandManager> getCommandManagers() {
        return commandManagers;
    }

    /** Returns a Command Manager matching an email
     *  If no matching email is found returns a new Command Manager
     * @param email A given email address for a user
     * @return Command Manager
     */
    public static CommandManager getCommandManagerByEmail(String email) {
        if (commandManagers.containsKey(email)) {
            return commandManagers.get(email);
        } else {
            return getNewCommandManager(email);
        }
    }

    /** Creates a new Command Manager object for a user matching a given email
     * @param email A given email address for a user
     * @return New command manger object
     */
    private static CommandManager getNewCommandManager(String email) {
        CommandManager commandManager = new CommandManager();
        commandManager.setUser(UserAccessor.getUserByEmail(email));
        commandManagers.put(email, commandManager);
        return commandManager;
    }

    /** Updates a given Command Manager in the database
     * @param commandManager A Command Manager to update
     */
    public static void update(CommandManager commandManager) {
        commandManager.update();
    }

    /** Clears all Command Managers */
    public static void resetCommandManagers() {
        commandManagers = new HashMap<>();
    }
}
