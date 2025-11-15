package mist.mystralix.ExternalFileHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class JSONHandler {

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