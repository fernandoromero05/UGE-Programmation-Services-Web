package com.uge.ws.corp;

import com.uge.ws.common.Bike;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Path("/corp")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EiffelBikeCorpResource {

    private final EiffelBikeCorpService service = new EiffelBikeCorpService();

    // ---------- bikes ---------- //

    @GET
    @Path("/bikes")
    public List<Bike> getAllBikes() {
        return service.listAllBikes();
    }

    @GET
    @Path("/bikes/{id}")
    public Response getBike(@PathParam("id") long id) {
        Bike bike = service.getBike(id);
        if (bike == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(bike).build();
    }

    @GET
    @Path("/bikes/{id}/notes")
    public List<String> getBikeNotes(@PathParam("id") long bikeId) {
        return service.getBikeNotes(bikeId);   // delegate to EiffelBikeCorpService
    }

    // ---------- rent ---------- //

    public static class RentRequest {
        public long bikeId;
        public long userId;
    }

    @POST
    @Path("/rent")
    public EiffelBikeCorpService.RentResult rentBike(RentRequest req) {
        return service.rentBike(req.bikeId, req.userId);
    }

    // ---------- return ---------- //

    public static class ReturnRequest {
        public long rentalId;
        public String note;
    }

    @POST
    @Path("/return")
    public EiffelBikeCorpService.ReturnResult returnBike(ReturnRequest req) {
        return service.returnBike(req.rentalId, req.note);
    }

    // ---------- user rentals ---------- //

    @GET
    @Path("/rentals/user/{userId}")
    public List<RentalDto> getRentalsForUser(@PathParam("userId") long userId) {
        List<Rental> rentals = service.getRentalsByUser(userId);
        List<RentalDto> result = new ArrayList<>();

        for (Rental r : rentals) {
            RentalDto dto = new RentalDto();
            dto.id = r.getId();
            dto.bikeId = r.getBikeId();
            dto.userId = r.getUserId();
            dto.status = r.getStatus() != null ? r.getStatus().name() : null;
            dto.start = r.getStart() != null ? r.getStart().toString() : null;
            dto.end = r.getEnd() != null ? r.getEnd().toString() : null;
            result.add(dto);
        }
        return result;
    }

    /** Simple DTO so we avoid LocalDateTime in JSON. */
    public static class RentalDto {
        public long id;
        public long bikeId;
        public long userId;
        public String status;
        public String start;  // ISO string
        public String end;    // ISO string
    }
}