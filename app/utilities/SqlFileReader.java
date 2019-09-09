package utilities;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

class SqlFileReader {
    private String filepath;
    private final Logger logger = UtilityFunctions.getLogger();

    SqlFileReader(String filename) {
        filepath = Paths.get(".").toAbsolutePath().normalize().toString() + '/' + filename;
    }

    String readFile() {
        String output = "";
        try (FileReader fileReader = new FileReader(this.filepath);) {
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);

            String line = bufferedReader.readLine();
            while (line != null) {
                output += line.trim() + '\n';
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to read file %s", e.toString()));
        }

        logger.debug(output);
        return output;
    }
}
