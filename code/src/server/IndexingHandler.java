package code.src.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class IndexingHandler implements Runnable {
    private Map<String, Set<String>> index;
    private ArrayList<File> files;
    private int threadsAmount = 4;
    private AtomicBoolean indexed;

    IndexingHandler(Map<String, Set<String>> index, ArrayList<File> files, AtomicBoolean indexed) {
        this.index = index;
        this.files = files;
        this.indexed = indexed;
    }

    IndexingHandler(Map<String, Set<String>> index, ArrayList<File> files, int threadsAmount, AtomicBoolean indexed) {
        this.index = index;
        this.files = files;
        this.threadsAmount = threadsAmount;
        this.indexed = indexed;
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(threadsAmount);
        ArrayList<FileIndexer> tasks = new ArrayList<>();
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        int step = threadsAmount > 0 ? files.size() / threadsAmount : files.size();
        if (step < 1) {
            for (File file : files) {
                tasks.add(new FileIndexer(index, new ArrayList<File>(List.of(file))));
            }
        } else {
            for (int i = 0; i < threadsAmount; i++) {
                if (i == threadsAmount - 1 && (i + 1) * step < files.size()) {
                    tasks.add(new FileIndexer(index, new ArrayList<File>(files.subList(i * step, files.size()))));
                } else {
                    tasks.add(new FileIndexer(index, new ArrayList<File>(files.subList(i * step, (i + 1) * step))));
                }
            }
        }
        for (FileIndexer task : tasks) {
            completionService.submit(task);
        }
        for (int i = 0; i < tasks.size(); i++) {
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
        indexed.set(true);
    }
}
