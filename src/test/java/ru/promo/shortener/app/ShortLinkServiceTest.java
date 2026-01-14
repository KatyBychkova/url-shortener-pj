package ru.promo.shortener.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.promo.shortener.core.ShortLink;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для ShortLinkService.
 * Проверяется ТОЛЬКО бизнес-логика (без CLI, без браузера).
 */
class ShortLinkServiceTest {

    /**
     * Тестируемый сервис
     */
    private ShortLinkService service;

    /**
     * UUID пользователя для тестов
     */
    private UUID userId;

    /**
     * Выполняется перед КАЖДЫМ тестом
     */
    @BeforeEach
    void setUp() {
        service = new ShortLinkService(60); // TTL = 60 секунд
        userId = UUID.randomUUID();
    }

    /**
     * Проверка: ссылка создаётся корректно
     */
    @Test
    void create_shouldCreateShortLink() {
        ShortLink link = service.create(
                "https://example.com",
                userId,
                3
        );

        assertNotNull(link);
        assertEquals("https://example.com", link.getOriginalUrl());
        assertEquals(userId, link.getOwnerId());
    }

    /**
     * Проверка: один пользователь + один URL → один shortCode
     */
    @Test
    void create_sameUserSameUrl_returnsSameShortCode() {
        ShortLink first = service.create(
                "https://example.com",
                userId,
                3
        );

        ShortLink second = service.create(
                "https://example.com",
                userId,
                3
        );

        assertEquals(
                first.getShortCode(),
                second.getShortCode()
        );
    }

    /**
     * Проверка: разные пользователи → разные shortCode
     */
    @Test
    void create_differentUsers_getDifferentShortCodes() {
        ShortLink first = service.create(
                "https://example.com",
                UUID.randomUUID(),
                3
        );

        ShortLink second = service.create(
                "https://example.com",
                UUID.randomUUID(),
                3
        );

        assertNotEquals(
                first.getShortCode(),
                second.getShortCode()
        );
    }

    /**
     * Проверка: владелец может удалить ссылку
     */
    @Test
    void delete_ownerCanDeleteLink() {
        ShortLink link = service.create(
                "https://example.com",
                userId,
                3
        );

        boolean deleted = service.delete(
                link.getShortCode(),
                userId
        );

        assertTrue(deleted);
        assertNull(service.getByCode(link.getShortCode()));
    }

    /**
     * Проверка: не владелец НЕ может удалить ссылку
     */
    @Test
    void delete_notOwnerCannotDeleteLink() {
        ShortLink link = service.create(
                "https://example.com",
                userId,
                3
        );

        boolean deleted = service.delete(
                link.getShortCode(),
                UUID.randomUUID()
        );

        assertFalse(deleted);
        assertNotNull(service.getByCode(link.getShortCode()));
    }

    /**
     * Проверка: владелец может изменить лимит
     */
    @Test
    void editLimit_ownerCanEditLimit() {
        ShortLink link = service.create(
                "https://example.com",
                userId,
                3
        );

        boolean edited = service.editLimit(
                link.getShortCode(),
                userId,
                10
        );

        assertTrue(edited);
        assertEquals(10, link.getMaxClicks());
    }

    /**
     * Проверка: не владелец НЕ может изменить лимит
     */
    @Test
    void editLimit_notOwnerCannotEditLimit() {
        ShortLink link = service.create(
                "https://example.com",
                userId,
                3
        );

        boolean edited = service.editLimit(
                link.getShortCode(),
                UUID.randomUUID(),
                10
        );

        assertFalse(edited);
        assertEquals(3, link.getMaxClicks());
    }
}
