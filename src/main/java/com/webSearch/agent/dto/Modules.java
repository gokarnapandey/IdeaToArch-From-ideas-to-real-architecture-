package com.webSearch.agent.dto;

import java.util.List;

/** Output of the Module Discovery agent: modules, responsibilities and their dependencies. */
public record Modules(
        List<Module> modules,
        List<Dependency> dependencies
) {
    public record Module(
            String name,
            String responsibility,
            List<String> capabilities
    ) {}

    public record Dependency(
            String from,
            String to,
            String type,    // USES | EVENTS | DATA
            String reason
    ) {}
}
