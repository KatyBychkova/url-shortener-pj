package ru.promo.shortener.cli;

import ru.promo.shortener.app.ShortLinkService;
import ru.promo.shortener.core.ShortLink;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

/**
 * CLI-интерфейс приложения
 */
public class CommandLineApp {

    private final Scanner scanner = new Scanner(System.in);
    private final ShortLinkService service;
    private final int defaultMaxClicks;

    private UUID currentUserId;

    public CommandLineApp() {

        long ttlSeconds = 60;
        int maxClicks = 3;

        try {
            Properties props = new Properties();
            props.load(
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("application.properties")
            );

            ttlSeconds = Long.parseLong(
                    props.getProperty("app.default-ttl-seconds")
            );

            maxClicks = Integer.parseInt(
                    props.getProperty("app.default-max-clicks")
            );

        } catch (Exception e) {
            System.out.println("Config not loaded, using defaults");
        }

        this.defaultMaxClicks = maxClicks;
        this.service = new ShortLinkService(ttlSeconds);
    }

    public void start() {
        System.out.println("=== URL Shortener ===");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) break;
            if (input.equalsIgnoreCase("help")) { printHelp(); continue; }

            if (input.startsWith("create ")) { handleCreate(input); continue; }
            if (input.startsWith("open ")) { handleOpen(input); continue; }
            if (input.equals("list")) { handleList(); continue; }
            if (input.startsWith("delete ")) { handleDelete(input); continue; }
            if (input.startsWith("edit ")) { handleEdit(input); continue; }

            System.out.println("Unknown command. Type 'help'.");
        }
    }

    private void ensureUser() {
        if (currentUserId == null) {
            currentUserId = UUID.randomUUID();
            System.out.println("Your UUID: " + currentUserId);
        }
    }

    private void handleCreate(String input) {
        ensureUser();
        String url = input.substring("create ".length()).trim();

        ShortLink link = service.create(url, currentUserId, defaultMaxClicks);

        System.out.println("Short link created:");
        System.out.println("clck.ru/" + link.getShortCode());
    }

    private void handleOpen(String input) {
        String code = input.substring("open ".length()).trim();
        ShortLink link = service.getByCode(code);

        if (link == null) {
            System.out.println("Link not found.");
            return;
        }

        if (!link.isActive()) {
            System.out.println(link.isExpired()
                    ? "Link expired."
                    : "Click limit reached.");
            return;
        }

        try {
            link.registerClick();
            Desktop.getDesktop().browse(new URI(link.getOriginalUrl()));
            System.out.printf("Clicks: %d / %d%n",
                    link.getCurrentClicks(), link.getMaxClicks());
        } catch (Exception e) {
            System.out.println("Failed to open browser.");
        }
    }

    private void handleList() {
        ensureUser();
        List<ShortLink> links = service.getByOwner(currentUserId);

        if (links.isEmpty()) {
            System.out.println("No links.");
            return;
        }

        for (ShortLink link : links) {
            System.out.printf(
                    "clck.ru/%s | %d/%d%n",
                    link.getShortCode(),
                    link.getCurrentClicks(),
                    link.getMaxClicks()
            );
        }
    }

    private void handleDelete(String input) {
        ensureUser();
        String code = input.substring("delete ".length()).trim();

        System.out.println(
                service.delete(code, currentUserId)
                        ? "Link deleted."
                        : "Access denied or link not found."
        );
    }

    private void handleEdit(String input) {
        ensureUser();
        String[] parts = input.split("\\s+");

        if (parts.length != 3) {
            System.out.println("Usage: edit <code> <newLimit>");
            return;
        }

        int newLimit = Integer.parseInt(parts[2]);

        System.out.println(
                service.editLimit(parts[1], currentUserId, newLimit)
                        ? "Limit updated."
                        : "Access denied or link not found."
        );
    }

    private void printHelp() {
        System.out.println("""
                create <url>
                open <code>
                list
                delete <code>
                edit <code> <newLimit>
                exit
                """);
    }
}
