package org.example.library.core;

import lombok.RequiredArgsConstructor;
import org.example.library.reader.TextReader;
import org.example.library.tokenizator.Tokenizer;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
public class FileProcessor {
    private final Tokenizer tokenizer;
    private final IndexStorage index;
    private final Set<TextReader> readers;

    public void indexFile(String path) {
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

    private String getExtension(String path) {
        return path.substring(path.lastIndexOf("."));
    }
}
