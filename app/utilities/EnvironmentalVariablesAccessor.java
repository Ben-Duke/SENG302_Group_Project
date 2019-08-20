package utilities;

import jdk.nashorn.internal.runtime.ParserException;

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
    private FileReader envFileReader;
    private String envFileDelimiter;
    private char commentSymbol;

    /**
     * EnvironmentalVariableAccessor constructor.
     *
     * @throws FileNotFoundException Thrown if the ".env" file does not exist in
     *         the projects root folder.
     */
    public EnvironmentalVariablesAccessor() throws FileNotFoundException {
        this.envFileReader = new FileReader(".env");
        this.envFileDelimiter = "=";
        this.commentSymbol = '#';
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
     * @param rawInputLines A List<String> of input lines, that are delimited by
     *                      an equals sign.
     * @return An AbstractMap (implemented as a subclass) mapping keys to values.
     * @throws ParserException Thrown if any line in the file does not conform to
     *          the regex "*=*".
     */
    private AbstractMap<String, String> getParsedKeyValuePairs(List<String> rawInputLines) throws ParserException {
        AbstractMap<String, String> environmentalVariables = new HashMap<String, String>();

        String key;
        String value;

        for (String line: rawInputLines) {
            boolean notEmptyLine = ! line.isEmpty();

            if (notEmptyLine) {
                boolean notCommentedOutLine = line.charAt(0) != this.commentSymbol;

                if (notCommentedOutLine) {
                    List<String> delimitedLineStrings = Arrays.asList(line.split(this.envFileDelimiter));
                    if (delimitedLineStrings.size() != 2) {
                        throw new ParserException("ERROR: line in .env contains invalid " +
                                "number of delimited strings: " + delimitedLineStrings.size());
                    }

                    key = delimitedLineStrings.get(0);
                    value = delimitedLineStrings.get(1);

                    environmentalVariables.put(key, value);
                }
            }
        }

        return environmentalVariables;
    }

    public static void main(String[] args) {
        EnvironmentalVariablesAccessor envAccessor = null;
        try {
            envAccessor = new EnvironmentalVariablesAccessor();
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist.");
        }

        List<String> lines = null;
        if (envAccessor != null) {
            try {
                lines = envAccessor.getLinesEnvFile();
            } catch (IOException e) {
                System.out.println("Error reading .env file.");
            }
        }

        AbstractMap<String, String> envVariables = null;
        if (lines != null) {
            try {
                envVariables = envAccessor.getParsedKeyValuePairs(lines);
            } catch (ParserException e) {
                System.out.println("Some line does not match \"*=*\" regex.");
            }
        }

        if (envVariables != null) {
            for (String key: envVariables.keySet()) {
                System.out.println("key: " + key + ", value: " + envVariables.get(key));
            }
        } else {
            System.out.println("Some error occurred parsing .env lines.");
        }
    }
}
