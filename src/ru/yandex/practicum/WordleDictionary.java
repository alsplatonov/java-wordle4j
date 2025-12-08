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

    public WordleDictionary(List<String> rawLines, Logger logger) {
        this.logger = logger;

        List<String> tmp = new ArrayList<>();

        for (String s : rawLines) {
            String w = normalizeWord(s);

            if (isValidWord5(w)) {
                if (!tmp.contains(w)) {
                    tmp.add(w);
                }
            }
        }

        this.words = tmp;
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
    private static boolean isValidWord5(String w) {
        if (w.length() != 5) return false;
        return true;
    }
}
