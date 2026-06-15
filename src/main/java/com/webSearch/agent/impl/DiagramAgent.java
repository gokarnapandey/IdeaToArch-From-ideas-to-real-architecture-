package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.Diagrams;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class DiagramAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a diagramming assistant. Emit valid diagram SOURCE only — no prose, no explanation.
            For each of sequence, classDiagram, component and requestFlow, produce BOTH a Mermaid version
            and a PlantUML version. Derive:
            - sequence and requestFlow from the architecture's request flow
            - classDiagram from the LLD types and services
            - component from the modules and their dependencies
            Ensure each block is syntactically valid for its tool.
            """;

    public DiagramAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.DIAGRAM;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getDiagrams();
    }

    @Override
    public boolean optional() {
        return true; // diagrams are nice-to-have; a failure must not derail the run
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Architecture (HLD):
                %s

                LLD:
                %s

                Modules:
                %s

                Database design:
                %s
                """.formatted(json(session.getArchitecture()), json(session.getLld()),
                json(session.getModules()), json(session.getDatabase()));
        session.setDiagrams(generate(session, SYSTEM, user, Diagrams.class));
    }
}
