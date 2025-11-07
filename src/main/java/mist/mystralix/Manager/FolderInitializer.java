package mist.mystralix.Manager;

import java.io.File;
import java.util.Objects;

public class FolderInitializer {

    public void initializeEssentialFolders() {
        File path = new File(Objects.requireNonNull(FolderInitializer.class.getResource("/")).getPath());

        File folder = new File(path + "\\tasks");
        if(!folder.exists()){
            boolean created = folder.mkdirs();
            String folderCreatedComment = created ? "Folder has been created!" : "Folder could not be created!";
            System.out.println(folderCreatedComment);
        } else {
            System.out.println("Folder already exists!");
        }
    }

}