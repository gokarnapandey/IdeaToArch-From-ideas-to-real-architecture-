package com.webSearch.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Live design sessions. The {@link ConcurrentHashMap} is an in-memory cache; every write is also
 * persisted to disk via {@link FileSessionRepository}, and a cache miss falls back to disk so a
 * session survives a JVM restart (e.g. a {@code spring-boot-devtools} reload) and can be reloaded.
 */
@Component
public class SessionStore {

    private final Map<String, DesignSession> sessions = new ConcurrentHashMap<>();
    private final FileSessionRepository repository;

    public SessionStore(FileSessionRepository repository) {
        this.repository = repository;
    }

    public void put(DesignSession session) {
        sessions.put(session.getId(), session);
        repository.save(session);
    }

    public DesignSession get(String id) {
        DesignSession cached = sessions.get(id);
        if (cached != null) {
            return cached;
        }
        DesignSession persisted = repository.load(id);
        if (persisted != null) {
            // Rehydrate the cache so subsequent reads (and step runs) hit memory.
            sessions.putIfAbsent(id, persisted);
            return sessions.get(id);
        }
        return null;
    }

    public DesignSession require(String id) {
        DesignSession session = get(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }
        return session;
    }
}
