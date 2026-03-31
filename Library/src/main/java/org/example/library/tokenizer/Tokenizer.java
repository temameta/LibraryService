package org.example.library.tokenizer;

import java.util.Set;

public interface Tokenizer {
    Set<String> extractTokens(String text);
}
