package com.webSearch.agent.dto;

import java.util.List;

/** Output of the Database agent: schema with constraints, indexes and an ER diagram. */
public record DatabaseDesign(
        List<Table> tables,
        List<Relationship> relationships,
        String erMermaid,
        String rationale
) {
    public record Table(
            String name,
            List<Column> columns,
            List<String> indexes,
            List<String> constraints
    ) {
        public record Column(
                String name,
                String type,
                boolean primaryKey,
                boolean nullable,
                String note
        ) {}
    }

    public record Relationship(
            String from,
            String to,
            String cardinality,
            String onDelete
    ) {}
}
