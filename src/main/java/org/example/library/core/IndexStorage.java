package org.example.library.core;

import java.util.Set;

public interface IndexStorage {
    void addToken(String token, Set<String> paths);
    void updateToken(String token, Set<String> paths);
    void removeToken(String token);
    void removePath(String path);
    Set<String> search(String token);
}