package com.webSearch.agent.dto;

import java.util.List;

/** Output of the Entities agent: the domain/data entity model that feeds the DB schema and LLD. */
public record EntityModel(
        List<Entity> entities,
        List<EntityRelationship> relationships,
        String rationale
) {
    public record Entity(
            String name,
            String module,
            String description,
            List<Attribute> attributes
    ) {
        public record Attribute(
                String name,
                String type,
                boolean identifier,
                boolean required,
                String note
        ) {}
    }

    public record EntityRelationship(
            String from,
            String to,
            String cardinality, // ONE_TO_ONE | ONE_TO_MANY | MANY_TO_ONE | MANY_TO_MANY
            String description
    ) {}
}
