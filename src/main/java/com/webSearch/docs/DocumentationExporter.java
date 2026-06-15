package com.webSearch.docs;

import com.webSearch.agent.dto.ClaudePrompts;
import com.webSearch.agent.dto.Diagrams;
import com.webSearch.session.DesignSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Writes the full {@code docs/} tree for a session and bundles it into a downloadable zip. */
@Component
public class DocumentationExporter {

    public Path exportToZip(DesignSession session) throws IOException {
        Path root = Files.createTempDirectory("docs-" + session.getId() + "-");
        writeTree(session, root);
        return ZipBundler.zip(root);
    }

    private void writeTree(DesignSession s, Path root) throws IOException {
        write(root.resolve("README.md"), DocTemplates.readme(s));
        write(root.resolve("architecture.md"), DocTemplates.architecture(s));
        write(root.resolve("hld.md"), DocTemplates.hld(s));
        write(root.resolve("lld.md"), DocTemplates.lld(s));
        write(root.resolve("module-design.md"), DocTemplates.moduleDesign(s));
        write(root.resolve("entities.md"), DocTemplates.entities(s));
        write(root.resolve("database.md"), DocTemplates.database(s));
        write(root.resolve("api.md"), DocTemplates.api(s));
        write(root.resolve("sequence-diagrams.md"), DocTemplates.sequenceDiagrams(s));
        write(root.resolve("class-diagrams.md"), DocTemplates.classDiagrams(s));
        write(root.resolve("developer-guide.md"), DocTemplates.developerGuide(s));
        write(root.resolve("openapi.yaml"), OpenApiYamlBuilder.build(s));

        // Phase-wise Claude build prompts, one Markdown file per phase (ordered).
        ClaudePrompts prompts = s.getPrompts();
        if (prompts != null && prompts.phases() != null) {
            for (ClaudePrompts.BuildPhase ph : prompts.phases()) {
                write(root.resolve("prompts").resolve(safeFileName(ph.fileName(), "phase-" + ph.order())),
                        ph.markdown() == null ? "" : ph.markdown());
            }
        }

        // Raw diagram sources.
        Diagrams d = s.getDiagrams();
        if (d != null) {
            writePlantUml(root.resolve("diagrams/sequence.puml"), d.sequence());
            writePlantUml(root.resolve("diagrams/class.puml"), d.classDiagram());
            writePlantUml(root.resolve("diagrams/component.puml"), d.component());
        }
        if (s.getDatabase() != null && s.getDatabase().erMermaid() != null
                && !s.getDatabase().erMermaid().isBlank()) {
            write(root.resolve("diagrams/er.mmd"), s.getDatabase().erMermaid());
        }
    }

    private void writePlantUml(Path file, Diagrams.Diagram diagram) throws IOException {
        if (diagram != null && diagram.plantUml() != null && !diagram.plantUml().isBlank()) {
            write(file, diagram.plantUml());
        }
    }

    private void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content == null ? "" : content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** Sanitize an LLM-provided filename to prevent path traversal. */
    private String safeFileName(String fileName, String module) {
        String base = (fileName != null && !fileName.isBlank())
                ? fileName
                : (module != null && !module.isBlank() ? module : "module") + ".md";
        base = base.replaceAll("[^A-Za-z0-9._-]", "-");
        if (!base.toLowerCase().endsWith(".md")) {
            base = base + ".md";
        }
        return base;
    }
}
