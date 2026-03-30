package org.example.library.processor;

import org.example.library.reader.PlainTextReader;
import org.example.library.reader.TextReader;
import org.example.library.storage.InMemoryIndex;
import org.example.library.storage.IndexStorage;
import org.example.library.tokenizer.RegexTokenizer;
import org.example.library.tokenizer.Tokenizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorTest {
    private final IndexStorage index = new InMemoryIndex();
    private final Tokenizer tokenizer = new RegexTokenizer(" ");
    private final Set<TextReader> readers = Set.of(new PlainTextReader());
    private final FileProcessor processor = new FileProcessorImpl(tokenizer, index, readers);

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Индексация файла работает корректно")
    void shouldCorrectlyIndexFile() throws IOException {
        Path file = tempDir.resolve("file.txt");
        Files.writeString(file, "Тестовый файл");
        processor.indexFile(file);
        assertEquals(Set.of(file), index.search("файл"));
        Files.writeString(file, "Тестовый текст");
        processor.indexFile(file);
        assertEquals(Set.of(file), index.search("текст"));
        assertNotEquals(Set.of(file), index.search("файл"));
    }

    @Test
    @DisplayName("Неподдерживаемый файл игнорируется")
    void shouldHandleUnsupportedFile() throws IOException {
        Path file = tempDir.resolve("file.pdf");
        Files.createFile(file);
        assertDoesNotThrow(() -> processor.indexFile(file));
        assertTrue(index.search("слово").isEmpty());
    }

    @Test
    @DisplayName("Несуществующий файл игнорируется")
    void shouldHandleNonExistingFile() {
        Path file = tempDir.resolve("file.txt");
        assertDoesNotThrow(() -> processor.indexFile(file));
    }
}
