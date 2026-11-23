package service;

import authn.Secured;
import com.sun.xml.messaging.saaj.util.Base64;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import model.entities.Tool;

/**
 * REST Service for Tool resource.
 * Endpoints:
 * - GET /rest/api/v1/tool?author=${author}&language=${language} (DB filtered, public/private projection)
 * - GET /rest/api/v1/tool/{id} (requires auth if private)
 * - POST /rest/api/v1/tool (requires @Secured, validations, returns 201 Created)
 * - PUT /rest/api/v1/tool/{id} (optional, requires @Secured)
 * - DELETE /rest/api/v1/tool/{id} (optional, requires @Secured)
 * 
 * @authors Lucas Sierra, Adrian Rusu
 */
@Stateless
@Path("tool")
public class ToolFacadeREST extends AbstractFacade<Tool> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public ToolFacadeREST() {
        super(Tool.class);
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
     * Formats a Tool into a representation according to visibility:
     * - Public or authenticated: full details.
     * - Private & anonymous: only id, nom, autor, llenguatge.
     */
    private Object formatTool(Tool t, boolean authenticated) {
        if (!t.isPrivat() || authenticated) {
            return t;
        }
        Map<String, Object> reduced = new LinkedHashMap<>();
        reduced.put("id", t.getId());
        reduced.put("nom", t.getNom());
        reduced.put("autor", t.getAutor());
        reduced.put("llenguatge", t.getLlenguatge());
        reduced.put("privat", true);
        return reduced;
    }

    /**
     * 1. GET /rest/api/v1/tool?author=${author}&language=${language}
     * Lists tools filtered in Database and ordered by name.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTools(
            @QueryParam("author") String author,
            @QueryParam("language") String language,
            @Context HttpHeaders headers) {

        StringBuilder jpql = new StringBuilder("SELECT t FROM Tool t WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (author != null && !author.trim().isEmpty()) {
            jpql.append("AND (LOWER(t.autor.user) = LOWER(:author) OR LOWER(t.autor.nom) = LOWER(:author)) ");
            params.put("author", author.trim());
        }

        if (language != null && !language.trim().isEmpty()) {
            jpql.append("AND LOWER(t.llenguatge) = LOWER(:language) ");
            params.put("language", language.trim());
        }

        jpql.append("ORDER BY t.nom ASC");

        TypedQuery<Tool> query = em.createQuery(jpql.toString(), Tool.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<Tool> list = query.getResultList();
        boolean authenticated = isAuthenticated(headers);

        List<Object> result = new ArrayList<>();
        for (Tool t : list) {
            result.add(formatTool(t, authenticated));
        }

        return Response.ok(result).build();
    }

    /**
     * 2. GET /rest/api/v1/tool/{id}
     * Returns tool details. If private, requires authentication.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToolById(@PathParam("id") Long id, @Context HttpHeaders headers) {
        Tool tool = super.find(id);
        if (tool == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Tool not found\"}")
                    .build();
        }

        if (tool.isPrivat() && !isAuthenticated(headers)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Tool is private, authentication required\"}")
                    .build();
        }

        return Response.ok(tool).build();
    }

    /**
     * 3. POST /rest/api/v1/tool
     * Creates a new tool. Requires authentication.
     * Validates: non-empty name, existing code, existing author.
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTool(Tool tool, @Context HttpHeaders headers) {
        // Validation: name not empty
        if (tool.getNom() == null || tool.getNom().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El nom de l'eina no pot ser buit\"}")
                    .build();
        }

        // Validation: code not empty
        if (tool.getCodi() == null || tool.getCodi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El codi de l'eina no pot ser buit\"}")
                    .build();
        }

        // Validation: author exists
        if (tool.getAutor() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Cal indicar l'autor de l'eina\"}")
                    .build();
        }

        Customer author = null;
        if (tool.getAutor().getId() != null) {
            author = em.find(Customer.class, tool.getAutor().getId());
        } else if (tool.getAutor().getUser() != null) {
            try {
                author = em.createNamedQuery("Customer.findByUser", Customer.class)
                        .setParameter("user", tool.getAutor().getUser())
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
        tool.setAutor(author);

        // Default language to Python if not provided
        if (tool.getLlenguatge() == null || tool.getLlenguatge().trim().isEmpty()) {
            tool.setLlenguatge("Python");
        }

        // Creation date
        if (tool.getDataCreacio() == null) {
            tool.setDataCreacio(LocalDate.now());
        }

        super.create(tool);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id", tool.getId());
        resp.put("message", "Eina creada correctament");

        URI location = URI.create("/tool/" + tool.getId());
        return Response.created(location).entity(resp).build();
    }

    /**
     * 4. PUT /rest/api/v1/tool/{id} (Optional)
     * Modifies an existing tool. Requires authentication.
     */
    @PUT
    @Secured
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTool(@PathParam("id") Long id, Tool updated) {
        Tool existing = super.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Tool not found\"}")
                    .build();
        }

        if (updated.getNom() != null && !updated.getNom().trim().isEmpty()) {
            existing.setNom(updated.getNom());
        }
        if (updated.getDescripcio() != null) {
            existing.setDescripcio(updated.getDescripcio());
        }
        if (updated.getCodi() != null && !updated.getCodi().trim().isEmpty()) {
            existing.setCodi(updated.getCodi());
        }
        if (updated.getLlenguatge() != null && !updated.getLlenguatge().trim().isEmpty()) {
            existing.setLlenguatge(updated.getLlenguatge());
        }
        if (updated.getVersio() != null) {
            existing.setVersio(updated.getVersio());
        }
        existing.setPrivat(updated.isPrivat());

        super.edit(existing);
        return Response.ok(existing).build();
    }

    /**
     * 5. DELETE /rest/api/v1/tool/{id} (Optional)
     * Deletes a tool. Requires authentication.
     */
    @DELETE
    @Secured
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTool(@PathParam("id") Long id) {
        Tool existing = super.find(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Tool not found\"}")
                    .build();
        }

        super.remove(existing);
        return Response.ok("{\"message\": \"Tool deleted successfully\"}").build();
    }
}
