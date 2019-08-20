package utilities;

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
    }
}
