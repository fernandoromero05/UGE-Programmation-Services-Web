// src/main/java/com/uge/ws/corp/EiffelBikeCorpResource.java
package com.uge.ws.corp;

import com.uge.ws.common.Bike;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/corp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EiffelBikeCorpResource {

    private final EiffelBikeCorpService service = new EiffelBikeCorpService();

    @GET
    @Path("/bikes")
    public List<Bike> getBikes() {
        return service.listBikes();
    }

    @POST
    @Path("/bikes/{bikeId}/rent")
    public Response rent(@PathParam("bikeId") long bikeId,
                         @QueryParam("userId") long userId) {
        String result = service.rentBike(bikeId, userId);
        return Response.ok(new SimpleMessage(result)).build();
    }

    @POST
    @Path("/bikes/{bikeId}/return")
    public Response returnBike(@PathParam("bikeId") long bikeId, String note) {
        String result = service.returnBike(bikeId, note);
        return Response.ok(new SimpleMessage(result)).build();
    }

    // Small DTO for messages
    public static class SimpleMessage {
        public String message;
        public SimpleMessage() {}
        public SimpleMessage(String message) { this.message = message; }
    }
}
