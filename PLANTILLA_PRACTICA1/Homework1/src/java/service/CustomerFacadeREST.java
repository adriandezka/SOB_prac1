package service;

import authn.Secured;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.entities.Customer;

/**
 * REST Service for Customer resource.
 * Endpoints:
 * - GET /rest/api/v1/customer
 * - GET /rest/api/v1/customer/{id} (with HATEOAS links)
 * - PUT /rest/api/v1/customer/{id} (optional, requires @Secured)
 * 
 * @authors Lucas Sierra, Adrian Rusu
 */
@Stateless
@Path("customer")
public class CustomerFacadeREST extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public CustomerFacadeREST() {
        super(Customer.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * 1. GET /rest/api/v1/customer
     * Returns JSON list of all customers (passwords hidden via @JsonbTransient).
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        List<Customer> customers = em.createQuery("SELECT c FROM Customer c ORDER BY c.id ASC", Customer.class)
                .getResultList();
        return Response.ok(customers).build();
    }

    /**
     * 2. GET /rest/api/v1/customer/{id}
     * Returns customer details with HATEOAS links to last added prompt and tool.
     * Supports both numeric ID and username lookup.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") String idOrUser) {
        Customer customer = null;
        try {
            Long numId = Long.parseLong(idOrUser);
            customer = em.find(Customer.class, numId);
        } catch (NumberFormatException e) {
            // Not a numeric ID, try searching by username
            try {
                customer = em.createNamedQuery("Customer.findByUser", Customer.class)
                        .setParameter("user", idOrUser)
                        .getSingleResult();
            } catch (NoResultException ex) {
                customer = null;
            }
        }

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Customer not found\"}")
                    .build();
        }

        // Search last added prompt ID for this customer
        List<Long> lastPromptList = em.createQuery(
                "SELECT p.id FROM Prompt p WHERE p.autor = :customer ORDER BY p.id DESC", Long.class)
                .setParameter("customer", customer)
                .setMaxResults(1)
                .getResultList();

        // Search last added tool ID for this customer
        List<Long> lastToolList = em.createQuery(
                "SELECT t.id FROM Tool t WHERE t.autor = :customer ORDER BY t.id DESC", Long.class)
                .setParameter("customer", customer)
                .setMaxResults(1)
                .getResultList();

        // Build HATEOAS JSON using LinkedHashMap
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", customer.getId());
        result.put("nom", customer.getNom());
        result.put("user", customer.getUser());

        Map<String, Object> links = new LinkedHashMap<>();
        if (!lastPromptList.isEmpty()) {
            Map<String, String> promptLink = new HashMap<>();
            promptLink.put("href", "/prompt/" + lastPromptList.get(0));
            links.put("last-prompt-added", promptLink);
        }
        if (!lastToolList.isEmpty()) {
            Map<String, String> toolLink = new HashMap<>();
            toolLink.put("href", "/tool/" + lastToolList.get(0));
            links.put("last-tool-added", toolLink);
        }
        result.put("_links", links);

        return Response.ok(result).build();
    }

    /**
     * 3. PUT /rest/api/v1/customer/{id} (Optional)
     * Modifies customer details. Requires authentication.
     */
    @PUT
    @Secured
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") Long id, Customer updated) {
        Customer existing = em.find(Customer.class, id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Customer not found\"}")
                    .build();
        }

        if (updated.getNom() != null && !updated.getNom().trim().isEmpty()) {
            existing.setNom(updated.getNom());
        }
        if (updated.getUser() != null && !updated.getUser().trim().isEmpty()) {
            existing.setUser(updated.getUser());
        }
        if (updated.getPass() != null && !updated.getPass().trim().isEmpty()) {
            existing.setPass(updated.getPass());
        }

        super.edit(existing);
        return Response.ok(existing).build();
    }
}
