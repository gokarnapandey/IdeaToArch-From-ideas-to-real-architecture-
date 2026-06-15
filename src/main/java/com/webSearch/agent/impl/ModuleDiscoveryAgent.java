package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.Modules;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class ModuleDiscoveryAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a software architect decomposing a system into modules (bounded contexts).
            Aim for high cohesion and low coupling. For each module give a single clear responsibility
            and its capabilities. For each dependency state from, to, type (USES | EVENTS | DATA) and a
            justification. Explicitly avoid and flag any circular dependency.
            """;

    public ModuleDiscoveryAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.MODULE_DISCOVERY;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getModules();
    }

    @Override
    public void run(DesignSession session) {
        String user = "Requirements:\n" + json(session.getRequirements());
        session.setModules(generate(session, SYSTEM, user, Modules.class));
    }
}
