package com.webSearch.searchResult;


import java.util.List;

public record TavilyResponse(
        List<SearchResult> results
) {}