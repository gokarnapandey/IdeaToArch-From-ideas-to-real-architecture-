package com.webSearch.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Minimal retry wrapper around LLM calls. Phase 1 keeps this dependency-free
 * (no Resilience4j) — bounded retries with linear backoff for transient failures.
 * Per-agent timeout is enforced separately by the Orchestrator's virtual-thread fan-out.
 */
@Component
public class LlmCallExecutor {

    private static final Logger log = LoggerFactory.getLogger(LlmCallExecutor.class);
    private static final int MAX_ATTEMPTS = 2;
    private static final long BASE_BACKOFF_MILLIS = 800L;

    public <T> T call(String name, Supplier<T> action) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return action.get();
            } catch (RuntimeException e) {
                last = e;
                log.warn("LLM call '{}' attempt {}/{} failed: {}", name, attempt, MAX_ATTEMPTS, e.getMessage());
                if (attempt < MAX_ATTEMPTS) {
                    sleep(BASE_BACKOFF_MILLIS * attempt);
                }
            }
        }
        throw last;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during LLM retry backoff", ie);
        }
    }
}
