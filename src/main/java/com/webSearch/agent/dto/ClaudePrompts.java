package com.webSearch.agent.dto;

import java.util.List;

/**
 * Output of the Prompt Generation agent: an ordered set of build phases. Pasted into Claude one
 * after another, they implement the whole application incrementally.
 */
public record ClaudePrompts(
        List<BuildPhase> phases
) {
    public record BuildPhase(
            int order,
            String title,
            String goal,
            List<String> dependsOn,
            String fileName, // e.g. "phase-1-project-scaffold.md"
            String markdown  // a self-contained, ready-to-paste Claude prompt for this phase
    ) {}
}
