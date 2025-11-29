package mist.mystralix.Exception;

/**
 * Exception thrown when a requested file cannot be found in the expected path.
 *
 * <p>This exception typically occurs when strict mode is enabled in
 * {@code FileHandler.getFile()}, indicating that the file must exist or the
 * program should treat it as an error.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *   throw new FileDoesNotExistException("File config.json does not exist!");
 * </pre>
 */
public class FileDoesNotExistException extends FileException {

    /**
     * Creates a new {@code FileDoesNotExistException} with the specified message.
     *
     * @param message a description of the missing file
     */
    public FileDoesNotExistException(String message) {
        super(message);
    }
}
