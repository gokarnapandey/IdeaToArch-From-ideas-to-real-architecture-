package com.webSearch.service;

import com.webSearch.agent.AgentType;
import com.webSearch.orchestration.Orchestrator;
import com.webSearch.session.DesignSession;
import com.webSearch.session.SessionStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Interactive, human-in-the-loop pipeline. Creating a session does NOT run any agents;
 * the client runs one step at a time via {@link #runStep}, reviewing and optionally giving
 * feedback before advancing. Steps run synchronously (one agent each).
 */
@Service
@RequiredArgsConstructor
public class DesignService {

    private static final Logger log = LoggerFactory.getLogger(DesignService.class);

    private final SessionStore store;
    private final Orchestrator orchestrator;

    /** Create an empty session — no agents run yet. */
    public DesignSession start(String idea) {
        DesignSession session = new DesignSession(UUID.randomUUID().toString(), idea);
        session.setStatus(DesignSession.Status.RUNNING);
        store.put(session);
        return session;
    }

    /**
     * Run a single step (agent), optionally incorporating user feedback (which makes the
     * agent revise its previous output). Synchronous; returns the updated session.
     */
    public DesignSession runStep(String id, AgentType type, String feedback) {
        DesignSession session = store.require(id);
        session.getStepFeedback().put(type.name(), feedback == null ? "" : feedback);
        session.getAgentErrors().removeIf(e -> e.startsWith(type.name() + ":"));

        try {
            orchestrator.runStep(session, type);
            log.info("[{}] step {} completed", id, type);
        } catch (Exception e) {
            log.warn("[{}] step {} failed: {}", id, type, e.getMessage());
            session.addAgentError(type + ": " + e.getMessage());
        }

        if (type == AgentType.PROMPT_GENERATION && session.getPrompts() != null) {
            session.setStatus(DesignSession.Status.COMPLETED);
        }
        return session;
    }
}
