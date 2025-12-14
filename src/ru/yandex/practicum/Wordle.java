package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
Главный класс игры Wordle.
Поддерживает два режима:
1) Пользователь вводит слова сам.
2) Если пользователь нажимает Enter, компьютер делает ход сам, используя подсказки.
*/
public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try {
            runGame();
        } catch (Exception e) {
            System.out.println("Произошла непредвиденная ошибка. Смотрите лог.");
        }
    }

    private static void runGame() {
        try (PrintWriter logWriter = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILE), StandardCharsets.UTF_8))) {

            Logger logger = new Logger(logWriter);
            // Загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
            // Создаём игру
            WordleGame game = new WordleGame(dictionary, logger);

            try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
                play(game, scanner, logger);
            }

        } catch (DictionaryLoadException e) {
            logAndShowError(e, "Ошибка загрузки словаря");
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось создать файл лога: " + e.getMessage());
        } catch (Throwable t) {
            logAndShowError(t, "Непредвиденная ошибка");
        }
    }

    private static void play(WordleGame game, Scanner scanner, Logger logger) {
        System.out.println("Игра Wordle. Попробуйте угадать слово из 5 букв. У вас 6 попыток.");

        while (!game.isFinished()) {
            System.out.printf(
                    "Осталось попыток: %d. Введите слово (или Enter для подсказки):%n",
                    game.getRemainingSteps()
            );

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                // Игрок нажал Enter → компьютер делает ход
                processComputerMove(game, logger);
            } else {
                // Игрок вводит слово сам
                processUserMove(game, input, logger);
            }
        }
        // Финальный результат
        printResult(game);
    }

    private static void processUserMove(WordleGame game, String input, Logger logger) {
        try {
            String normalized = WordleDictionary.normalizeWord(input);
            String clue = game.makeGuess(normalized);

            System.out.println(input);
            System.out.println(clue);

        } catch (InvalidWordException | WordNotFoundInDictionaryException e) {
            System.out.println(e.getMessage());
            logger.logError("Ошибка хода пользователя", e);
        }
    }

    private static void processComputerMove(WordleGame game, Logger logger) {
        String hint = game.suggest();

        if (hint == null) {
            System.out.println("Подходящих подсказок не найдено.");
            return;
        }

        System.out.println("Подсказка (ход компьютера): " + hint);

        try {
            String clue = game.makeGuess(hint);

            System.out.println(hint);
            System.out.println(clue);

        } catch (InvalidWordException | WordNotFoundInDictionaryException e) {
            System.out.println(e.getMessage());
            logger.logError("Ошибка хода компьютера", e);
        }
    }

    private static void printResult(WordleGame game) {
        if (game.isWon()) {
            System.out.println("Поздравляем! Вы угадали слово: " + game.getAnswer());
        } else {
            System.out.println("К сожалению, попытки закончились. Загаданное слово: " + game.getAnswer());
        }
    }

    // Вспомогательный метод для логирования и консольного вывода ошибок на верхнем уровне
    private static void logAndShowError(Throwable t, String message) {
        try (PrintWriter logWriter = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILE, true), StandardCharsets.UTF_8))) {
            Logger logger = new Logger(logWriter);
            logger.logError(message, t);
        } catch (IOException e) {
            System.out.println("Не удалось записать лог ошибки: " + e.getMessage());
        }
        System.out.println(message + ". Смотрите лог.");
    }
}
