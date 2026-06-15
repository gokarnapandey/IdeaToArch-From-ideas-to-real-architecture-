package com.webSearch.docs;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Zips a directory tree into a temp archive and returns its path. */
public final class ZipBundler {

    private ZipBundler() {
    }

    public static Path zip(Path sourceDir) throws IOException {
        Path zip = Files.createTempFile("docs-", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip));
             Stream<Path> walk = Files.walk(sourceDir)) {
            walk.filter(Files::isRegularFile).forEach(file -> addEntry(zos, sourceDir, file));
        }
        return zip;
    }

    private static void addEntry(ZipOutputStream zos, Path root, Path file) {
        String entryName = root.relativize(file).toString().replace('\\', '/');
        try {
            zos.putNextEntry(new ZipEntry(entryName));
            Files.copy(file, (OutputStream) zos);
            zos.closeEntry();
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Failed to add " + entryName + " to zip", e);
        }
    }
}
