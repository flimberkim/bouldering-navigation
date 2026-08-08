package com.boulderingnavigation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.Yaml;

/**
 * Fails the build when api-spec.yaml drifts from the backend's actual routes,
 * so the backend agent can't finish a task with a stale contract.
 */
@SpringBootTest
class ApiSpecContractTest {

    private static final String CONTROLLER_PACKAGE_PREFIX = "com.boulderingnavigation.controller";

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void apiSpecMatchesActualRoutes() throws IOException {
        Set<String> actualRoutes = actualRoutes();
        Set<String> specRoutes = specRoutes();

        assertEquals(specRoutes, actualRoutes,
                "api-spec.yaml must exactly match the backend's actual routes"
                        + " — update api-spec.yaml in the same change as any endpoint change.\n"
                        + "In spec but not implemented: " + diff(specRoutes, actualRoutes) + "\n"
                        + "Implemented but not in spec: " + diff(actualRoutes, specRoutes));
    }

    private Set<String> diff(Set<String> a, Set<String> b) {
        Set<String> result = new LinkedHashSet<>(a);
        result.removeAll(b);
        return result;
    }

    private Set<String> actualRoutes() {
        Set<String> routes = new LinkedHashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.getBeanType().getPackageName().startsWith(CONTROLLER_PACKAGE_PREFIX)) {
                continue;
            }
            RequestMappingInfo info = entry.getKey();
            for (String pattern : info.getPatternValues()) {
                for (RequestMethod method : info.getMethodsCondition().getMethods()) {
                    routes.add(method.name() + " " + pattern);
                }
            }
        }
        return routes;
    }

    @SuppressWarnings("unchecked")
    private Set<String> specRoutes() throws IOException {
        Map<String, Object> spec;
        try (var in = Files.newInputStream(Path.of("api-spec.yaml"))) {
            spec = new Yaml().load(in);
        }
        Set<String> routes = new LinkedHashSet<>();
        Map<String, Object> paths = (Map<String, Object>) spec.get("paths");
        for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
            Map<String, Object> operations = (Map<String, Object>) pathEntry.getValue();
            for (String httpMethod : operations.keySet()) {
                routes.add(httpMethod.toUpperCase(Locale.ROOT) + " " + pathEntry.getKey());
            }
        }
        return routes;
    }
}
