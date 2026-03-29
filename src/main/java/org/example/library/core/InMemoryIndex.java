package org.example.library.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryIndex implements IndexStorage {
    // token - paths
    private final ConcurrentMap<String, Set<String>> index = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public void addTokens(Set<String> tokens, String path) {
        writeLock.lock();
        try {
            removePath(path);
            tokens.forEach(tok -> {
                if (index.containsKey(tok)) {
                    Set<String> newPaths = index.get(tok);
                    newPaths.add(path);
                    index.put(tok, newPaths);
                } else {
                    index.put(tok, Set.of(path));
                }
            });
        } finally {
            writeLock.unlock();
        }
    }


    public void removeToken(String token) {
        writeLock.lock();
        try {
            index.remove(token);
        } finally {
            writeLock.lock();
        }
    }

    public void removePath(String path) {
        writeLock.lock();
        try {
            index.forEach((token, paths) -> paths.remove(path));
        } finally {
            writeLock.unlock();
        }
    }

    public Set<String> search(String token) {
        readLock.lock();
        try {
            return index.getOrDefault(token, Collections.emptySet());
        } finally {
            readLock.unlock();
        }
    }
}