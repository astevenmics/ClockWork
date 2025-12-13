package mist.mystralix.infrastructure.file;

import mist.mystralix.infrastructure.exception.FileDoesNotExistException;
import mist.mystralix.infrastructure.exception.FileException;
import mist.mystralix.infrastructure.exception.ReadPermissionFileException;

import java.io.File;
import java.util.Objects;

public class FileHandler {

    public File getFile(String fileName, boolean throwFileExistsException) throws FileException {

        File filePath = new File(
                Objects.requireNonNull(
                        FileHandler.class.getResource("/")
                ).getPath()
        );

        File file = new File(filePath + fileName);

        if (!file.exists()) {
            if (throwFileExistsException) {
                throw new FileDoesNotExistException(
                        "File (" + fileName + ") does not exist!"
                );
            }
            return null;
        }

        if (!file.canRead()) {
            throw new ReadPermissionFileException(
                    "File read permission is not granted for " + fileName
            );
        }

        return file;
    }
}