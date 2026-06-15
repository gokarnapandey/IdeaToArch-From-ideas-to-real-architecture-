package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.ClaudePrompts;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PromptGenerationAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You generate an ORDERED set of build phases that, pasted into Claude one after another,
            implement this entire application incrementally and correctly.
            Choose the phase breakdown that best fits the design (layered, or per-module — your call).
            For each phase set:
            - order: 1-based sequence number
            - title: short phase title
            - goal: what this phase delivers
            - dependsOn: titles of earlier phases it builds on
            - fileName: "phase-<order>-<kebab-title>.md"
            - markdown: a SELF-CONTAINED, ready-to-paste Claude prompt that implements ONLY this phase.
            Each prompt must embed the concrete context that phase needs — package structure, the
            relevant entities and their fields, the DB schema pieces, the LLD types / services /
            controllers / exception hierarchy, the request flow, and any relevant diagram.
            Phase 1 must scaffold the project (build file, base packages, configuration). The final
            phase wires everything together and adds run instructions. Every prompt must be runnable
            on its own, assuming the earlier phases are complete. Be precise and unambiguous so Claude
            can produce production-quality code.
            """;

    public PromptGenerationAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.PROMPT_GENERATION;
    }

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(240);
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getPrompts();
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Idea:
                %s

                Modules:
                %s

                Entities (domain model):
                %s

                Architecture (HLD):
                %s

                Database design:
                %s

                LLD:
                %s

                Diagrams:
                %s
                """.formatted(session.getIdea(), json(session.getModules()), json(session.getEntities()),
                json(session.getArchitecture()), json(session.getDatabase()), json(session.getLld()),
                json(session.getDiagrams()));
        session.setPrompts(generate(session, SYSTEM, user, ClaudePrompts.class));
    }
}
