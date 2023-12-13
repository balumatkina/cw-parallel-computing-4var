package code.src.server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class FileIndexer implements Callable<Void> {

    private Map<String, Set<String>> index;
    private ArrayList<File> files;

    FileIndexer(Map<String, Set<String>> index, ArrayList<File> files) {
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
                            if (!index.containsKey(word)) {
                                index.put(word, new HashSet<>());
                            }
                            index.get(word).add(file.getName());
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
