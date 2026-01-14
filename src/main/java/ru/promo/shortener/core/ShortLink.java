package ru.promo.shortener.core;

import java.time.Instant;
import java.util.UUID;

/**
 * Доменная модель короткой ссылки
 */
public class ShortLink {

    private final String shortCode;
    private final String originalUrl;
    private final UUID ownerId;

    private final Instant createdAt;
    private final Instant expiresAt;

    private int maxClicks;
    private int currentClicks = 0;

    public ShortLink(
            String shortCode,
            String originalUrl,
            UUID ownerId,
            int maxClicks,
            long ttlSeconds
    ) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.ownerId = ownerId;
        this.maxClicks = maxClicks;

        this.createdAt = Instant.now();
        this.expiresAt = createdAt.plusSeconds(ttlSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isExpired() && currentClicks < maxClicks;
    }

    public void registerClick() {
        currentClicks++;
    }

    /** ✅ редактирование лимита */
    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public int getCurrentClicks() {
        return currentClicks;
    }

    public int getMaxClicks() {
        return maxClicks;
    }
}
