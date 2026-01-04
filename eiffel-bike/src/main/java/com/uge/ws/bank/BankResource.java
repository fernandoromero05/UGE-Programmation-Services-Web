// src/main/java/com/uge/ws/bank/BankResource.java
package com.uge.ws.bank;

import com.uge.ws.common.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankResource {

    private final BankService bankService = new BankService();
    private final DataStore store = DataStore.get();

    @GET
    @Path("/accounts/{id}")
    public Response getAccount(@PathParam("id") String id) {
        var acc = store.getAccounts().get(id);
        if (acc == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(acc).build();
    }

    @POST
    @Path("/payments")
    public PaymentResponse pay(PaymentRequest req) {
        return bankService.processPayment(req);
    }
}
