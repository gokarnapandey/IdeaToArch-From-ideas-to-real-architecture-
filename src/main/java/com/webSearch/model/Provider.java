package com.webSearch.model;

/**
 * LLM providers behind the multi-model Strategy.
 * Phase 1 wires ANTHROPIC only; OPENROUTER / OLLAMA / GEMINI are added in Phase 3.
 */
public enum Provider {
    ANTHROPIC
    // , OPENROUTER, OLLAMA, GEMINI
}
