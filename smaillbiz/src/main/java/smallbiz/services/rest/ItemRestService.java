package smallbiz.services.rest;

import java.net.URI;
import java.util.Collection;
 
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
 
import smaillbiz.entities.Item;
 
@Path("/item")
@Produces ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Stateless
public class ItemRestService {
    //the PersistenceContext annotation is a shortcut that hides the fact
    //that, an entity manager is always obtained from an EntityManagerFactory.
    //The peristitence.xml file defines persistence units which is supplied by name
    //to the EntityManagerFactory, thus  dictating settings and classes used by the
    //entity manager
    @PersistenceContext(unitName = "testPU")
    private EntityManager em;
 
    //Inject UriInfo to build the uri used in the POST response
    @Context
    private UriInfo uriInfo;
 
    @POST
    public Response createItem(Item item){
        if(item == null){
            throw new BadRequestException();
        }
        em.persist(item);
 
        //Build a uri with the Item id appended to the absolute path
        //This is so the client gets the Item id and also has the path to the resource created
        URI itemUri = uriInfo.getAbsolutePathBuilder().path(item.getId()).build();
 
        //The created response will not have a body. The itemUri will be in the Header
        return Response.created(itemUri).build();
    }
 
    @GET
    @Path("{id}")
    public Response getItem(@PathParam("id") String id){
        Item item = em.find(Item.class, id);
 
        if(item == null){
            throw new NotFoundException();
        }
 
        return Response.ok(item).build();
    }
 
    //Response.ok() does not accept collections
    //But we return a collection and JAX-RS will generate header 200 OK and
    //will handle converting the collection to xml or json as the body
    @GET
    public Collection<Item> getItems(){
        TypedQuery<Item> query = em.createNamedQuery("Item.findAll", Item.class);
        return query.getResultList();
    }
 
    @PUT
    @Path("{id}")
    public Response updateItem(Item item, @PathParam("id") String id){
        if(id == null){
            throw new BadRequestException();
        }
 
        //Ideally we should check the id is a valid UUID. Not implementing for now
        item.setId(id);
        em.merge(item);
 
        return Response.ok().build();
    }
 
    @DELETE
    @Path("{id}")
    public Response deleteItem(@PathParam("id") String id){
        Item item = em.find(Item.class, id);
        if(item == null){
            throw new NotFoundException();
        }
        em.remove(item);
        return Response.noContent().build();
    }
 
}