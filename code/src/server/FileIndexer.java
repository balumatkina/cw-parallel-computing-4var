package code.src.server;

import java.util.concurrent.Callable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileIndexer implements Callable<Void> {

    private Index index;
    private ArrayList<File> files;

    FileIndexer(Index index, ArrayList<File> files) {
        this.index = index;
        this.files = files;
    }

    @Override
    public Void call() {
        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\W");
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            index.put(word, file.toString());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
