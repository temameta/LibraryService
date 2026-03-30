package org.example.library.api;

import org.example.library.monitor.FileMonitor;
import org.example.library.monitor.FileMonitorImpl;
import org.example.library.processor.FileProcessor;
import org.example.library.processor.FileProcessorImpl;
import org.example.library.reader.PlainTextReader;
import org.example.library.reader.TextReader;
import org.example.library.storage.InMemoryIndex;
import org.example.library.storage.IndexStorage;
import org.example.library.tokenizer.RegexTokenizer;
import org.example.library.tokenizer.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

public class Library implements AutoCloseable {
    private final FileMonitor monitor;
    private final FileProcessor processor;
    private final Set<TextReader> readers;
    private final IndexStorage index;
    private final Tokenizer tokenizer;

    public Library(String regex) {
        this(new RegexTokenizer(regex));
    }

    public Library(Tokenizer tokenizer) {
        this(tokenizer, Set.of(new PlainTextReader()));
    }

    public Library(Tokenizer tokenizer, Set<TextReader> readers) {
        this.index = new InMemoryIndex();
        this.tokenizer = tokenizer;
        this.readers = readers;
        this.processor = new FileProcessorImpl(this.tokenizer, this.index, this.readers);
        try {
            this.monitor = new FileMonitorImpl(this.processor);
            this.monitor.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize FileMonitor\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void indexDirectory(Path path) {
        path = path.toAbsolutePath().normalize();
        if (!Files.isDirectory(path)) {
            System.out.println("Not a directory");
            return;
        }
        try {
            Files.walk(path).filter(Files::isRegularFile).forEach(processor::indexFile);
            monitor.registerDirectory(path);
        } catch (IOException e) {
            System.out.println("Error while indexing directory\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void indexFile(Path path) {
        path = path.toAbsolutePath().normalize();
        if (Files.isRegularFile(path))  {
            processor.indexFile(path);
            monitor.registerDirectory(path.getParent());
        }
        else System.out.println("Not a file");
    }

    public Set<Path> search(String word) {
        return index.search(word);
    }

    @Override
    public void close() {
        monitor.stop();
    }
}
