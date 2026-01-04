// src/main/java/com/uge/ws/shop/GustaveBikeServiceResource.java
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

    private final GustaveBikeShopService shop = new GustaveBikeShopService();

    @GET
    @Path("/bikes")
    public List<Bike> listSellableBikes() {
        return shop.listSellableBikes();
    }

    @GET
    @Path("/bikes/{id}")
    public Response getBike(@PathParam("id") long id) {
        return shop.getSellableBike(id)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @POST
    @Path("/baskets")
    public Basket createBasket(@QueryParam("customerId") long customerId) {
        return shop.createBasket(customerId);
    }

    @POST
    @Path("/baskets/{basketId}/items")
    public Response addItem(@PathParam("basketId") long basketId, BasketItem item) {
        return shop.addItem(basketId, item.getBikeId(), item.getQuantity())
                .map(Response::ok)
                .orElse(Response.status(Response.Status.BAD_REQUEST))
                .build();
    }

    @POST
    @Path("/baskets/{basketId}/checkout")
    public Response checkout(@PathParam("basketId") long basketId,
                             @QueryParam("currency") @DefaultValue("EUR") String currency) {
        var result = shop.checkout(basketId, currency);
        if (result.isSuccess()) {
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
    }
}
