package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.DatabaseDesign;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a database engineer designing a normalized relational (PostgreSQL) schema.
            For each table give columns (with type, primaryKey, nullable, note), indexes and
            constraints. Define relationships with cardinality and onDelete behavior. Justify each
            index, constraint and foreign-key choice in `rationale`. Honor the engineering decisions
            (e.g. UUID vs sequence ids, soft vs hard delete). Emit an ER diagram as a Mermaid
            `erDiagram` in `erMermaid`.
            """;

    public DatabaseAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.DATABASE;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getDatabase();
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Entities (domain model):
                %s

                Modules:
                %s

                Requirements:
                %s
                """.formatted(json(session.getEntities()), json(session.getModules()),
                json(session.getRequirements()));
        session.setDatabase(generate(session, SYSTEM, user, DatabaseDesign.class));
    }
}
