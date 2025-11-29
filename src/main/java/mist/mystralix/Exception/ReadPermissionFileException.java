package mist.mystralix.Exception;

/**
 * Exception thrown when a file exists but cannot be read due to insufficient
 * permissions or OS-level restrictions.
 *
 * <p>This is used by {@code FileHandler} when a file is present but unreadable.</p>
 *
 * <p>Example:</p>
 * <pre>
 *   if (!file.canRead()) {
 *       throw new ReadPermissionFileException("Cannot read settings.json");
 *   }
 * </pre>
 */
public class ReadPermissionFileException extends FileException {

    /**
     * Constructs a new {@code ReadPermissionFileException} with a descriptive message.
     *
     * @param message explanation of the permission issue
     */
    public ReadPermissionFileException(String message) {
        super(message);
    }
}
