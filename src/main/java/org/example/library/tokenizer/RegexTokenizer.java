package org.example.library.tokenizer;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegexTokenizer implements Tokenizer{
    private final String regex;

    @Override
    public Set<String> extractTokens(String text) {
        if (text.isEmpty()) return Collections.emptySet();
        return Arrays.stream(text.split(regex))
                .filter(word -> !word.isBlank())  // Фильтруем пустые строки
                .collect(Collectors.toSet());
    }
}
