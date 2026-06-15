package com.webSearch.controller;

import com.webSearch.agent.AgentType;
import com.webSearch.docs.DocumentationExporter;
import com.webSearch.service.DesignService;
import com.webSearch.session.DesignSession;
import com.webSearch.session.SessionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Drives the interactive design pipeline:
 * <ul>
 *   <li>POST /api/design — create a session (no agents run)</li>
 *   <li>GET  /api/design/{id} — the typed session (status + artifacts)</li>
 *   <li>POST /api/design/{id}/steps/{agent} — run one step, optional {feedback}</li>
 *   <li>POST /api/design/{id}/export — download the generated docs/ as a zip</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/design")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;
    private final SessionStore store;
    private final DocumentationExporter exporter;

    @PostMapping
    public DesignSessionRef start(@RequestBody StartDesignRequest request) {
        DesignSession session = designService.start(request.idea());
        return new DesignSessionRef(session.getId(), session.getStatus().name());
    }

    @GetMapping("/{id}")
    public DesignSession get(@PathVariable String id) {
        return store.require(id);
    }

    /** Run a single step (agent) on demand, optionally with user feedback to revise it. */
    @PostMapping("/{id}/steps/{agent}")
    public DesignSession runStep(@PathVariable String id,
                                 @PathVariable String agent,
                                 @RequestBody(required = false) StepRunRequest body) {
        AgentType type;
        try {
            type = AgentType.valueOf(agent.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown step: " + agent);
        }
        return designService.runStep(id, type, body == null ? null : body.feedback());
    }

    @PostMapping("/{id}/export")
    public ResponseEntity<Resource> export(@PathVariable String id) throws IOException {
        DesignSession session = store.require(id);
        Path zip = exporter.exportToZip(session);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"docs-" + id + ".zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(new FileSystemResource(zip));
    }
}
