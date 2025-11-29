package mist.mystralix.Exception;

/**
 * Base exception type for all file-related errors within the application.
 *
 * <p>This class extends {@link Exception} and acts as the parent for more
 * specific file-handling exceptions such as:</p>
 * <ul>
 *     <li>{@link FileDoesNotExistException}</li>
 *     <li>{@link ReadPermissionFileException}</li>
 * </ul>
 *
 * <p>All file-related failures inside the external file-handling utility are
 * wrapped using this type to provide a unified error category.</p>
 */
public class FileException extends Exception {

    /**
     * Constructs a new {@code FileException} with the provided detail message.
     *
     * @param message a descriptive error message indicating the cause
     */
    public FileException(String message) {
        super(message);
    }
}
