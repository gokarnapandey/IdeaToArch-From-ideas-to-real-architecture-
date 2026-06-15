package com.webSearch.agent;

import com.webSearch.session.DesignSession;

import java.time.Duration;

/**
 * Uniform contract for every pipeline agent. An agent reads upstream artifacts from the
 * {@link DesignSession} and writes its own result back into it.
 */
public interface Agent {

    AgentType type();

    /** Per-agent wall-clock budget enforced by the Orchestrator. */
    default Duration timeout() {
        return Duration.ofSeconds(150);
    }

    /** Optional agents (e.g. diagrams) may fail without derailing the run. */
    default boolean optional() {
        return false;
    }

    /** Run the agent, mutating the session in place. */
    void run(DesignSession session);
}
