package org.example.library.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryIndex implements IndexStorage {
    // token - paths
    private ConcurrentMap<String, Set<String>> index = new ConcurrentHashMap<>();

    public void addToken(String token, Set<String> paths) {
        index.put(token, paths);
    }

    public void updateToken(String token, Set<String> paths) {
        Set<String> newPaths = index.get(token);
        newPaths.addAll(paths);
        index.put(token, newPaths);
    }

    public void removeToken(String token) {
        index.remove(token);
    }

    public void removePath(String path) {
        index.forEach((token, paths) -> {
            paths.remove(path);
        });
    }

    public Set<String> search(String token) {
        return index.getOrDefault(token, Collections.emptySet());
    }

}