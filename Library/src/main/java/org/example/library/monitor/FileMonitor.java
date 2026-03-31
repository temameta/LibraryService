package org.example.library.monitor;

import java.nio.file.Path;

public interface FileMonitor {
    void registerDirectory(Path directory);
    void start();
    void stop();
}
