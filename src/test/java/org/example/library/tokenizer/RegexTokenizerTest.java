package org.example.library.tokenizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegexTokenizerTest {
    // Создаем токенизатор, который разбивает по НЕ-буквенным символам
    private final Tokenizer tokenizer = new RegexTokenizer(" ");

    @Test
    @DisplayName("Обычная строка разбивается на отдельные слова")
    void shouldTokenizeSimpleString() {
        Set<String> tokens = tokenizer.extractTokens("кошка сидит на окне");

        assertEquals(4, tokens.size());
        assertTrue(tokens.containsAll(Set.of("кошка", "сидит", "на", "окне")));
    }

    @Test
    @DisplayName("Пустая строка возвращает пустой набор токенов")
    void shouldReturnEmptySetForEmptyString() {
        Set<String> tokens = tokenizer.extractTokens("");
        tokens.forEach(System.out::println);
        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("Дубликаты слов не повторяются в результате")
    void shouldIgnoreDuplicates() {
        Set<String> tokens = tokenizer.extractTokens("кот кот кот");

        assertEquals(1, tokens.size());
        assertTrue(tokens.contains("кот"));
    }
}
