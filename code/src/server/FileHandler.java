package code.src.server;

import java.io.File;
import java.util.ArrayList;

public class FileHandler {
    public static ArrayList<File> getFiles(String path) {
        ArrayList<File> files = new ArrayList<>();
        addFile(new File(path), files);
        return files;
    }

    private static void addFile(File file, ArrayList<File> files) {
        if (file.isDirectory()) {
            File[] filesInDir = file.listFiles();
            for (File fileInDir : filesInDir) {
                addFile(fileInDir, files);
            }
        } else {
            files.add(file);
        }
    }
}
