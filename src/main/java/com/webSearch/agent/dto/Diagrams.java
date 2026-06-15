package com.webSearch.agent.dto;

/** Output of the Diagram agent: each diagram emitted in both Mermaid and PlantUML. */
public record Diagrams(
        Diagram sequence,
        Diagram classDiagram,
        Diagram component,
        Diagram requestFlow
) {
    public record Diagram(String mermaid, String plantUml) {}
}
