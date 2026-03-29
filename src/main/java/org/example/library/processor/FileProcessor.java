package org.example.library.processor;

import java.nio.file.Path;
import java.util.Set;

public interface FileProcessor {
    void indexFile(Path path);
    void deleteIndex(Path path);
    Set<Path> search(String word);
}
