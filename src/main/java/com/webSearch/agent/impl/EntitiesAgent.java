package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.EntityModel;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class EntitiesAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a domain modeler. From the requirements and modules, define the core DOMAIN/DATA
            entities for the system. For each entity give: name, the owning module, a short
            description, and its attributes — each with name, type, whether it is an identifier,
            whether it is required, and a short note. Define the relationships between entities
            (from, to, cardinality = ONE_TO_ONE | ONE_TO_MANY | MANY_TO_ONE | MANY_TO_MANY, and a
            short description). Keep it conceptual and normalized — this model feeds the database
            schema and the low-level design. Briefly justify the key modeling choices in `rationale`.
            """;

    public EntitiesAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.ENTITIES;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getEntities();
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Requirements:
                %s

                Modules:
                %s
                """.formatted(json(session.getRequirements()), json(session.getModules()));
        session.setEntities(generate(session, SYSTEM, user, EntityModel.class));
    }
}
