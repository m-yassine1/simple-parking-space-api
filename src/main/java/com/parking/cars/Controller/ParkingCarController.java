package com.parking.cars.Controller;

import com.parking.cars.model.Car;
import com.parking.cars.service.ParkingService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/api/v1")
public class ParkingCarController {
    private static final Logger log = LoggerFactory.getLogger(ParkingCarController.class);

    @Inject
    ParkingService parkingService;

    @POST @Path("/park")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> parkCar(@Valid Car car) {
        return parkingService.park(car).chain(carDto -> {
            try {
                return Uni.createFrom().item(Response
                        .created(new URI(String.format("/get-info?car_number=%s", carDto.carId)))
                        .entity(carDto)
                        .build());
            } catch (URISyntaxException e) {
                log.error("Error while creating URI", e);
                return Uni.createFrom().failure(e);
            }
        });
    }

    @DELETE @Path("/unpark/{slot_id:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> unparkCar(@PathParam("slot_id") Integer slotId) {
        return parkingService.unpark(slotId).chain(() -> {
            return Uni.createFrom().item(Response.noContent().build());
        });
    }

    @GET @Path("/get-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getInfo(
            @QueryParam("car_number") String carNumber,
            @QueryParam("slot_number") @PositiveOrZero @Valid Integer slotId) {
        return parkingService.getInfo(carNumber, slotId).chain(carDto -> {
            return Uni.createFrom().item(Response.ok(carDto).build());
        });
    }
}
