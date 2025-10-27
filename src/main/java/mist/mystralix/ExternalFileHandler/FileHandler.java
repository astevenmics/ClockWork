package mist.mystralix.ExternalFileHandler;

import mist.mystralix.Exception.FileDoesNotExistException;
import mist.mystralix.Exception.FileException;
import mist.mystralix.Exception.ReadPermissionFileException;

import java.io.File;
import java.util.Objects;

public class FileHandler {

    public File getFile(String fileName) throws FileException {

        // Make sure that the files and folders do not have spaces
        String filePath = Objects.requireNonNull(FileHandler.class.getClassLoader().getResource(fileName)).getPath();
        File file = new File(filePath);
        if(!file.exists()) { throw new FileDoesNotExistException("File (" + fileName + ") does not exist!"); }
        if(!file.canRead()) { throw new ReadPermissionFileException("File read permissions is not granted for " + fileName); }
        return file;
    }

}