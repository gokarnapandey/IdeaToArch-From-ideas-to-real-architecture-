package com.webSearch.agent.dto;

import java.util.List;

/** Output of the Architecture agent: the high-level design (HLD). */
public record Architecture(
        String style,
        String rationale,
        List<Layer> layers,
        List<String> requestFlow,
        List<String> crossCuttingConcerns
) {
    public record Layer(
            String name,
            String responsibility,
            List<String> components
    ) {}
}
