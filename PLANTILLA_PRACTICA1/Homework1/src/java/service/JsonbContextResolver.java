package service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * Configures JSON-B to format (pretty-print) all JSON responses
 * with indentation and line breaks.
 */
@Provider
public class JsonbContextResolver implements ContextResolver<Jsonb> {
    private final Jsonb jsonb;

    public JsonbContextResolver() {
        JsonbConfig config = new JsonbConfig().withFormatting(true);
        this.jsonb = JsonbBuilder.create(config);
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}
