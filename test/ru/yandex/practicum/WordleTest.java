package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WordleTest {

    // -------------------- WordleDictionary --------------------
    @Test
    void testDictionaryLoadsUniqueNormalizedWords() {
        // Проверяет, что словарь нормализует слова, убирает дубли, приводит к нижнему регистру, меняет ё -> е, берет
        //только слова из 5 букв
        List<String> raw = List.of("Слово", "слово", "шофёр", "ШОФер", "миР");
        WordleDictionary dict = new WordleDictionary(raw, new Logger(new PrintWriter(Writer.nullWriter())));

        assertEquals(2, dict.getWords().size());
        assertTrue(dict.getWords().contains("шофер"));
        assertTrue(dict.getWords().contains("слово"));
    }

    // -------------------- WordleGame / computeClue --------------------
    @Test
    void testComputeClueExactMatch() {
        // Проверяет, что полностью совпадающее слово даёт "+++++"
        String clue = WordleGame.computeClue("слово", "слово");
        assertEquals("+++++", clue);
    }

    @Test
    void testMakeGuessReducesRemainingSteps() throws Exception {
        // Проверяет, что ход уменьшает количество оставшихся попыток
        WordleDictionary dict = new WordleDictionary(List.of("слово"),
                new Logger(new PrintWriter(Writer.nullWriter())));
        WordleGame game = new WordleGame(dict, new Logger(new PrintWriter(Writer.nullWriter())));

        int before = game.getRemainingSteps();
        game.makeGuess("слово");
        int after = game.getRemainingSteps();
        assertEquals(before - 1, after);
    }

    @Test
    void testIsWonReturnsTrueWhenCorrect() throws Exception {
        // Проверяет, что isWon() возвращает true при верном угадывании
        WordleDictionary dict = new WordleDictionary(List.of("слово"),
                new Logger(new PrintWriter(Writer.nullWriter())));
        WordleGame game = new WordleGame(dict, new Logger(new PrintWriter(Writer.nullWriter())));

        game.makeGuess("слово");
        assertTrue(game.isWon());
    }

    // -------------------- WordleDictionaryLoader --------------------
    @Test
    void testLoaderLoadsDictionary() throws Exception {
        // Проверяет, что словарь загружается из списка строк
        List<String> lines = List.of("слово", "шофер");
        WordleDictionary dict = new WordleDictionary(lines, new Logger(new PrintWriter(Writer.nullWriter())));

        assertEquals(2, dict.getWords().size());
        assertTrue(dict.getWords().contains("слово"));
        assertTrue(dict.getWords().contains("шофер"));
    }

}

