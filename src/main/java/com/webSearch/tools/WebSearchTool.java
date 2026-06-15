package com.webSearch.tools;

import com.webSearch.searchResult.SearchResult;
import com.webSearch.searchResult.TavilyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class WebSearchTool {

    private static final Logger log = LoggerFactory.getLogger(WebSearchTool.class);

    private final RestClient restClient;

    @Value("${tavily.api.key}")
    private String apiKey;

    public WebSearchTool(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.tavily.com").build();
    }

    @Tool(description = "Search the web for real-time information. Returns a list of "
            + "results, each with a title, url, content snippet, and relevance score.")
    public List<SearchResult> webSearch(String query) {
        try {
            TavilyResponse response = restClient.post()
                    .uri("/search")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Map.of(
                            "query", query,
                            "search_depth", "basic",
                            "max_results", 5
                    ))
                    .retrieve()
                    .body(TavilyResponse.class);

            return response != null ? response.results() : List.of();
        } catch (Exception e) {
            // Degrade gracefully: a search failure must not fail the whole agent run.
            // The model continues with whatever context it already has.
            log.warn("Web search failed for query '{}': {}", query, e.getMessage());
            return List.of();
        }
    }
}

