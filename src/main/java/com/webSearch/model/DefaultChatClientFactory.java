package com.webSearch.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Phase 1 implementation: wraps the single autoconfigured Claude {@link ChatClient}.
 * Phase 3 will hold a {@code Map<Provider, ChatClient>} and route by provider/tier.
 */
@Component
public class DefaultChatClientFactory implements ChatClientFactory {

    private final ChatClient chatClient;

    public DefaultChatClientFactory(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public ChatClient defaultClient() {
        return chatClient;
    }

    @Override
    public ChatClient forProvider(Provider provider) {
        return chatClient;
    }
}
