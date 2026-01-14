package ru.promo.shortener.app;

import ru.promo.shortener.core.ShortLink;
import ru.promo.shortener.infra.LinkExpirationScheduler;

import java.util.*;

/**
 * Сервис бизнес-логики коротких ссылок
 */
public class ShortLinkService {

    private final Map<String, ShortLink> byCode = new HashMap<>();
    private final Map<String, String> userUrlIndex = new HashMap<>();

    private final long ttlSeconds;

    public ShortLinkService(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        new LinkExpirationScheduler().start(byCode);
    }

    public ShortLink create(String originalUrl, UUID userId, int maxClicks) {

        String userKey = userId + "::" + originalUrl;

        if (userUrlIndex.containsKey(userKey)) {
            return byCode.get(userUrlIndex.get(userKey));
        }

        String shortCode = generateShortCode(userId, originalUrl);

        ShortLink link = new ShortLink(
                shortCode,
                originalUrl,
                userId,
                maxClicks,
                ttlSeconds
        );

        byCode.put(shortCode, link);
        userUrlIndex.put(userKey, shortCode);

        return link;
    }

    public ShortLink getByCode(String code) {
        return byCode.get(code);
    }

    /** ✅ список ссылок владельца */
    public List<ShortLink> getByOwner(UUID ownerId) {
        List<ShortLink> result = new ArrayList<>();

        for (ShortLink link : byCode.values()) {
            if (link.getOwnerId().equals(ownerId)) {
                result.add(link);
            }
        }
        return result;
    }

    /** ✅ удаление с проверкой прав */
    public boolean delete(String code, UUID userId) {
        ShortLink link = byCode.get(code);

        if (link == null || !link.getOwnerId().equals(userId)) {
            return false;
        }

        byCode.remove(code);
        userUrlIndex.values().remove(code);
        return true;
    }

    /** ✅ редактирование лимита */
    public boolean editLimit(String code, UUID userId, int newLimit) {
        ShortLink link = byCode.get(code);

        if (link == null || !link.getOwnerId().equals(userId)) {
            return false;
        }

        link.setMaxClicks(newLimit);
        return true;
    }

    private String generateShortCode(UUID userId, String url) {
        String base = userId + url + System.nanoTime();
        return Integer.toHexString(base.hashCode()).substring(0, 6);
    }
}
