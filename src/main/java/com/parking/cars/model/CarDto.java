package com.parking.cars.model;

import java.util.Objects;

public class CarDto {
    public String carId;
    public Integer slotId;

    public CarDto carId(String carId) {
        this.carId = carId;
        return this;
    }

    public CarDto slotId(Integer slotId) {
        this.slotId = slotId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDto carDto = (CarDto) o;
        return Objects.equals(carId, carDto.carId) && Objects.equals(slotId, carDto.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId, slotId);
    }
}
