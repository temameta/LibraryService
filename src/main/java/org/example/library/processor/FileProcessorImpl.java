package org.example.library.processor;

import lombok.RequiredArgsConstructor;
import org.example.library.reader.TextReader;
import org.example.library.storage.IndexStorage;
import org.example.library.tokenizator.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@RequiredArgsConstructor
public class FileProcessorImpl implements FileProcessor {
    private final Tokenizer tokenizer;
    private final IndexStorage index;
    private final Set<TextReader> readers;

    public void indexFile(Path path) {
        String extension = getExtension(path);
        for (TextReader reader : readers) {
            try {
                if (reader.supports(extension)) {
                    String text = reader.read(path);
                    Set<String> tokens = tokenizer.extractTokens(text);
                    index.addTokens(tokens, path);
                }
            } catch (IOException exception) {
                System.out.println(path + "Failed to add file to index");
            }
        }
    }

    public void deleteIndex(Path path) {
        index.removePath(path);
    }

    public Set<Path> search(String word) {
        return index.search(word);
    }

    private String getExtension(Path path) {
        return path.toString().substring(path.toString().lastIndexOf("."));
    }
}
