package org.example.library.storage;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryIndex implements IndexStorage {
    // token - paths
    private final ConcurrentMap<String, Set<Path>> index = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    @Override
    public void addTokens(Set<String> tokens, Path path) {
        writeLock.lock();
        try {
            removePath(path);
            tokens.forEach(tok -> {
                index.computeIfAbsent(tok, k -> ConcurrentHashMap.newKeySet()).add(path);
            });
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removePath(Path path) {
        writeLock.lock();
        try {
            index.values().forEach(paths -> paths.remove(path));
            index.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<Path> search(String token) {
        readLock.lock();
        try {
            return index.getOrDefault(token, Collections.emptySet());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsToken(String token) {
        return index.containsKey(token);
    }
}