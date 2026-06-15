package com.webSearch.agent;

/** The lean, build-focused pipeline steps, in execution order. */
public enum AgentType {
    REQUIREMENT,
    MODULE_DISCOVERY,
    ENTITIES,
    ARCHITECTURE,
    DATABASE,
    LLD,
    DIAGRAM,
    PROMPT_GENERATION
}
