package com.webSearch.docs;

import com.webSearch.agent.dto.Lld;
import com.webSearch.session.DesignSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a minimal but valid OpenAPI 3.0.3 document deterministically from the LLD's
 * controllers and DTOs (no springdoc — there is no live API to scan yet).
 */
public final class OpenApiYamlBuilder {

    private static final Set<String> HTTP_METHODS =
            Set.of("get", "put", "post", "delete", "patch", "options", "head", "trace");

    private OpenApiYamlBuilder() {
    }

    public static String build(DesignSession s) {
        Lld lld = s.getLld();

        // 1) Collect schemas first so paths only $ref schemas that actually exist.
        Map<String, Lld.JavaType> schemas = new LinkedHashMap<>();
        if (lld != null) {
            collectSchemas(schemas, lld.entities());
            collectSchemas(schemas, lld.dtos());
        }

        // 2) Group endpoints by path then method.
        Map<String, Map<String, Lld.ControllerSpec.Endpoint>> paths = new LinkedHashMap<>();
        if (lld != null && lld.controllers() != null) {
            for (Lld.ControllerSpec c : lld.controllers()) {
                if (c.endpoints() == null) {
                    continue;
                }
                for (Lld.ControllerSpec.Endpoint e : c.endpoints()) {
                    String fp = fullPath(c.basePath(), e.path());
                    String method = method(e.httpMethod());
                    paths.computeIfAbsent(fp, k -> new LinkedHashMap<>()).put(method, e);
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("openapi: 3.0.3\n");
        sb.append("info:\n");
        sb.append("  title: ").append(q(title(s.getIdea()))).append("\n");
        sb.append("  version: \"0.1.0\"\n");
        sb.append("paths:\n");
        if (paths.isEmpty()) {
            sb.append("  {}\n");
        } else {
            for (Map.Entry<String, Map<String, Lld.ControllerSpec.Endpoint>> pe : paths.entrySet()) {
                sb.append("  ").append(q(pe.getKey())).append(":\n");
                for (Map.Entry<String, Lld.ControllerSpec.Endpoint> me : pe.getValue().entrySet()) {
                    Lld.ControllerSpec.Endpoint e = me.getValue();
                    sb.append("    ").append(me.getKey()).append(":\n");
                    sb.append("      summary: ").append(q(e.summary())).append("\n");
                    String reqRef = refIfPresent(e.requestDto(), schemas);
                    if (reqRef != null) {
                        sb.append("      requestBody:\n")
                                .append("        content:\n")
                                .append("          application/json:\n")
                                .append("            schema:\n")
                                .append("              $ref: ").append(q("#/components/schemas/" + reqRef)).append("\n");
                    }
                    sb.append("      responses:\n")
                            .append("        \"200\":\n")
                            .append("          description: OK\n");
                    String resRef = refIfPresent(e.responseDto(), schemas);
                    if (resRef != null) {
                        sb.append("          content:\n")
                                .append("            application/json:\n")
                                .append("              schema:\n")
                                .append("                $ref: ").append(q("#/components/schemas/" + resRef)).append("\n");
                    }
                }
            }
        }

        if (!schemas.isEmpty()) {
            sb.append("components:\n  schemas:\n");
            for (Map.Entry<String, Lld.JavaType> se : schemas.entrySet()) {
                sb.append("    ").append(se.getKey()).append(":\n      type: object\n");
                List<Lld.JavaType.Field> fields = se.getValue().fields();
                if (fields != null && !fields.isEmpty()) {
                    sb.append("      properties:\n");
                    for (Lld.JavaType.Field f : fields) {
                        if (f.name() == null || !isSimpleIdent(f.name())) {
                            continue;
                        }
                        sb.append("        ").append(f.name()).append(":\n          type: string\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    private static void collectSchemas(Map<String, Lld.JavaType> out, List<Lld.JavaType> types) {
        if (types == null) {
            return;
        }
        for (Lld.JavaType t : types) {
            if (t.name() != null && isSimpleIdent(t.name())) {
                out.putIfAbsent(t.name(), t);
            }
        }
    }

    private static String refIfPresent(String dto, Map<String, Lld.JavaType> schemas) {
        if (dto == null) {
            return null;
        }
        String d = dto.trim();
        if (d.isEmpty() || d.equalsIgnoreCase("void") || d.equalsIgnoreCase("none") || d.equals("-")) {
            return null;
        }
        return (isSimpleIdent(d) && schemas.containsKey(d)) ? d : null;
    }

    private static String fullPath(String base, String path) {
        String b = base == null ? "" : base.trim();
        String p = path == null ? "" : path.trim();
        String combined = (p.startsWith("/") && !b.isEmpty() && p.startsWith(b)) ? p : (b + "/" + p);
        combined = combined.replaceAll("/{2,}", "/");
        if (!combined.startsWith("/")) {
            combined = "/" + combined;
        }
        if (combined.length() > 1 && combined.endsWith("/")) {
            combined = combined.substring(0, combined.length() - 1);
        }
        return combined.isEmpty() ? "/" : combined;
    }

    private static String method(String httpMethod) {
        if (httpMethod == null) {
            return "get";
        }
        String m = httpMethod.trim().toLowerCase();
        return HTTP_METHODS.contains(m) ? m : "get";
    }

    private static String title(String idea) {
        String t = idea == null ? "Generated" : idea.replaceAll("\\s+", " ").trim();
        if (t.length() > 60) {
            t = t.substring(0, 60);
        }
        return t + " API";
    }

    private static boolean isSimpleIdent(String s) {
        return s.matches("[A-Za-z_][A-Za-z0-9_]*");
    }

    private static String q(String s) {
        String v = s == null ? "" : s;
        v = v.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
        return "\"" + v + "\"";
    }
}
