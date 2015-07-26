/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package services;

import credentials.Database;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import model.Product;
import model.ProductList;

/**
 *
 * @author Wayne
 */

@Path("/products")
@ApplicationScoped
public class ProductsREST {
    
    @Inject
    ProductList productList;
    
    @GET
    @Produces("application/json")
    public Response doGet() {
        return Response.ok(productList.toJSON()).build();
    }
    
    @GET
    @Path("{productId}")
    @Produces("application/json")
    public Response doGet(@PathParam("productId") int id) {
        return Response.ok(productList.get(id).toJSON()).build();
    }
    
    @POST
    @Consumes("application/json")
    public Response doPost(JsonObject json) {
        Response result;
        
        try {
            productList.add(new Product(json));
            int id = Database.getLastInsertID();
            result = Response.ok("http://localhost:8080/4414assignment6/rs/products/" + id).build();
        } catch (Exception e) {
            result = Response.status(500).build();
            System.out.println(e);
        }
        return result;
    }
   
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response doPut(@PathParam("id") int id, JsonObject json) {
        Response result;
        
        try {
            productList.set(id, new Product(json));
            result = Response.ok("http://localhost:8080/4414assignment6/rs/products/" + id).build();
        } catch (Exception e) {
            result = Response.status(500).build();
        }
        return result;
    }
    
    @DELETE
    @Path("{id}")
    public Response doDelete(@PathParam("id") int id) {
        Response result;
        
        try {
            productList.remove(id);
            result = Response.ok().build();
        } catch (Exception e) {
            result = Response.status(500).build();
        }
        return result;
    }
    
}
