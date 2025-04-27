package by.koronatech.office.core.service.impl;

import by.koronatech.office.core.service.VisitCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VisitCounterServiceImpl implements VisitCounterService {
    private static final Logger logger = LoggerFactory.getLogger(VisitCounterServiceImpl.class);
    private final Map<String, AtomicLong> visitCounts = new ConcurrentHashMap<>();

    @Override
    public void incrementVisit(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.error("URL cannot be null or empty");
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        visitCounts.computeIfAbsent(url, k -> new AtomicLong(0)).incrementAndGet();
        logger.info("Incremented visit count for URL [{}]: [{}]", url, getVisitCount(url));
    }

    @Override
    public long getVisitCount(String url) {
        if (url == null || url.trim().isEmpty()) {
            logger.error("URL cannot be null or empty");
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        AtomicLong counter = visitCounts.get(url);
        long count = counter != null ? counter.get() : 0;
        logger.info("Retrieved visit count [{}] for URL [{}]", count, url);
        return count;
    }
}