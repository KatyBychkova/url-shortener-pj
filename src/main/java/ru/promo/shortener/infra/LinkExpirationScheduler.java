package ru.promo.shortener.infra;

import ru.promo.shortener.core.ShortLink;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Планировщик удаления протухших ссылок
 */
public class LinkExpirationScheduler {

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    public void start(Map<String, ShortLink> storage) {
        executor.scheduleAtFixedRate(() -> {

            Iterator<Map.Entry<String, ShortLink>> iterator =
                    storage.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, ShortLink> entry = iterator.next();

                if (entry.getValue().isExpired()) {
                    System.out.println(
                            "[TTL] Link expired and removed: "
                                    + entry.getKey()
                    );
                    iterator.remove();
                }
            }

        }, 5, 5, TimeUnit.SECONDS);
    }
}

