package com.webSearch.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * File-based persistence for {@link DesignSession}: one JSON file per session under
 * {@code app.sessions.dir} (default {@code data/sessions}). Lets a session survive a JVM restart
 * (including {@code spring-boot-devtools} reloads) so it can be reloaded later.
 *
 * <p>Reuses the Spring-configured {@link ObjectMapper}, so the on-disk format matches the REST API
 * format exactly (same {@code Instant} and record serialization).
 */
@Component
public class FileSessionRepository {

    private static final Logger log = LoggerFactory.getLogger(FileSessionRepository.class);

    private final ObjectMapper mapper;
    private final Path dir;

    public FileSessionRepository(ObjectMapper mapper,
                                 @Value("${app.sessions.dir:data/sessions}") String dir) {
        this.mapper = mapper;
        this.dir = Path.of(dir);
        try {
            Files.createDirectories(this.dir);
            log.info("Persisting design sessions to {}", this.dir.toAbsolutePath());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create sessions directory: " + this.dir, e);
        }
    }

    /** Persist (or overwrite) a session atomically. Persistence failures are logged, never thrown. */
    public void save(DesignSession session) {
        Path target = fileFor(session.getId());
        Path tmp = target.resolveSibling(session.getId() + ".json.tmp");
        try {
            Files.write(tmp, mapper.writeValueAsBytes(session));
            try {
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicUnsupported) {
                // Some filesystems don't support ATOMIC_MOVE; fall back to a plain replace.
                Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            log.warn("Failed to persist session {}: {}", session.getId(), e.toString());
            try {
                Files.deleteIfExists(tmp);
            } catch (IOException ignored) {
                // best-effort cleanup of the temp file
            }
        }
    }

    /** Load a persisted session, or {@code null} if none exists / it can't be read. */
    public DesignSession load(String id) {
        Path file = fileFor(id);
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try {
            return mapper.readValue(Files.readAllBytes(file), DesignSession.class);
        } catch (Exception e) {
            log.warn("Failed to read persisted session {}: {}", id, e.toString());
            return null;
        }
    }

    private Path fileFor(String id) {
        return dir.resolve(id + ".json");
    }
}
