package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.Lld;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LldAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a senior engineer producing a production low-level design (LLD) for a Spring Boot app.
            Produce:
            - packages: the package tree with the purpose of each
            - entities and dtos: each with package and fields (name, type, note)
            - repositories: name, entity, key methods
            - services: interface + implementation, with method signatures and behavior
            - controllers: name, basePath, endpoints (httpMethod, path, requestDto, responseDto, summary)
            - configs and utils
            - exceptions: a base exception and a hierarchy mapped to HTTP statuses
            Follow the chosen design patterns and database schema, and stay consistent with the module
            boundaries. Endpoints with their DTOs will be used to generate an OpenAPI spec, so be precise.
            """;

    public LldAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.LLD;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getLld();
    }

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(240); // largest output of the pipeline
    }

    @Override
    public void run(DesignSession session) {
        String user = """
                Modules:
                %s

                Architecture (HLD):
                %s

                Database design:
                %s

                Entities (domain model):
                %s

                Requirements:
                %s
                """.formatted(json(session.getModules()), json(session.getArchitecture()),
                json(session.getDatabase()), json(session.getEntities()),
                json(session.getRequirements()));
        session.setLld(generate(session, SYSTEM, user, Lld.class));
    }
}
