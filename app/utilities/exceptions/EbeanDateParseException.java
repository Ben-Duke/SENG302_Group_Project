package utilities.exceptions;

/**
 * Custom exception class for Date parse exceptions. Used to turn a runtime exception
 * DateTimeParseException into a checked exception.
 */
public class EbeanDateParseException extends Exception {
    /**
     * Constructor for EbeanDateParseException class.
     * @param cause The wrapped Exception.
     */
    public EbeanDateParseException(Throwable cause) {
        super(cause);
    }
}
