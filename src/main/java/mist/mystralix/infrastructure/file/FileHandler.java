package mist.mystralix.infrastructure.file;

import mist.mystralix.infrastructure.exception.FileDoesNotExistException;
import mist.mystralix.infrastructure.exception.FileException;
import mist.mystralix.infrastructure.exception.ReadPermissionFileException;

import java.io.File;
import java.util.Objects;

/**
 * Utility class for locating and validating files inside the application's
 * classpath resources directory.
 *
 * <p>The FileHandler ensures the following:</p>
 * <ul>
 *     <li>The file exists (optional strict mode)</li>
 *     <li>The file is readable</li>
 *     <li>The file is correctly resolved relative to the classpath root</li>
 * </ul>
 *
 * <p>This is primarily used by systems like the message filter to load external
 * JSON configuration files such as <code>censored_words.json</code>.</p>
 */
public class FileHandler {

    /**
     * Retrieves a file from the classpath resources directory.
     *
     * <p>The {@code fileName} is appended to the application's resource root.
     * Example:</p>
     *
     * <pre>
     *   getFile("/censored_words.json", true);
     * </pre>
     *
     * @param fileName the relative file path (starting with '/')
     * @param throwFileExistsException
     *        if {@code true}, a missing file results in a
     *        {@link FileDoesNotExistException};
     *        if {@code false}, a missing file returns {@code null}
     *
     * @return the resolved {@link File} instance, or {@code null} if the file
     *         does not exist and strict mode is disabled
     *
     * @throws FileException when:
     *         <ul>
     *             <li>The file does not exist and strict mode is enabled</li>
     *             <li>The file is unreadable</li>
     *             <li>The directory cannot be accessed</li>
     *         </ul>
     */
    public File getFile(String fileName, boolean throwFileExistsException) throws FileException {

        // Resolve the root directory of the classpath (/resources/)
        File filePath = new File(
                Objects.requireNonNull(
                        FileHandler.class.getResource("/")
                ).getPath()
        );

        File file = new File(filePath + fileName);

        // Handle missing files
        if (!file.exists()) {
            if (throwFileExistsException) {
                throw new FileDoesNotExistException(
                        "File (" + fileName + ") does not exist!"
                );
            }
            return null;
        }

        // Validate read permission
        if (!file.canRead()) {
            throw new ReadPermissionFileException(
                    "File read permission is not granted for " + fileName
            );
        }

        return file;
    }
}
