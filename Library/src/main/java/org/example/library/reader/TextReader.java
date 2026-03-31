package org.example.library.reader;

import java.io.IOException;
import java.nio.file.Path;

public interface TextReader {
    String read(Path path) throws IOException;
    boolean supports(String extension);
}
