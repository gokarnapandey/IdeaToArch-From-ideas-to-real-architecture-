package com.webSearch.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Builds the {@link OllamaApi} bean against a remote, bearer-token-protected Ollama server.
 *
 * <p>Stock Ollama has no auth, so Spring AI exposes no property for it. Spring AI's
 * autoconfiguration creates {@code OllamaApi} as {@code @ConditionalOnMissingBean}, so defining
 * our own here makes both the chat model and the (Phase 2) embedding model use it.
 *
 * <p>The base URL is non-secret (an IP:port) from {@code spring.ai.ollama.base-url}; the token is
 * read from {@code OLLAMA_BEARER_TOKEN} only (never committed; omitted ⇒ no auth header).
 *
 * <p>Timeouts are generous: a 14B model on a remote host needs time to load and generate, far more
 * than the auto-detected client's short default read timeout (which otherwise aborts every call
 * with a {@code ReadTimeoutException} and triggers an endless retry storm).
 */
@Configuration
public class OllamaConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration READ_TIMEOUT = Duration.ofMinutes(5);   // non-streaming agent calls
    private static final Duration STREAM_TIMEOUT = Duration.ofMinutes(10); // streaming report

    @Bean
    public OllamaApi ollamaApi(
            @Value("${spring.ai.ollama.base-url}") String baseUrl,
            @Value("${OLLAMA_BEARER_TOKEN:}") String apiKey) {

        // Non-streaming agent calls: JDK HTTP client with a long read timeout.
        HttpClient jdkHttpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(jdkHttpClient);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        RestClient.Builder restClientBuilder = RestClient.builder().requestFactory(requestFactory);

        // Streaming (SSE report): Reactor Netty connector with a long response timeout.
        reactor.netty.http.client.HttpClient reactorHttpClient =
                reactor.netty.http.client.HttpClient.create().responseTimeout(STREAM_TIMEOUT);
        WebClient.Builder webClientBuilder =
                WebClient.builder().clientConnector(new ReactorClientHttpConnector(reactorHttpClient));

        if (StringUtils.hasText(apiKey)) {
            String bearer = "Bearer " + apiKey;
            restClientBuilder = restClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, bearer);
            webClientBuilder = webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, bearer);
        }

        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .build();
    }
}
