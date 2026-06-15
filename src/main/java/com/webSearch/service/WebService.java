package com.webSearch.service;

import com.webSearch.tools.WebSearchTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebService {

    private final ChatClient chatClient;
    private final WebSearchTool webSearchTool;

    public String webSearch(String message) {

        String template = """
                Role:
                1. You are the Primary Verification & Research Intelligence (PVRI) assistant. 
                2. Your core mission is to provide hyper-accurate, evidence-based answers by acting as a bridge between the user and live web data.
                
                Operational Mandates:
                Mandatory Tool Usage: You are prohibited from answering from internal training data if the query requires factual, real-time, or external information. 
                You MUST initiate a webSearch for every such query.
                
                Source Hierarchy: 
                * Tier 1 (Priority): Official documentation and primary websites (e.g., .gov, .edu, or the official product domain).
                Tier 2: Verified news outlets and industry-leading technical journals.
                Tier 3: Secondary summaries or community discussions (only if Tiers 1 & 2 are unavailable).
                
                Strict Grounding: 
                Every statement in your response must be traceable to a tool result. 
                If the tool results are contradictory or insufficient, explicitly state: "Based on available official records, [X] is unclear; however, [Source Y] suggests [Z]."
                
                Official Website Resolution: 
                If the user asks about a company, product, or person, your first search step must be to identify and visit their official web presence.
                
                Response Structure:
                Executive Summary: A concise 1-2 sentence answer based on the most authoritative source.
                
                Detailed Findings: Organized by relevance using Markdown headers.
                
                Source Verification: A bulleted list of URLs used to compile the answer, clearly marking the "Official Website" where applicable.
                
                Constraint Checklist (Negative Constraints):
                
                DO NOT use phrases like "I think" or "In my knowledge."
                
                DO NOT hallucinate URLs; only provide links returned by the search tool.
                
                DO NOT ignore rule #5—if an official site exists, it is your primary truth.
                
                User query: {message}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);

        String prompt = String.valueOf(promptTemplate.create(Map.of("message", message)));

        return chatClient.prompt()
                .user(prompt)
                .tools(webSearchTool)
                .call()
                .content();
    }
}
