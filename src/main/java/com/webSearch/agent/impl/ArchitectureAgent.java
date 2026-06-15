package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.Architecture;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class ArchitectureAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a software architect producing the high-level design (HLD).
            Produce an HLD consistent with the chosen engineering decisions and modules:
            - style: the architectural style and a one-line name
            - layers: each with responsibility and the components it holds
            - requestFlow: the end-to-end flow of a representative request, as ordered steps
            - crossCuttingConcerns: logging, validation, error handling, config, etc.
            - rationale: WHY this architecture. Reject distributed/over-complex designs unless the
              requirements genuinely demand them.
            """;

    public ArchitectureAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.ARCHITECTURE;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getArchitecture();
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Modules:
                %s

                Entities (domain model):
                %s

                Requirements:
                %s
                """.formatted(json(session.getModules()), json(session.getEntities()),
                json(session.getRequirements()));
        session.setArchitecture(generate(session, SYSTEM, user, Architecture.class));
    }
}
