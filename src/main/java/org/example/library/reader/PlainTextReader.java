package org.example.library.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class PlainTextReader implements TextReader{
    private final Set<String> supportingExtensions = Set.of(".txt", ".log", ".md", ".rtf",
                                                                ".json", ".csv", ".xml", ".yaml",
                                                                ".yml", ".ini", ".cfg", ".conf",
                                                                ".properties", ".java", ".py", ".c",
                                                                ".cpp", ".html", ".css", ".js", ".sql");
    private final int lightFileLimit = 50 * 1024 * 1024;
    @Override
    public String read(Path path) throws IOException {
        long fileSize = Files.size(path);
        if (fileSize < lightFileLimit) return readLight(path);
        return readHeavy(path);
    }

    @Override
    public boolean supports(String extension) {
        return supportingExtensions.contains(extension);
    }

    private String readLight(Path path) throws IOException {
        return Files.readString(path).replaceAll("[\\x00-\\x1F\\x7F]", " ");
    }

    private String readHeavy(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        return sb.toString();
    }
}
