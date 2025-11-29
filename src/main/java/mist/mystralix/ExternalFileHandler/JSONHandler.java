package mist.mystralix.ExternalFileHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * Utility class for reading JSON files into Java collections using Jackson.
 *
 * <p>This class provides a strongly typed method for converting a JSON file
 * into a {@link HashSet} of any given type.</p>
 *
 * <p>Usage example:</p>
 *
 * <pre>
 *     JSONHandler jsonHandler = new JSONHandler();
 *     HashSet<String> words = jsonHandler.getFileContentsHashSet(file, String.class);
 * </pre>
 */
public class JSONHandler {

    /**
     * Reads the specified JSON file and converts it into a {@link HashSet}
     * containing objects of the provided type.
     *
     * <p>The JSON file must contain an array-like collection structure,
     * for example:</p>
     *
     * <pre>
     * [
     *   "word1",
     *   "word2",
     *   "word3"
     * ]
     * </pre>
     *
     * @param file      the JSON file to read
     * @param typeClass the type of elements stored in the JSON array
     * @param <T>       generic type inferred from {@code typeClass}
     *
     * @return a {@link HashSet} containing all elements parsed from the file
     * @throws IOException if the file cannot be read or parsing fails
     */
    public <T> HashSet<T> getFileContentsHashSet(File file, Class<T> typeClass) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                file,
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(HashSet.class, typeClass)
        );
    }
}
