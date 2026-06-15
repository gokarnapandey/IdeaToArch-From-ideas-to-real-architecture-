package com.webSearch.searchResult;


public record SearchResult(
        String title,
        String url,
        String content,
        double score
) {}

