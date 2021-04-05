package com.parking.cars;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;

@QuarkusTest
public class ParkingCarTest {

    @ConfigProperty(name = "parking.lot.size")
    Integer parkingSize;

    @Test
    public void requestNotFound() {
        given()
          .when().get("/dummy")
          .then()
             .statusCode(404);
    }

    @Test
    public void invalidRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"a12A3\"}")
                .when().post("/api/v1/park")
                .then()
                .statusCode(400)
                .body(containsStringIgnoringCase("Invalid car id"));
    }

    @Test
    public void addCarRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park")
                .then()
                .statusCode(201)
                .body(containsStringIgnoringCase("A12A3"));
    }

    @Test
    public void carAlreadyParkedRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park");

        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park")
                .then()
                .statusCode(400)
                .body(containsStringIgnoringCase("A12A3"));
    }

    @Test
    public void carParkingFull() {
        for(int i = 0; i < parkingSize; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body(String.format("{\"car_number\": \"A%d\"}", i))
                    .when().post("/api/v1/park");
        }

        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park")
                .then()
                .statusCode(400)
                .body(containsStringIgnoringCase("Parking is full"));
    }

    @Test
    public void carNotFoundRequest() {
        given()
                .when().delete("/api/v1/unpark/1")
                .then()
                .statusCode(400)
                .body(containsStringIgnoringCase("Parking space 1 is empty"));
    }

    @Test
    public void invalidSlotIdRequest() {
        given()
                .when().delete(String.format("/api/v1/unpark/%d", parkingSize + 1))
                .then()
                .statusCode(400)
                .body(containsStringIgnoringCase("Invalid Parking space"));
    }

    @Test
    public void unparkedSuccessfulRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park");

        given()
                .when().delete("/api/v1/unpark/0")
                .then()
                .statusCode(204);
    }

    @Test
    public void carNotFoundInfoRequest() {
        given()
                .queryParam("car_number", "A123")
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(404);
    }

    @Test
    public void emptyQueryParametersRequest() {
        given()
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(400);
    }

    @Test
    public void carNotFoundFullQueryParameterRequest() {
        given()
                .queryParam("car_number", "A123")
                .queryParam("slot_number", 1)
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(404);
    }

    @Test
    public void getCarInformationViaSlotIdRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park");

        given()
                .queryParam("slot_number", 0)
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(200);
    }

    @Test
    public void getCarInformationViaCarNumberRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park");

        given()
                .queryParam("car_number", "A12A3")
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(200);
    }

    @Test
    public void getCarInformationViaCarNumberAndSlotNumberRequest() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"car_number\": \"A12A3\"}")
                .when().post("/api/v1/park");

        given()
                .queryParam("car_number", "A12A3")
                .queryParam("slot_number", 0)
                .when().get("/api/v1/get-info")
                .then()
                .statusCode(200);
    }
}