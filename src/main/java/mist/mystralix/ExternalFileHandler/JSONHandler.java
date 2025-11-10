package mist.mystralix.ExternalFileHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import mist.mystralix.Objects.Task;
import mist.mystralix.Objects.UserCounter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    public <K, V> HashMap<K, V> getFileContentsHashMap(
            File file,
            Class<K> firstTypeClass,
            Class<V> secondTypeClass
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
                file,
                TypeFactory
                        .defaultInstance()
                        .constructMapType(HashMap.class, firstTypeClass, secondTypeClass)
        );
    }

    public void setUserTasksInFile(File file, HashMap<Integer, Task> userTasks) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, userTasks);
    }

    public void setUserCounterInFile(File file, HashMap<String, UserCounter> existingCounter) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file, existingCounter);
    }
}