package com.webSearch.agent;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.webSearch.model.ChatClientFactory;
import com.webSearch.resilience.LlmCallExecutor;
import com.webSearch.session.DesignSession;
import org.springframework.ai.chat.client.ChatClient;

/**
 * Shared plumbing for LLM-backed agents: obtains a {@link ChatClient} from the factory,
 * calls it for a typed structured-output response, and serializes upstream artifacts to
 * JSON for inclusion in prompts. Wrapped by {@link LlmCallExecutor} for retries.
 */
public abstract class AbstractLlmAgent implements Agent {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    protected final ChatClientFactory chatClientFactory;
    protected final LlmCallExecutor executor;

    protected AbstractLlmAgent(ChatClientFactory chatClientFactory, LlmCallExecutor executor) {
        this.chatClientFactory = chatClientFactory;
        this.executor = executor;
    }

    /**
     * The agent's existing artifact in the session (its prior output). Lets the model revise
     * rather than regenerate when the user supplies feedback. Returns null when none yet.
     */
    protected abstract Object currentArtifact(DesignSession session);

    /** Call the LLM for a structured (typed) response, applying any per-step user feedback. */
    protected <T> T generate(DesignSession session, String system, String user, Class<T> responseType,
                             Object... tools) {
        String effectiveUser = applyFeedback(session, user);
        return executor.call(type().name(), () -> {
            ChatClient.ChatClientRequestSpec spec = chatClientFactory.defaultClient()
                    .prompt()
                    .system(system)
                    .user(effectiveUser);
            if (tools != null && tools.length > 0) {
                spec = spec.tools(tools);
            }
            return spec.call().entity(responseType);
        });
    }

    /** Append a revision block to the prompt when the user has given feedback for this step. */
    private String applyFeedback(DesignSession session, String user) {
        String feedback = session.getStepFeedback().get(type().name());
        if (feedback == null || feedback.isBlank()) {
            return user;
        }
        return user + """

                ---
                You previously produced this output for this step:
                %s

                The user wants you to REVISE it based on the following feedback. Incorporate the
                feedback, keep whatever is still correct, and return the full updated result:
                %s
                """.formatted(json(currentArtifact(session)), feedback);
    }

    /** Pretty-print an upstream artifact for embedding in a prompt; null-safe. */
    protected String json(Object value) {
        if (value == null) {
            return "(not available)";
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
