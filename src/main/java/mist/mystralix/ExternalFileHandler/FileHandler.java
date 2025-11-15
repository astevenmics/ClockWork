package mist.mystralix.ExternalFileHandler;

import mist.mystralix.Exception.FileDoesNotExistException;
import mist.mystralix.Exception.FileException;
import mist.mystralix.Exception.ReadPermissionFileException;

import java.io.File;
import java.util.Objects;

public class FileHandler {

    public File getFile(
            String fileName,
            boolean throwFileExistsException
                /*
                    This allows the file to be created if it does not exist
                    If true, throws an exception if file does not exist
                    If false, does not throw an exception, allowing different outcomes
                */
    ) throws FileException {

        // Note: Make sure that the directories do not have spaces
        File filePath = new File(Objects.requireNonNull(FileHandler.class.getResource("/")).getPath());
        File file = new File(filePath + fileName);

        if(!file.exists()) {
            if (throwFileExistsException) {
                throw new FileDoesNotExistException("File (" + fileName + ") does not exist!");
            }
            return null;
        }

        if(!file.canRead()) {
            throw new ReadPermissionFileException("File read permissions is not granted for " + fileName);
        }
        return file;
    }

}