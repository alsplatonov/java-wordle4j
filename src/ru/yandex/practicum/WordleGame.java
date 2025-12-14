package ru.yandex.practicum;

import java.util.*;

/*
Основная логика игры Wordle.
Хранит загаданное слово, историю попыток и вычисляет подсказки.
Подсказка формируется с помощью символов:
+  — буква на своём месте
^  — буква есть в слове, но на другой позиции
-  — буквы нет в слове
*/
public class WordleGame {

    private final String answer;
    private int remainingSteps;
    private final WordleDictionary dictionary;
    private final Logger logger;
    private static final int WORD_LENGTH = 5;
    private static final int MAX_STEPS = 6;

    private final List<String> guesses = new ArrayList<>(); // введённые игроком слова
    private final List<String> clues = new ArrayList<>();   // подсказки по каждому ходу

    public WordleGame(WordleDictionary dictionary, Logger logger) {
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary == null");
        }

        this.dictionary = dictionary;
        this.logger = logger;
        this.remainingSteps = MAX_STEPS;

        List<String> words = dictionary.getWords();
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Игровой словарь пуст");
        }

        Random random = new Random();
        this.answer = words.get(random.nextInt(words.size())); // случайный выбор слова
        logger.log("Загаданное слово выбрано.");
    }

    public String getAnswer() {
        return answer;
    }

    public int getRemainingSteps() {
        return remainingSteps;
    }

    public boolean isFinished() {
        return remainingSteps <= 0 || isWon();
    }

    public boolean isWon() {
        if (guesses.isEmpty()) return false;
        // берём последнее введённое слово и сравниваем с загаданным
        return guesses.get(guesses.size() - 1).equals(answer);
    }

    /*
     Обрабатывает попытку: проверяет слово, вычисляет подсказку "+^-",
     сохраняет историю и уменьшает число оставшихся шагов.
     */
    public String makeGuess(String guess) {

        if (guess == null || guess.length() != WORD_LENGTH) {
            throw new InvalidWordException("Слово должно состоять из " + WORD_LENGTH + " букв.");
        }

        if (!dictionary.getWords().contains(guess)) {
            throw new WordNotFoundInDictionaryException("Слово отсутствует в словаре.");
        }

        String clue = computeClue(guess, answer);

        guesses.add(guess);
        clues.add(clue);
        remainingSteps--;

        logger.log("Ход: " + guess + " -> " + clue + ". Осталось: " + remainingSteps);
        return clue;
    }

    /*
     Возвращает слово-подсказку, учитывая всю историю ходов.
     Компьютер выбирает первое подходящее слово из словаря, которое не было использовано
     и соответствует всем предыдущим подсказкам.
     */
    public String suggest() {
        List<String> words = dictionary.getWords();

        for (String candidate : words) {
            if (!guesses.contains(candidate) && isConsistentWithHistory(candidate)) {
                logger.log("Подсказка: " + candidate);
                return candidate;
            }
        }

        logger.log("Подсказка не найдена");
        return null;
    }

    /*
     Проверяет, что кандидат соответствует всем предыдущим ходам.
     Используется для автоподсказки.
     */
    private boolean isConsistentWithHistory(String candidate) {
        for (int i = 0; i < guesses.size(); i++) {
            String prevGuess = guesses.get(i);
            String expectedClue = clues.get(i);
            String actualClue = computeClue(prevGuess, candidate);

            if (!actualClue.equals(expectedClue)) return false;
        }
        return true;
    }

    /*
     Вычисляет подсказку для хода.
     +  — правильная буква на правильной позиции
     ^  — правильная буква на неправильной позиции
     -  — буквы нет в слове
     */
    public static String computeClue(String guess, String answer) {
        // Инициализируем массив строк длиной 5 символов, по умолчанию "-"
        String[] res = new String[WORD_LENGTH];
        Arrays.fill(res, "-");

        String[] g = guess.split("");   // массив букв из угадываемого слова
        String[] a = answer.split("");  // массив букв из ответа

        boolean[] usedA = new boolean[WORD_LENGTH]; // отметка, какие буквы уже использованы в ответе

        // 1. Отмечаем буквы на правильных местах "+"
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (g[i].equals(a[i])) {
                res[i] = "+";
                usedA[i] = true;
            }
        }

        // 2. Считаем оставшиеся буквы в ответе
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (!usedA[i]) {
                String c = a[i];
                counts.put(c, counts.getOrDefault(c, 0) + 1);
            }
        }

        // 3. Отмечаем буквы, которые есть, но на других позициях "^"
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (res[i].equals("+")) continue;

            String c = g[i];
            Integer cnt = counts.get(c);

            if (cnt != null && cnt > 0) {
                res[i] = "^";
                counts.put(c, cnt - 1);
            } else {
                res[i] = "-";
            }
        }

        // 4. Объединяем массив в строку и возвращаем
        return String.join("", res);
    }

}
