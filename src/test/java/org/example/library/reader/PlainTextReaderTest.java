package org.example.library.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PlainTextReaderTest {
    private final TextReader reader = new PlainTextReader();
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Текст из файла считывается корректно")
    void shouldReadTextFromFileCorrectly() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "первая строка");
        String text = reader.read(file);

        assertEquals("первая строка", text);
    }

    @Test
    @DisplayName("Управляющие символы заменяются на пробелы")
    void shouldReplaceControlCharactersWithSpace() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "первая строка\nвторая строка");
        String text = reader.read(file);

        assertEquals("первая строка вторая строка", text);

        Files.writeString(file, "первая строка\tвторая строка");
        text = reader.read(file);

        assertEquals("первая строка вторая строка", text);
    }

    @Test
    @DisplayName("Пустой файл возвращает пустую строку")
    void shouldReturnEmptyStringIfEmptyFileProvided() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.createFile(file);
        String text = reader.read(file);

        assertTrue(text.isEmpty());

        Files.writeString(file,"");
        text = reader.read(file);

        assertTrue(text.isEmpty());
    }

    @Test
    @DisplayName("Передача несуществующего файла выбрасывает исключение")
    void shouldThrowIOExceptionIfNonExistingFileProvided() {
        Path file = tempDir.resolve("test.txt");
        assertThrows(IOException.class, () -> reader.read(file));
    }

    @Test
    @DisplayName("Функция supports возвращает true, если файл поддерживается, иначе - false")
    void shouldReturnTrueIfFileSupportsElseFalse() {
        assertTrue(reader.supports(".txt"));
        assertFalse(reader.supports(".pdf"));
    }
}
