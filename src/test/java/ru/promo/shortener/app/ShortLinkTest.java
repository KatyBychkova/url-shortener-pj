package ru.promo.shortener.app;

import org.junit.jupiter.api.Test;
import ru.promo.shortener.core.ShortLink;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты доменной модели ShortLink.
 * Проверяется внутренняя логика объекта.
 */
class ShortLinkTest {

    /**
     * Проверка: лимит кликов блокирует ссылку
     */
    @Test
    void clickLimit_shouldBlockLink() {
        ShortLink link = new ShortLink(
                "abc123",
                "https://example.com",
                UUID.randomUUID(),
                2,
                60
        );

        assertTrue(link.isActive());

        link.registerClick();
        link.registerClick();

        assertFalse(link.isActive());
    }

    /**
     * Проверка: TTL истекает
     */
    @Test
    void ttl_shouldExpireLink() throws InterruptedException {
        ShortLink link = new ShortLink(
                "ttl1",
                "https://example.com",
                UUID.randomUUID(),
                10,
                1 // TTL = 1 секунда
        );

        // ждём, пока TTL истечёт
        Thread.sleep(1500);

        assertTrue(link.isExpired());
        assertFalse(link.isActive());
    }
}
