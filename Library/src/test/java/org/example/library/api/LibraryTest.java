package org.example.library.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Полный цикл индексирования файла и его поиск отрабатывает корректно")
    void shouldHandleFullCycleFile() throws IOException {
        try (Library library = new Library(" ")) {
            Path file = tempDir.resolve("file.txt");
            Files.writeString(file, "обычный Файл");
            library.indexFile(file);
            assertEquals(Set.of(file), library.search("файл"));
        }
    }

    @Test
    @DisplayName("Полный цикл индексирования директории отрабатывает корректно")
    void shouldHandleFullCycleDirectory() throws IOException {
        try (Library library = new Library(" ")) {
            Path dir = tempDir.resolve("dir");
            Path firstFile = dir.resolve("file.txt");
            Path secondFile = dir.resolve("file1.txt");
            Path thirdFile = dir.resolve("dir1/file2.txt");
            Path fourthFile = dir.resolve("dir2/file3.txt");
            Path fifthFile = dir.resolve("dir2/file4.txt");

            Files.createDirectories(dir.resolve("dir1"));
            Files.createDirectories(dir.resolve("dir2"));
            Files.writeString(firstFile, "файл");
            Files.writeString(secondFile, "файл");
            Files.writeString(thirdFile, "файл");
            Files.writeString(fourthFile, "файл");
            Files.writeString(fifthFile, "файл");
            library.indexDirectory(dir);
            assertEquals(Set.of(firstFile, secondFile, thirdFile, fourthFile, fifthFile), library.search("файл"));
        }
    }

    @Test
    @DisplayName("Поиск после закрытия монитора файлов работает")
    void shouldSearchAfterMonitorClosed() throws IOException {
        try (Library library = new Library(" ")) {
            Path file = tempDir.resolve("file.txt");
            Files.writeString(file, "обычный Файл");
            library.indexFile(file);
            library.close();
            assertEquals(Set.of(file), library.search("файл"));
        }
    }

    @Test
    @DisplayName("Индексирование файла после изменения работает корректно")
    void shouldCorrectlyIndexFileAfterRefactor() throws IOException {
        try (Library library = new Library(" ")) {
            Path file = tempDir.resolve("file.txt");
            Files.writeString(file, "обычный Файл");
            library.indexFile(file);
            assertEquals(Set.of(file), library.search("файл"));
            assertEquals(Set.of(file), library.search("обычный"));
            Files.writeString(file, "необычный Файл");
            library.indexFile(file);
            assertEquals(Set.of(file), library.search("файл"));
            assertEquals(Set.of(file), library.search("необычный"));
            assertNotEquals(Set.of(file), library.search("обычный"));
        }
    }
}
