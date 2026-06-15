package com.webSearch.agent.dto;

import java.util.List;

/** Output of the LLD agent: production low-level design for the system. */
public record Lld(
        List<PackageNode> packages,
        List<JavaType> entities,
        List<JavaType> dtos,
        List<RepoSpec> repositories,
        List<ServiceSpec> services,
        List<ControllerSpec> controllers,
        List<String> configs,
        List<String> utils,
        ExceptionHierarchy exceptions
) {
    public record PackageNode(String path, String purpose) {}

    public record JavaType(String name, String pkg, List<Field> fields) {
        public record Field(String name, String type, String note) {}
    }

    public record RepoSpec(String name, String entity, List<String> methods) {}

    public record ServiceSpec(String iface, String impl, List<MethodSpec> methods) {
        public record MethodSpec(String signature, String behavior) {}
    }

    public record ControllerSpec(String name, String basePath, List<Endpoint> endpoints) {
        public record Endpoint(
                String httpMethod,
                String path,
                String requestDto,
                String responseDto,
                String summary
        ) {}
    }

    public record ExceptionHierarchy(String base, List<ExceptionType> types) {
        public record ExceptionType(String name, String parent, int httpStatus, String when) {}
    }
}
