package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */

public class WordleDictionaryLoader {
    private final Logger logger;

    public WordleDictionaryLoader(Logger logger) {
        this.logger = logger;
    }
    //Читает файл словаря, нормализует слова и возвращает объект WordleDictionary
    public WordleDictionary load(String path) throws DictionaryLoadException {
        List<String> all = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // нормализуем — приводим к нижнему регистру, удаляем лишнее
                String normalized = WordleDictionary.normalizeWord(line);

                if (!normalized.isEmpty()) {
                    all.add(normalized);
                }
            }

        } catch (FileNotFoundException e) {
            throw new DictionaryLoadException("Файл словаря не найден: " + path, e);
        } catch (IOException e) {
            throw new DictionaryLoadException("Ошибка чтения словаря: " + e.getMessage(), e);
        }

        if (all.isEmpty()) {
            throw new DictionaryLoadException("Словарь пуст: " + path);
        }

        logger.log("Словарь загружен. Всего строк: " + all.size());
        return new WordleDictionary(all, logger);
    }
}