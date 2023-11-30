package code.src.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class IndexingHandler {
    private Map<String, Set<String>> index;
    private ArrayList<File> files;
    private int threadsAmount = 4;

    IndexingHandler(Map<String, Set<String>> index, ArrayList<File> files) {
        this.index = index;
        this.files = files;
    }

    IndexingHandler(Map<String, Set<String>> index, ArrayList<File> files, int threadsAmount) {
        this.index = index;
        this.files = files;
        this.threadsAmount = threadsAmount;
    }

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(threadsAmount);
        ArrayList<FileIndexer> tasks = new ArrayList<>();
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        int step = files.size() / threadsAmount;
        if (step == 0) {
            tasks.add(new FileIndexer(index, files));
        } else {
            for (int i = 0; i < threadsAmount; i++) {
                tasks.add(new FileIndexer(index, new ArrayList<File>(files.subList(i * step, (i + 1) * step))));
            }
        }
        try {
            Thread.sleep(5000);
            System.out.println("Awakened");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (FileIndexer task : tasks) {
            completionService.submit(task);
        }
        for (int i = 0; i < threadsAmount; i++) {
            try {
                Future<Void> future = completionService.poll(30, TimeUnit.SECONDS);
                if (future == null) {
                    break;
                }
                future.get();
            } catch (CancellationException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }
}
