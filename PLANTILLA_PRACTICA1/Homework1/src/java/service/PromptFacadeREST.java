package service;

import authn.Secured;
import com.sun.xml.messaging.saaj.util.Base64;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.entities.Customer;
import model.entities.Prompt;
import model.entities.Tool;

/**
 * REST Service for Prompt resource.
 * Endpoints:
 * - GET /rest/api/v1/prompt?author=${author}&tag=${tag} (DB filtered, public/private projection)
 * - GET /rest/api/v1/prompt/{id} (requires auth if private)
 * - POST /rest/api/v1/prompt (requires @Secured, validations, returns 201 Created)
 * - PUT /rest/api/v1/prompt/{id} (optional, requires @Secured)
 * - GET /rest/api/v1/prompt/{id}/tools (associated tools, 404 if none, requires auth if private)
 * - PUT /rest/api/v1/prompt/{id}/tools (requires @Secured, updates associated tools by ID list)
 * 
 * @authors Lucas Sierra, Adrian Rusu
 */
@Stateless
@Path("prompt")
public class PromptFacadeREST extends AbstractFacade<Prompt> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public PromptFacadeREST() {
        super(Prompt.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Helper to verify if the incoming request has valid Basic Auth credentials
     */
    private boolean isAuthenticated(HttpHeaders headers) {
        if (headers == null) return false;
        List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) return false;

        String auth = authHeaders.get(0);
        if (!auth.startsWith("Basic ")) return false;

        try {
            String encoded = auth.substring("Basic ".length()).trim();
            String decoded = Base64.base64Decode(encoded);
            StringTokenizer tokenizer = new StringTokenizer(decoded, ":");
            if (!tokenizer.hasMoreTokens()) return false;
            String username = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens()) return false;
            String password = tokenizer.nextToken();

            TypedQuery<Customer> query = em.createNamedQuery("Customer.findByUser", Customer.class);
            Customer c = query.setParameter("user", username).getSingleResult();
            return c.getPass().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Formats a Prompt according to visibility:
     * - Public or authenticated: full info.
     * - Private & anonymous: only id, titol, autor, dataCreacio.
     */
    private Object formatPrompt(Prompt p, boolean authenticated) {
        if (!p.isPrivat() || authenticated) {
            return p;
        }
        Map<String, Object> reduced = new LinkedHashMap<>();
        reduced.put("id", p.getId());
        reduced.put("titol", p.getTitol());
        reduced.put("autor", p.getAutor());
        reduced.put("dataCreacio", p.getDataCreacio());
        reduced.put("privat", true);
        return reduced;
    }

    /**
     * 1. GET /rest/api/v1/prompt?author=${author}&tag=${tag}
     * Lists prompts ordered by title with database filtering.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrompts(
            @QueryParam("author") String author,
            @QueryParam("tag") String tag,
            @Context HttpHeaders headers) {

        StringBuilder jpql = new StringBuilder("SELECT p FROM Prompt p WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (author != null && !author.trim().isEmpty()) {
            jpql.append("AND (LOWER(p.autor.user) = LOWER(:author) OR LOWER(p.autor.nom) = LOWER(:author)) ");
            params.put("author", author.trim());
        }

        if (tag != null && !tag.trim().isEmpty()) {
            jpql.append("AND :tag MEMBER OF p.etiquetes ");
            params.put("tag", tag.trim().toLowerCase());
        }

        jpql.append("ORDER BY p.titol ASC");

        TypedQuery<Prompt> query = em.createQuery(jpql.toString(), Prompt.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<Prompt> list = query.getResultList();
        boolean authenticated = isAuthenticated(headers);

        List<Object> result = new ArrayList<>();
        for (Prompt p : list) {
            result.add(formatPrompt(p, authenticated));
        }

        return Response.ok(result).build();
    }

    /**
     * 2. GET /rest/api/v1/prompt/{id}
     * Returns full prompt details. If private, requires authentication.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPromptById(@PathParam("id") Long id, @Context HttpHeaders headers) {
        Prompt prompt = super.find(id);
        if (prompt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Prompt not found\"}")
                    .build();
        }

        if (prompt.isPrivat() && !isAuthenticated(headers)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Prompt is private, authentication required\"}")
                    .build();
        }

        return Response.ok(prompt).build();
    }

    /**
     * 3. POST /rest/api/v1/prompt
     * Creates a new prompt. Requires authentication.
     * Validates: non-empty title, existing template, existing author, existing associated tools.
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPrompt(Prompt prompt, @Context HttpHeaders headers) {
        // Validation: title not empty
        if (prompt.getTitol() == null || prompt.getTitol().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El títol del prompt no pot ser buit\"}")
                    .build();
        }

        // Validation: template exists
        if (prompt.getPlantilla() == null || prompt.getPlantilla().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La plantilla (contingut) del prompt és obligatòria\"}")
                    .build();
        }

        // Validation: author exists
        if (prompt.getAutor() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Cal indicar l'autor del prompt\"}")
                    .build();
        }

        Customer author = null;
        if (prompt.getAutor().getId() != null) {
            author = em.find(Customer.class, prompt.getAutor().getId());
        } else if (prompt.getAutor().getUser() != null) {
            try {
                author = em.createNamedQuery("Customer.findByUser", Customer.class)
                        .setParameter("user", prompt.getAutor().getUser())
                        .getSingleResult();
            } catch (NoResultException e) {
                author = null;
            }
        }

        if (author == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"L'autor indicat no existeix al sistema\"}")
                    .build();
        }
        prompt.setAutor(author);

        // Validation: associated tools exist
        Set<Tool> managedTools = new HashSet<>();
        if (prompt.getTools() != null && !prompt.getTools().isEmpty()) {
            for (Tool t : prompt.getTools()) {
                if (t.getId() == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"L'identificador d'eina és obligatori per associar-la\"}")
                            .build();
                }
                Tool found = em.find(Tool.class, t.getId());
                if (found == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"L'eina amb ID " + t.getId() + " no existeix al sistema\"}")
                            .build();
                }
                managedTools.add(found);
            }
        }
        prompt.setTools(managedTools);

        // Creation date
        if (prompt.getDataCreacio() == null) {
            prompt.setDataCreacio(LocalDate.now());
        }

        super.create(prompt);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", prompt.getId());
        resp.put("message", "Prompt creat correctament");

        URI location = URI.create("/prompt/" + prompt.getId());
        return Response.created(location).entity(resp).build();
    }

    /**
     * 4. PUT /rest/api/v1/prompt/{id} (Optional)
     * Modifies an existing prompt. Requires authentication.
     */
    @PUT
    @Secured
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePrompt(@PathParam("id") Long id, Prompt updated) {
        Prompt existing = super.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Prompt not found\"}")
                    .build();
        }

        if (updated.getTitol() != null && !updated.getTitol().trim().isEmpty()) {
            existing.setTitol(updated.getTitol());
        }
        if (updated.getDescripcio() != null) {
            existing.setDescripcio(updated.getDescripcio());
        }
        if (updated.getPlantilla() != null && !updated.getPlantilla().trim().isEmpty()) {
            existing.setPlantilla(updated.getPlantilla());
        }
        if (updated.getEtiquetes() != null) {
            existing.setEtiquetes(updated.getEtiquetes());
        }
        existing.setPrivat(updated.isPrivat());

        super.edit(existing);
        return Response.ok(existing).build();
    }

    /**
     * 5. GET /rest/api/v1/prompt/{id}/tools
     * Returns tools associated with the prompt.
     * Returns 404 if prompt doesn't exist or has no tools.
     * Requires authentication if prompt is private.
     */
    @GET
    @Path("{id}/tools")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPromptTools(@PathParam("id") Long id, @Context HttpHeaders headers) {
        Prompt prompt = super.find(id);
        if (prompt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Prompt not found\"}")
                    .build();
        }

        if (prompt.isPrivat() && !isAuthenticated(headers)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Prompt is private, authentication required\"}")
                    .build();
        }

        Set<Tool> tools = prompt.getTools();
        if (tools == null || tools.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No hi ha eines associades a aquest prompt\"}")
                    .build();
        }

        return Response.ok(tools).build();
    }

    /**
     * 6. PUT /rest/api/v1/prompt/{id}/tools
     * Updates tools associated with the prompt. Expects list of tool IDs.
     * Requires authentication.
     */
    @PUT
    @Secured
    @Path("{id}/tools")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePromptTools(@PathParam("id") Long id, List<Long> toolIds) {
        Prompt prompt = super.find(id);
        if (prompt == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Prompt not found\"}")
                    .build();
        }

        Set<Tool> newTools = new HashSet<>();
        if (toolIds != null) {
            for (Long toolId : toolIds) {
                Tool t = em.find(Tool.class, toolId);
                if (t == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"L'eina amb ID " + toolId + " no existeix\"}")
                            .build();
                }
                newTools.add(t);
            }
        }

        prompt.setTools(newTools);
        super.edit(prompt);

        return Response.ok(newTools).build();
    }
}
