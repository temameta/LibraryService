package org.example.library.tokenizator;

import java.util.Set;

public interface Tokenizer {
    Set<String> extractTokens(String text);
}
