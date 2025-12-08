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

    public static void main(String[] args) {
        String dictionaryFile = "words_ru.txt";
        String logFile = "wordle.log";

        try (PrintWriter logWriter = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8))) {

            Logger logger = new Logger(logWriter);

            try {
                // Загружаем словарь
                WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
                WordleDictionary dictionary = loader.load(dictionaryFile);

                // Создаём игру
                WordleGame game = new WordleGame(dictionary, logger);

                try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
                    System.out.println("Игра Wordle. Попробуйте угадать слово из 5 букв. У вас 6 попыток.");

                    while (!game.isFinished()) {
                        System.out.printf(
                                "Осталось попыток: %d. Введите слово (или Enter для подсказки):%n",
                                game.getRemainingSteps()
                        );

                        String line = scanner.nextLine();
                        String input = (line == null) ? "" : line.trim();

                        if (input.isEmpty()) {
                            // Игрок нажал Enter → компьютер делает ход
                            String hint = game.suggest();
                            if (hint == null) {
                                System.out.println("Подходящих подсказок не найдено.");
                                break;
                            }
                            System.out.println("Подсказка (ход компьютера): " + hint);
                            try {
                                String clue = game.makeGuess(hint);
                                System.out.println(hint);
                                System.out.println(clue);
                            } catch (InvalidWordException | WordNotFoundInDictionaryException e) {
                                System.out.println(e.getMessage());
                                logger.log("Error: " + e.getMessage());
                            }
                        } else {
                            // Игрок вводит слово сам
                            try {
                                String normalized = WordleDictionary.normalizeWord(input);
                                String clue = game.makeGuess(normalized);
                                System.out.println(input);
                                System.out.println(clue);
                            } catch (InvalidWordException | WordNotFoundInDictionaryException e) {
                                System.out.println(e.getMessage());
                                logger.log("User error: " + e.getMessage());
                            }
                        }
                    }

                    // Финальный результат
                    if (game.isWon()) {
                        System.out.println("Поздравляем! Вы угадали слово: " + game.getAnswer());
                    } else {
                        System.out.println("К сожалению, попытки закончились. Загаданное слово: " + game.getAnswer());
                    }
                }

            } catch (DictionaryLoadException e) {
                logger.log("Ошибка загрузки словаря: " + e.getMessage());
                e.printStackTrace(logWriter);
                System.out.println("Ошибка при загрузке словаря. Смотрите лог.");
            } catch (Throwable t) {
                logger.log("Непредвиденная ошибка: " + t.getMessage());
                t.printStackTrace(logWriter);
                System.out.println("Произошла ошибка. Смотрите лог.");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Не удалось создать файл лога: " + e.getMessage());
        }
    }
}
