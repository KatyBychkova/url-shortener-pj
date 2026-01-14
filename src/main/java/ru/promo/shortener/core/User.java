package ru.promo.shortener.core;

import java.util.UUID;

/**
 * Модель пользователя системы.
 * Пользователь не проходит авторизацию,
 * а идентифицируется по UUID.
 */
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    private final UUID id;

    /**
     * Конструктор пользователя
     * @param id UUID, сгенерированный при первом действии
     */
    public User(UUID id) {
        this.id = id;
    }

    /**
     * Возвращает UUID пользователя
     */
    public UUID getId() {
        return id;
    }
}
