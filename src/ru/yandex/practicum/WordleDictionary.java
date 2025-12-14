package ru.yandex.practicum;

import java.util.*;

/*
этот класс содержит в себе список слов List<String>
его методы похожи на методы списка, но учитывают особенности игры
также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
*/

public class WordleDictionary {

    private final List<String> words;
    private final Logger logger;
    private static final int WORD_LENGTH = 5;

    public WordleDictionary(List<String> rawLines, Logger logger) {
        this.logger = logger;

        List<String> dictionary = new ArrayList<>();

        for (String str : rawLines) {
            String word = normalizeWord(str);

            if (isValidWordByLength(word)) {
                if (!dictionary.contains(word)) {
                    dictionary.add(word);
                }
            }
        }

        this.words = dictionary;
        logger.log("Игровой словарь сформирован: " + words.size() + " слов");
    }


    public List<String> getWords() {
        return words;
    }

    /*
     Нормализует слово:
     приводит к нижнему регистру
     заменяет 'ё' → 'е'
     */
    public static String normalizeWord(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replace('ё', 'е');
    }

     //Проверяет, что слово длиной 5 букв
    private static boolean isValidWordByLength(String w) {
        return w.length() == WORD_LENGTH;
    }
}
