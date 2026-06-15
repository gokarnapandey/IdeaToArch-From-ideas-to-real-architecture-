package com.webSearch.orchestration;

import com.webSearch.agent.Agent;
import com.webSearch.agent.AgentType;
import com.webSearch.session.DesignSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Executes the {@link StageGraph} level by level. Agents within a level run concurrently on
 * virtual threads with a per-agent timeout. Phase 1 is lenient: an agent failure is recorded
 * on the session and the pipeline continues, so the user always gets partial results.
 */
@Component
public class Orchestrator {

    private static final Logger log = LoggerFactory.getLogger(Orchestrator.class);

    private final Map<AgentType, Agent> agents;

    public Orchestrator(List<Agent> agentBeans) {
        this.agents = agentBeans.stream().collect(Collectors.toMap(Agent::type, Function.identity()));
    }

    public void execute(DesignSession session) {
        for (List<AgentType> level : StageGraph.LEVELS) {
            runLevel(level, session);
        }
    }

    /** Run a single step (agent) synchronously. Propagates failures to the caller. */
    public void runStep(DesignSession session, AgentType type) {
        Agent agent = agents.get(type);
        if (agent == null) {
            throw new IllegalArgumentException("Unknown step: " + type);
        }
        runAgent(agent, session);
    }

    private void runLevel(List<AgentType> level, DesignSession session) {
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = level.stream()
                    .map(agents::get)
                    .filter(Objects::nonNull)
                    .map(agent -> CompletableFuture
                            .runAsync(() -> runAgent(agent, session), exec)
                            .orTimeout(agent.timeout().toMillis(), TimeUnit.MILLISECONDS)
                            .exceptionally(ex -> {
                                String message = agent.type() + ": " + rootMessage(ex);
                                log.warn("[{}] agent {} failed: {}", session.getId(), agent.type(), rootMessage(ex));
                                session.addAgentError(message);
                                return null;
                            }))
                    .toList();
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }

    private void runAgent(Agent agent, DesignSession session) {
        log.info("[{}] running agent {}", session.getId(), agent.type());
        agent.run(session);
        log.info("[{}] agent {} done", session.getId(), agent.type());
    }

    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getClass().getSimpleName() + ": " + cur.getMessage();
    }
}
