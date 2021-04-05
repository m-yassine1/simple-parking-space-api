package com.parking.cars.service.implementation;

import com.parking.cars.exception.ParkingCarException;
import com.parking.cars.exception.model.Error;
import com.parking.cars.model.Car;
import com.parking.cars.model.CarDto;
import com.parking.cars.service.ParkingService;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class ParkingCarServiceImpl implements ParkingService {

    @ConfigProperty(name = "parking.lot.size")
    Integer parkingSize;

    CarDto[] parking;

    @Startup
    public void startup(@Observes StartupEvent event) {
        parking = new CarDto[parkingSize];
    }

    @Override
    public Uni<CarDto> park(Car car) {
        return validateParkCar(car).chain(() -> {
            int vacantSpot = getFirstVacantSpot();
            if(vacantSpot < 0) {
                return Uni.createFrom().failure(new ParkingCarException("Parking is full", "Parking Full", Error.ErrorCode.PARKING_FULL));
            }

            parking[vacantSpot] = new CarDto()
                    .carId(car.id)
                    .slotId(vacantSpot);

            return Uni.createFrom().item(parking[vacantSpot]);
        });
    }

    @Override
    public Uni<Void> unpark(Integer slotId) {
        return validateUnparkCar(slotId).chain(() -> {
            invalidateCarIdCache(parking[slotId].carId);
            invalidateSlotIdCache(slotId);
            parking[slotId] = null;
            return Uni.createFrom().voidItem();
        });
    }

    @Override
    public Uni<CarDto> getInfo(String carId, Integer slotId) {
        return validateGetInfo(carId, slotId).chain(() -> {
            if(Objects.nonNull(carId)) {
                return Uni.createFrom().item(getParkedCar(carId).orElse(null));
            }

            return Uni.createFrom().item(getParkedCar(slotId).orElse(null));
        });
    }

    private Uni<Void> validateParkCar(Car car) {
        if (!isThereVacancy()) {
            return Uni.createFrom().failure(new ParkingCarException("Parking is full", "Parking Full", Error.ErrorCode.PARKING_FULL));
        } else if (fetchParkedCar(car.id).isPresent()) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Car %s is already parked", car.id), "Already Existing Car", Error.ErrorCode.CAR_ALREADY_PARKED));
        }

       return Uni.createFrom().voidItem();
    }

    private Uni<Void> validateUnparkCar(Integer slotId) {
        if (isSlotIdValid(slotId)) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Invalid Parking space %d", slotId), "Invalid Parking Space", Error.ErrorCode.INVALID_PARKING_SPACE));
        } else if (!isSlotIdOccupied(slotId)) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Parking space %d is empty", slotId), "Parking Space Empty", Error.ErrorCode.PARKING_SPACE_EMPTY));
        }

        return Uni.createFrom().voidItem();
    }

    private Uni<Void> validateGetInfo(String carId, Integer slotId) {
        if(Objects.isNull(carId) && Objects.isNull(slotId)) {
            return Uni.createFrom().failure(new ParkingCarException("Slot id or car id is required", "Empty Query Parameters", Error.ErrorCode.SLOT_ID_OR_CAR_ID_REQUIRED));
        } else if (Objects.nonNull(slotId) && isSlotIdValid(slotId)) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Invalid Parking space %d", slotId), "Invalid Parking Space", Error.ErrorCode.INVALID_PARKING_SPACE));
        } else if (Objects.nonNull(slotId) && !isSlotIdOccupied(slotId)) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Parking space %d is empty", slotId), "Parking Space Empty", Error.ErrorCode.PARKING_SPACE_EMPTY));
        } else if(Objects.nonNull(carId) && getParkedCar(carId).isEmpty()) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Car %s is not parked", carId), "Parking Space Empty", Response.Status.NOT_FOUND, Error.ErrorCode.CAR_NOT_FOUND));
        } else if(Objects.nonNull(carId) && Objects.nonNull(slotId) && !Objects.equals(parking[slotId], getParkedCar(carId).orElse(null))) {
            return Uni.createFrom().failure(new ParkingCarException(String.format("Information for Car %s on spot %d does not exist", carId, slotId), "Invalid Selection", Response.Status.NOT_FOUND, Error.ErrorCode.INVALID_SELECTION));
        }

        return Uni.createFrom().voidItem();
    }

    private boolean isSlotIdValid(Integer slotId) {
        return slotId < 0 || slotId >= parking.length;
    }

    private boolean isSlotIdOccupied(Integer slotId) {
        return Objects.nonNull(parking[slotId]);
    }

    @CacheResult(cacheName = "car-id")
    protected Optional<CarDto> getParkedCar(String carId) {
        return fetchParkedCar(carId);
    }

    @CacheInvalidate(cacheName = "car-id")
    protected void invalidateCarIdCache(String carId) {

    }

    protected Optional<CarDto> fetchParkedCar(String carId) {
        return Arrays.stream(parking).filter(carDto -> {
           return Objects.nonNull(carDto) && Objects.equals(carDto.carId, carId);
        }).findFirst();
    }


    @CacheResult(cacheName = "slot-id")
    protected Optional<CarDto> getParkedCar(Integer slotId) {
        return Objects.nonNull(parking[slotId]) ? Optional.of(parking[slotId]) : Optional.empty();
    }

    @CacheInvalidate(cacheName = "slot-id")
    protected void invalidateSlotIdCache(Integer slotId) {

    }

    private boolean isThereVacancy() {
        return Arrays.stream(parking).anyMatch(Objects::isNull);
    }

    private int getFirstVacantSpot() {
        for(int i = 0; i < parking.length; i++) {
            if(Objects.isNull(parking[i])) {
                return i;
            }
        }

        return -1;
    }
}
