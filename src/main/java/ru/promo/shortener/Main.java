package ru.promo.shortener;

import ru.promo.shortener.cli.CommandLineApp;

/**
 * Точка входа в приложение
 */
public class Main {

    public static void main(String[] args) {
        CommandLineApp app = new CommandLineApp();
        app.start();
    }
}
