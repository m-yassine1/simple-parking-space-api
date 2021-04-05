package com.parking.cars.service;

import com.parking.cars.model.Car;
import com.parking.cars.model.CarDto;
import io.smallrye.mutiny.Uni;

public interface ParkingService {
    Uni<CarDto> park(Car car);
    Uni<Void> unpark(Integer slotId);
    Uni<CarDto> getInfo(String carId, Integer slotId);
}
