package com.webSearch.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webSearch.agent.dto.Architecture;
import com.webSearch.agent.dto.ClaudePrompts;
import com.webSearch.agent.dto.DatabaseDesign;
import com.webSearch.agent.dto.Diagrams;
import com.webSearch.agent.dto.EntityModel;
import com.webSearch.agent.dto.Lld;
import com.webSearch.agent.dto.Modules;
import com.webSearch.agent.dto.Requirements;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The mutable shared context for one design run. Each step (agent) reads upstream artifacts and
 * writes its own. Fields are {@code volatile} and written by a single agent each, so the
 * interactive one-step-at-a-time flow is safe without locking.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesignSession {

    public enum Status { PENDING, RUNNING, COMPLETED, FAILED }

    private final String id;
    private final String idea;
    private final Instant createdAt;

    private volatile Status status = Status.PENDING;
    private volatile String error;

    /** Non-fatal per-agent failures collected during the run (lenient orchestration). */
    private final List<String> agentErrors = new CopyOnWriteArrayList<>();

    /** Latest user feedback per step (keyed by AgentType.name()), applied on the next run of that step. */
    private final Map<String, String> stepFeedback = new ConcurrentHashMap<>();

    // --- accumulated artifacts (each written by exactly one step) ---
    private volatile Requirements requirements;
    private volatile Modules modules;
    private volatile EntityModel entities;
    private volatile Architecture architecture;
    private volatile DatabaseDesign database;
    private volatile Lld lld;
    private volatile Diagrams diagrams;
    private volatile ClaudePrompts prompts;

    public DesignSession(String id, String idea) {
        this(id, idea, Instant.now());
    }

    /** Used by Jackson to rehydrate a persisted session (preserves the original {@code createdAt}). */
    @JsonCreator
    public DesignSession(@JsonProperty("id") String id,
                         @JsonProperty("idea") String idea,
                         @JsonProperty("createdAt") Instant createdAt) {
        this.id = id;
        this.idea = idea;
        this.createdAt = createdAt;
    }

    public void addAgentError(String message) {
        agentErrors.add(message);
    }
}
