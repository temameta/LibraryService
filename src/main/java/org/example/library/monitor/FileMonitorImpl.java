package org.example.library.monitor;

import lombok.RequiredArgsConstructor;
import org.example.library.processor.FileProcessor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileMonitorImpl implements FileMonitor {
    private final WatchService watchService;
    private final FileProcessor fileProcessor;
    private final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();

    public FileMonitorImpl(FileProcessor fileProcessor) throws IOException {
        this.fileProcessor = fileProcessor;
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    private Thread watchThread;
    private volatile boolean running = false;

    public void registerDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerSingleDirectory(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Ошибка при регистрации каталога: " + e.getMessage());
        }
    }

    private void registerSingleDirectory(Path directory) throws IOException {
        WatchKey key = directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, directory);
    }

    public void start() {
        if (running) return;
        running = true;

        watchThread = new Thread(this::processEvents, "FileMonitorThread");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    public void stop() {
        running = false;
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processEvents() {
        while (running) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException | ClosedWatchServiceException e) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey не найден!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) continue;

                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path child = dir.resolve(fileName);

                try {
                    if (kind == ENTRY_CREATE) {
                        if (Files.isDirectory(child)) {
                            registerDirectory(child);
                        } else if (Files.isRegularFile(child)) {
                            fileProcessor.indexFile(child);
                        }
                    } else if (kind == ENTRY_MODIFY) {
                        if (Files.isRegularFile(child)) {
                            fileProcessor.indexFile(child);
                        }
                    } else if (kind == ENTRY_DELETE) {
                        fileProcessor.deleteIndex(child);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка при обработке файла " + child + ": " + e.getMessage());
                }
            }

            boolean valid = key.reset();
            if (!valid) keys.remove(key);
        }
    }
}