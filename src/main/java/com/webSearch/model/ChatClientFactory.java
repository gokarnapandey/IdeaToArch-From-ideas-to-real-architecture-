package com.webSearch.model;

import org.springframework.ai.chat.client.ChatClient;

/**
 * Strategy entry point for obtaining a {@link ChatClient}. Phase 1 returns a single
 * Anthropic-backed client; Phase 3 adds provider/tier routing (e.g. Opus for
 * reasoning-heavy agents, Sonnet/Haiku for lightweight ones).
 */
public interface ChatClientFactory {

    /** The default client (Anthropic Claude in Phase 1). */
    ChatClient defaultClient();

    /** Client for a specific provider. Phase 1 delegates everything to {@link #defaultClient()}. */
    ChatClient forProvider(Provider provider);
}
