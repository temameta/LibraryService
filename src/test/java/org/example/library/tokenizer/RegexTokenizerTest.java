package org.example.library.tokenizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RegexTokenizerTest {
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

        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("Дубликаты слов не повторяются в результате")
    void shouldIgnoreDuplicates() {
        Set<String> tokens = tokenizer.extractTokens("кот кот кот");

        assertEquals(1, tokens.size());
        assertTrue(tokens.contains("кот"));
    }

    @Test
    @DisplayName("Строка, состоящая только из пробелов возвращает пустой набор токенов")
    void shouldReturnEmptySetForStringWithSpacesOnly() {
        Set<String> tokens = tokenizer.extractTokens(" ");

        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("Слова разного регистра считаются одним токеном с нижним регистром")
    void shouldIgnoreCase() {
        Set<String> tokens = tokenizer.extractTokens("Кот КОТ кот");

        assertEquals(1, tokens.size());
        assertTrue(tokens.contains("кот"));
    }

    @Test
    @DisplayName("Лишние пробелы не попадают в токены")
    void shouldIgnoreSpaces() {
        Set<String> tokens = tokenizer.extractTokens("Кот        кошка");

        assertFalse(tokens.contains(" "));
    }

    @Test
    @DisplayName("Пробелы в начале и в конце слов не попадают в токен, если регулярка - не пробел")
    void shouldIgnoreSpacesInStartAndEndOfWords() {
        Tokenizer dotTokenizer = new RegexTokenizer(".");
        Set<String> tokens = dotTokenizer.extractTokens(" Кот. кошка");
        boolean isContains = false;

        for (String token : tokens) {
            if (token.contains(" ")) {
                isContains = true;
                break;
            }
        }

        assertFalse(isContains);
    }

    @Test
    @DisplayName("null значение должно вернуть пустой набор токенов")
    void shouldReturnEmptySetIfNullProvided() {
        Set<String> tokens = tokenizer.extractTokens(null);

        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("null значение не должно вызывать исключений")
    void shouldNotThrowExceptionsIfNullProvided() {
        assertDoesNotThrow(() -> {tokenizer.extractTokens(null);});
    }
}
