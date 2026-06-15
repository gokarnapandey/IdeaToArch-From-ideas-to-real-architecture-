package com.webSearch.session;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory store of live design sessions (Phase 1). Phase 2 persists a summary to Postgres. */
@Component
public class SessionStore {

    private final Map<String, DesignSession> sessions = new ConcurrentHashMap<>();

    public void put(DesignSession session) {
        sessions.put(session.getId(), session);
    }

    public DesignSession get(String id) {
        return sessions.get(id);
    }

    public DesignSession require(String id) {
        DesignSession session = sessions.get(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }
        return session;
    }
}
