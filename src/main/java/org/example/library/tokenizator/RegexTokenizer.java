package org.example.library.tokenizator;

import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class RegexTokenizer implements Tokenizer{
    private final String regex;

    @Override
    public Set<String> extractTokens(String text) {
        return Set.of(text.split(regex));
    }
}
