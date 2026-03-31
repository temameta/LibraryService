package org.example.library.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryIndexTest {
    private IndexStorage index;

    @BeforeEach
    void setUp() {
         index = new InMemoryIndex();
    }

    @Test
    @DisplayName("Индекс корректно возвращает пути")
    void shouldReturnCorrectPaths() {
        Path file = Path.of("file");
        index.addTokens(Set.of("кот"), file);

        assertEquals(Set.of(file), index.search("кот"));
    }

    @Test
    @DisplayName("При попытке поиска несуществующего токена возвращает пустой набор путей")
    void shouldEmptySetIfNonExistingTokenProvided() {
        assertTrue(index.search("nonExistingToken").isEmpty());
    }

    @Test
    @DisplayName("При поиске слова возвращает все файлы с этим словом")
    void shouldReturnAllPaths() {
        Path firstFile = Path.of("firstFile");
        Path secondFile = Path.of("secondFile");
        index.addTokens(Set.of("кот"), firstFile);
        index.addTokens(Set.of("кот"), secondFile);

        assertEquals(Set.of(firstFile, secondFile), index.search("кот"));
    }

    @Test
    @DisplayName("Индекс корректно удаляет файлы")
    void shouldCorrectlyRemoveFiles() {
        Path firstFile = Path.of("firstFile");
        Path secondFile = Path.of("secondFile");
        index.addTokens(Set.of("кот"), firstFile);
        index.addTokens(Set.of("кот"), secondFile);
        index.removePath(firstFile);

        assertEquals(Set.of(secondFile), index.search("кот"));

        index.removePath(secondFile);

        assertTrue(index.search("кот").isEmpty());
    }

    @Test
    @DisplayName("Индекс удаляет токен, если он не находится ни в одном из файлов")
    void shouldRemoveTokenIfItIsNotLocatedAnywhere() {
        Path file = Path.of("fileToRemove");
        index.addTokens(Set.of("кот"), file);
        index.removePath(file);

        assertFalse(index.containsToken("кот"));
    }

    @Test
    @DisplayName("Индекс корректно изменяет файлы для токена")
    void shouldCorrectlyUpdateFiles() {
        Path file = Path.of("file");
        index.addTokens(Set.of("кот", "собака"), file);
        index.addTokens(Set.of("кот", "хомяк"), file);

        assertEquals(Set.of(file), index.search("хомяк"));
        assertNotEquals(Set.of(file), index.search("собака"));
    }

    @Test
    @DisplayName("100 потоков добавляют файлы параллельно без потерь данных")
    void shouldHandleConcurrentWrites() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        Set<Path> paths = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < threadCount; i++) {
            final int num = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Path file = Path.of("/docs/file" + num + ".txt");
                paths.add(file);
                index.addTokens(Set.of("многопоточность"), file);
            });
        }
        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        Set<Path> result = index.search("многопоточность");
        assertEquals(threadCount, result.size());
        assertTrue(result.containsAll(paths));
    }

    @Test
    @DisplayName("100 потоков ищут файлы параллельно без выбрасывания исключений")
    void shouldHandleConcurrentReads() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        for (int i = 0; i < threadCount; i++) {
            Path file = Path.of("/docs/file" + i + ".txt");
            index.addTokens(Set.of("многопоточность"), file);
        }
        List<Set<Path>> searches = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                searches.add(index.search("многопоточность"));
            });
        }
        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(threadCount, searches.size());
    }

    @Test
    @DisplayName("50 потоков ищут файлы, 50 потоков пишут параллельно без выбрасывания исключений")
    void shouldHandleConcurrentReadsAndWrites() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        for (int i = 0; i < threadCount; i++) {
            Path file = Path.of("/docs/file" + i + ".txt");
            index.addTokens(Set.of("чтение"), file);
        }
        for (int i = 0; i < threadCount; i++) {
            if (i % 2 ==0)
                executor.submit(() -> {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    index.search("чтение");
                });
            else {
                final int num = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Path file = Path.of("/docs/file" + num + ".txt");
                    index.addTokens(Set.of("запись"), file);
                });
            }
        }
        startLatch.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(threadCount / 2, index.search("запись").size());
        assertEquals(threadCount / 2, index.search("чтение").size());
    }
}
