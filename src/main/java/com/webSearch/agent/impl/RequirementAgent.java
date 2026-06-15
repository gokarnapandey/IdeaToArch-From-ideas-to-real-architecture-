package com.webSearch.agent.impl;

import com.webSearch.agent.AbstractLlmAgent;
import com.webSearch.agent.AgentType;
import com.webSearch.agent.dto.Requirements;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

@Component
public class RequirementAgent extends AbstractLlmAgent {

    private static final String SYSTEM = """
            You are a senior, product-minded software engineer doing requirement discovery.
            Turn a raw project idea into engineering-ready requirements.
            Rules:
            - Separate functional from non-functional (performance, scale, security, availability...).
            - Anything you infer that the user did not state goes in `implicit` (never silently invent scope).
            - Capture `assumptions` you are making and `openQuestions` you would ask the stakeholder.
            - List what is explicitly `outOfScope`.
            Be specific and concrete; avoid vague filler.
            """;

    public RequirementAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        super(chatClientFactory, executor);
    }

    @Override
    public AgentType type() {
        return AgentType.REQUIREMENT;
    }

    @Override
    protected Object currentArtifact(DesignSession session) {
        return session.getRequirements();
    }

    @Override
    public void run(DesignSession session) {
        String user = "Project idea:\n" + session.getIdea();
        session.setRequirements(generate(session, SYSTEM, user, Requirements.class));
    }
}
