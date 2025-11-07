package mist.mystralix.ExternalFileHandler;

import mist.mystralix.Exception.FileDoesNotExistException;
import mist.mystralix.Exception.FileException;
import mist.mystralix.Exception.ReadPermissionFileException;
import mist.mystralix.Objects.TaskHandler;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public File getUserTaskFile(User user) throws FileException {
        String userTaskFileName = "\\tasks\\" + user.getId() + ".json";
        File file;

        try {
            FileHandler fileHandler = new FileHandler();
            file = fileHandler.getFile(userTaskFileName, false);
            if(Objects.isNull(file)) {

                File path = new File(Objects.requireNonNull(TaskHandler.class.getResource("/")).getPath());
                file = new File(path + userTaskFileName);

                boolean fileCreated = file.createNewFile();

                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write("[]");
                }  catch(IOException e) {
                    System.out.println(e.getMessage());
                }

                if(!fileCreated) { return null; }
            }
        } catch (IOException | FileException e) {
            throw new RuntimeException(e.getMessage());
        }
        return file;
    }

}