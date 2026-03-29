package org.example.library.storage;

import java.nio.file.Path;
import java.util.Set;

public interface IndexStorage {
    void addTokens(Set<String> tokens, Path path);
    void removeToken(String token);
    void removePath(Path path);
    Set<Path> search(String token);
}