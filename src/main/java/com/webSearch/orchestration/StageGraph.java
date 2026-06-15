package com.webSearch.orchestration;

import com.webSearch.agent.AgentType;

import java.util.List;

import static com.webSearch.agent.AgentType.ARCHITECTURE;
import static com.webSearch.agent.AgentType.DATABASE;
import static com.webSearch.agent.AgentType.DIAGRAM;
import static com.webSearch.agent.AgentType.ENTITIES;
import static com.webSearch.agent.AgentType.LLD;
import static com.webSearch.agent.AgentType.MODULE_DISCOVERY;
import static com.webSearch.agent.AgentType.PROMPT_GENERATION;
import static com.webSearch.agent.AgentType.REQUIREMENT;

/**
 * The lean, build-focused flow as ordered levels. The interactive flow runs one step at a time in
 * this order; {@code Orchestrator.execute} (auto-run, unused by default) would run them in sequence.
 */
public final class StageGraph {

    public static final List<List<AgentType>> LEVELS = List.of(
            List.of(REQUIREMENT),
            List.of(MODULE_DISCOVERY),
            List.of(ENTITIES),
            List.of(ARCHITECTURE),
            List.of(DATABASE),
            List.of(LLD),
            List.of(DIAGRAM),
            List.of(PROMPT_GENERATION)
    );

    private StageGraph() {
    }
}
