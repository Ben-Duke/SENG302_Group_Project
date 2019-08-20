package utilities;

import jdk.nashorn.internal.runtime.ParserException;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;


/**
 * A Class to read the .env file in the root directory and get important data from it.
 */
public class EnvironmentalVariablesAccessor {
    private static EnvironmentalVariablesAccessor envAccessor = null;
    private static AbstractMap<String, String> envVariablesMap = null;

    private static final Logger logger = UtilityFunctions.getLogger();

    private FileReader envFileReader;
    private String envFileDelimiter;
    private char commentSymbol;

    /**
     * EnvironmentalVariableAccessor constructor.
     *
     * @throws FileNotFoundException Thrown if the ".env" file does not exist in
     *         the projects root folder.
     */
    private EnvironmentalVariablesAccessor() throws FileNotFoundException {
        this.envFileReader = new FileReader(".env");
        this.envFileDelimiter = "=";
        this.commentSymbol = '#';
    }

    /**
     * Gets the singleton instance of the EnvironmentalVariableAccessor.
     *
     * @return The EnvironmentalVariableAccessor singleton object.
     * @throws FileNotFoundException If the ".env" file does not exist in the project root.
     */
    private static EnvironmentalVariablesAccessor getInstance() throws FileNotFoundException {
        if (envAccessor == null) {
            envAccessor = new EnvironmentalVariablesAccessor();
        }

        return envAccessor;
    }

    /**
     * Gets the singleton instance of the Environmental variable map.
     *
     * @return A AbstractMap<String, String> containing all env variables, or null
     * if they cant be loaded.
     */
    private static AbstractMap<String, String> getEnvVariables() {
        if (envVariablesMap == null) {
            EnvironmentalVariablesAccessor envAccessor = null;
            try {
                envAccessor = EnvironmentalVariablesAccessor.getInstance();
            } catch (FileNotFoundException e) {
                logger.error("'.env' file not found.");
            }

            List<String> lines = null;
            if (envAccessor != null) {
                try {
                    lines = envAccessor.getLinesEnvFile();
                } catch (IOException e) {
                    logger.error("Error reading from '.env' file.");
                }
            }

            AbstractMap<String, String> envVariables = null;
            if (lines != null) {
                try {
                    envVariables = envAccessor.getParsedKeyValuePairs(lines);
                } catch (ParserException e) {
                    logger.error("Some line does not match \"*=*\" regex.");
                }
            }

            if (envVariables == null) {
                logger.error("Some error occurred parsing .env lines.");
            }

            envVariablesMap = envVariables;
        }

        return envVariablesMap;
    }

    /**
     * Gets an environmental variable.
     *
     * @param key A String representing the key to look for in the .env file.
     * @return A String representing the env variable, or null if not found.
     */
    public static String getEnvVariable(String key) {
        AbstractMap<String, String> envVariables = null;

        try {
            envVariables = EnvironmentalVariablesAccessor.getEnvVariables();
        } catch (ParserException e) {
            logger.error("Parse exception in .env file.");
        }

        if (envVariables == null) {
            logger.error("Error getting env variable.");
            return null;
        } else {
            return envVariables.get(key);
        }
    }

    /**
     * Gets all lines in the ".env" file, as a List<String>.
     *
     * @return A List<String> of all lines in the ".env" file.
     * @throws IOException Thrown due to some unknown read error (unlikely).
     *         Try retrying a few times.
     */
    private List<String> getLinesEnvFile() throws IOException {
        int bufferSizeBytes = 8192;
        BufferedReader buffReader = new BufferedReader(this.envFileReader, bufferSizeBytes);

        List<String> lines = new ArrayList<String>();
        String line = buffReader.readLine();
        while (line != null) {
            lines.add(line);
            line = buffReader.readLine();
        }

        return lines;
    }

    /**
     * Gets a map of all environmental variables in the .env file.
     *
     * A .env contaning a line with "apikey=123" will have the key "apikey" and
     * the value "123".
     *
     * Ignores empty lines in the file, and lines starting with '#'.
     *
     * If keys are duplicated the resulting value will be the last value for that key
     * in the file.
     *
     * Leading and trailing whitespace on each line is trimmed.
     * Any whitespace around the equals sign is also trimmed.
     *
     *
     * @param rawInputLines A List<String> of input lines, that are delimited by
     *                      an equals sign.
     * @return An AbstractMap (implemented as a subclass) mapping keys to values.
     */
    private AbstractMap<String, String> getParsedKeyValuePairs(List<String> rawInputLines) {
        AbstractMap<String, String> environmentalVariables = new HashMap<String, String>();

        String key;
        String value;

        for (String line: rawInputLines) {
            boolean notEmptyLine = ! line.isEmpty();

            if (notEmptyLine) {
                line = line.trim();
                boolean notCommentedOutLine = line.charAt(0) != this.commentSymbol;

                if (notCommentedOutLine) {
                    List<String> delimitedLineStrings = Arrays.asList(line.split(this.envFileDelimiter));
                    if (delimitedLineStrings.size() == 2) {
                        key = delimitedLineStrings.get(0).trim();
                        value = delimitedLineStrings.get(1).trim();

                        environmentalVariables.put(key, value);
                    }
                }
            }
        }

        return environmentalVariables;
    }

    public static void main(String[] args) {
        System.out.println("google api key=" +
                EnvironmentalVariablesAccessor.getEnvVariable("GOOGLE_MAPS_API_KEY"));
        System.out.println("eventfinda username=" +
                EnvironmentalVariablesAccessor.getEnvVariable("EVENTFINDA_API_KEY_USERNAME"));
        System.out.println("eventfinda password=" +
                EnvironmentalVariablesAccessor.getEnvVariable("EVENTFINDA_API_KEY_PASSWORD"));

        System.out.println("test key=" +
                EnvironmentalVariablesAccessor.getEnvVariable("test"));

    }
}
