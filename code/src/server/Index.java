package code.src.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Index {
    private List<List<IndexEntry>> array;
    private static final int initialCapacity = 32;
    private static final float loadFactor = 2;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int currentSize;

    Index() {
        this.array = new ArrayList<>(initialCapacity);
        currentSize = 0;
        for (int i = 0; i < initialCapacity; i++) {
            array.add(i, null);
        }
    }

    public void resize() {
        List<List<IndexEntry>> newArray = new ArrayList<>(currentSize * 2);
        for (int i = 0; i < currentSize * 2; i++) {
            newArray.add(i, null);
        }
        for (List<IndexEntry> list : array) {
            if (list != null) {
                for (IndexEntry entry : list) {
                    int index = Math.abs(entry.getKey().hashCode()) % newArray.size();
                    if (newArray.get(index) == null) {
                        newArray.set(index, new ArrayList<IndexEntry>(List.of(entry)));
                    } else {
                        newArray.get(index).add(entry);
                    }
                }
            }
        }
        array = newArray;
    }

    public void put(String key, String value) {
        lock.writeLock().lock();
        if (currentSize / array.size() >= loadFactor) {
            resize();
        }
        int index = Math.abs(key.hashCode()) % array.size();
        if (array.get(index) == null) {
            array.set(index, new ArrayList<IndexEntry>(List.of(new IndexEntry(key, value))));
        } else {
            for (IndexEntry entry : array.get(index)) {
                if (entry.getKey().equals(key)) {
                    entry.addValue(value);
                    lock.writeLock().unlock();
                    return;
                }
            }
            array.get(index).add(new IndexEntry(key, value));
            currentSize++;
            lock.writeLock().unlock();
            return;
        }
        lock.writeLock().unlock();
    }

    public Set<String> get(String key) {
        lock.readLock().lock();
        int index = Math.abs(key.hashCode()) % array.size();
        if (array.get(index) == null) {
            lock.readLock().unlock();
            return null;
        }
        for (IndexEntry entry : array.get(index)) {
            if (entry.getKey().equals(key)) {
                lock.readLock().unlock();
                return entry.getValue();
            }
        }
        lock.readLock().unlock();
        return null;
    }

    public String getString(String key) {
        lock.readLock().lock();
        int index = Math.abs(key.hashCode()) % array.size();
        if (array.get(index) == null) {
            lock.readLock().unlock();
            return null;
        }
        for (IndexEntry entry : array.get(index)) {
            if (entry.getKey().equals(key)) {
                lock.readLock().unlock();
                return entry.getValueString();
            }
        }
        lock.readLock().unlock();
        return null;
    }

    private class IndexEntry {
        private final String key;
        private final Set<String> value;

        IndexEntry(String key, String value) {
            this.key = key;
            this.value = new HashSet<>(List.of(value));
        }

        public String getKey() {
            return key;
        }

        public Set<String> getValue() {
            return value;
        }

        public String getValueString() {
            return value.toString();
        }

        public void addValue(String value) {
            this.value.add(value);
        }
    }
}
