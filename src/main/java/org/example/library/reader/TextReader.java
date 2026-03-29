package org.example.library.reader;

import java.io.IOException;

public interface TextReader {
    String read(String path) throws IOException;
    boolean supports(String path);
}
