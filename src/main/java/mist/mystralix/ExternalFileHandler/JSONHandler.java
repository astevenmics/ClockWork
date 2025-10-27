package mist.mystralix.ExternalFileHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class JSONHandler {

    public HashSet<String> getFileContents(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Return a HashSet of strings
        return objectMapper.readValue(file, new TypeReference<>() {});
    }
}