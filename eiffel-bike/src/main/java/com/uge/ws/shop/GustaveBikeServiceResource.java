package com.uge.ws.shop;

import com.uge.ws.common.Bike;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/shop")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GustaveBikeServiceResource {

    private final GustaveBikeShopService service = new GustaveBikeShopService();

    // ----- bikes for sale ----- //

    @GET
    @Path("/bikes")
    public List<Bike> listBikes(
            @QueryParam("currency") String currency) {
        return service.listBikesForSale(currency);
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

    // ----- baskets ----- //

    public static class CreateBasketRequest {
        public long customerId;
    }

    @POST
    @Path("/baskets")
    public Basket createBasket(CreateBasketRequest req) {
        return service.createBasket(req.customerId);
    }

    public static class AddItemRequest {
        public long bikeId;
        public int quantity;
    }

    @POST
    @Path("/baskets/{basketId}/items")
    public Basket addItem(@PathParam("basketId") long basketId, AddItemRequest req) {
        return service.addItem(basketId, req.bikeId, req.quantity);
    }

    @GET
    @Path("/baskets/{basketId}")
    public Response getBasket(@PathParam("basketId") long basketId) {
        Basket basket = service.getBasket(basketId);
        if (basket == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(basket).build();
    }

    @POST
    @Path("/baskets/{basketId}/checkout")
    public GustaveBikeShopService.CheckoutResult checkout(
            @PathParam("basketId") long basketId,
            @QueryParam("currency") String currency
    ) {
        return service.checkout(basketId, currency);
    }
}
