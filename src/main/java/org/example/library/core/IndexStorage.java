package org.example.library.core;

import java.util.Set;

public interface IndexStorage {
    void addTokens(Set<String> tokens, String path);
    void removeToken(String token);
    void removePath(String path);
    Set<String> search(String token);
}