package mist.mystralix.Manager;

import mist.mystralix.ExternalFileHandler.FileHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

public class Initializer {

    public void initializeEssentialFolders() {
        File path = new File(Objects.requireNonNull(Initializer.class.getResource("/")).getPath());

        File folder = new File(path + "\\tasks");
        if(!folder.exists()){
            boolean created = folder.mkdirs();
            String folderCreatedComment = created ? "Folder has been created!" : "Folder could not be created!";
            System.out.println(folderCreatedComment);
        }
    }

    public void initializeEssentialFiles() {
        File path = new File(Objects.requireNonNull(Initializer.class.getResource("/")).getPath());
        File file = new File(path + "\\task_counter_per_user.json");
        if(!file.exists()){
            try {
                boolean fileCreated = file.createNewFile();

                FileHandler fileHandler = new FileHandler();
                fileHandler.writeJSONInitializerInFile(file, "{}"); // HashSet

                String folderCreatedComment = fileCreated ? "File has been created!" : "File could not be created!";
                System.out.println(folderCreatedComment);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}