package org.example.library.monitor;

import org.example.library.processor.FileProcessor;
import org.example.library.processor.FileProcessorImpl;
import org.example.library.reader.PlainTextReader;
import org.example.library.reader.TextReader;
import org.example.library.storage.InMemoryIndex;
import org.example.library.storage.IndexStorage;
import org.example.library.tokenizer.RegexTokenizer;
import org.example.library.tokenizer.Tokenizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileMonitorTest {
    private final IndexStorage index = new InMemoryIndex();
    private final FileProcessor processor = new FileProcessorImpl(new RegexTokenizer(" "), index, Set.of(new PlainTextReader()));
    private final FileMonitor monitor = new FileMonitorImpl(processor);
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        monitor.start();
    }

    @AfterEach
    void tearDown() {
        monitor.stop();
    }

    @Test
    @DisplayName("Новый файл в отслеживаемой директории автоматически индексируется")
    void shouldAutomaticallyIndexNewFileInRegisteredDirectory() throws IOException, InterruptedException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);
        monitor.registerDirectory(dir);
        Path file = dir.resolve("file.txt");
        Files.writeString(file, "файл");
        boolean found = false;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if (!index.search("файл").isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertEquals(Set.of(file), index.search("файл"));
    }

    @Test
    @DisplayName("Изменённый файл в отслеживаемой директории автоматически индексируется заново")
    void shouldAutomaticallyIndexRefactoredFileInRegisteredDirectory() throws IOException, InterruptedException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);
        monitor.registerDirectory(dir);
        Path file = dir.resolve("file.txt");
        Files.writeString(file, "файл");
        boolean found = false;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if (!index.search("файл").isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertEquals(Set.of(file), index.search("файл"));
        Files.writeString(file, "изменение");
        found = false;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if (!index.search("изменение").isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertEquals(Set.of(file), index.search("изменение"));
    }

    @Test
    @DisplayName("Удалённый файл в отслеживаемой директории автоматически удаляется из индекса")
    void shouldAutomaticallyDeletedFileInIndexInRegisteredDirectory() throws IOException, InterruptedException {
        Path dir = tempDir.resolve("dir");
        Files.createDirectory(dir);
        monitor.registerDirectory(dir);
        Path file = dir.resolve("file.txt");
        Files.writeString(file, "файл");
        boolean found = false;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if (!index.search("файл").isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertEquals(Set.of(file), index.search("файл"));
        Files.deleteIfExists(file);
        found = false;
        for (int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if (index.search("файл").isEmpty()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
        assertTrue(index.search("файл").isEmpty());
        assertFalse(index.containsToken("файл"));
    }
}
