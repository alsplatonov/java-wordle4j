package ru.yandex.practicum;

import java.io.PrintWriter;

/*
 * Простой логгер, который пишет сообщения в файл.
 * Используется для отладки игровой логики.
 */

public class Logger {
    private final PrintWriter writer;

    public Logger(PrintWriter writer) {
        this.writer = writer;
    }
    // Записывает сообщение в лог
    public void log(String message) {
        writer.println(message);
        writer.flush();
    }
}
