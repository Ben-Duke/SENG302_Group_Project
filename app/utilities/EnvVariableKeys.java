package utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * All the built in variable keys for the .env file.
 */
public enum EnvVariableKeys {
    GOOGLE_MAPS_API_KEY {
        public String toString() {
            return "GOOGLE_MAPS_API_KEY";
        }
    },
    EVENTFINDA_API_KEY_USERNAME {
        public String toString() {
            return "EVENTFINDA_API_KEY_USERNAME";
        }
    },
    EVENTFINDA_API_KEY_PASSWORD {
        public String toString() {
            return "EVENTFINDA_API_KEY_PASSWORD";
        }
    },
    ADMIN_USER_PASSWORD_DEFAULT {
        public String toString() {
            return "ADMIN_USER_PASSWORD_DEFAULT";
        }
    },
    TEST_USER_PASSWORD_DEFAULT {
        public String toString() {
            return "TEST_USER_PASSWORD_DEFAULT";
        }
    };

    /**
     * Gets a List of all EnvVariableKeys that are required to be in the .env by
     * the application on startup.
     *
     * @return A List<EnvVariableKeys>
     */
    public static List<EnvVariableKeys> getRequiredEnvVariables() {
        List<EnvVariableKeys> keys = new ArrayList<EnvVariableKeys>();
        keys.add(EnvVariableKeys.GOOGLE_MAPS_API_KEY);
        keys.add(EnvVariableKeys.EVENTFINDA_API_KEY_USERNAME);
        keys.add(EnvVariableKeys.EVENTFINDA_API_KEY_PASSWORD);
        keys.add(EnvVariableKeys.ADMIN_USER_PASSWORD_DEFAULT);
        keys.add(EnvVariableKeys.TEST_USER_PASSWORD_DEFAULT);

        return keys;
    }
}
