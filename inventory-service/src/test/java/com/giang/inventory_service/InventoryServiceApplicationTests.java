package com.giang.inventory_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

//@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");

    static {
        mySQLContainer.start();
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }


    @Test
    void shouldReadInventory() {
        boolean response1 = RestAssured.given()
                                       .when()
                                       .get("/api/v1/inventory?skuCode=iphone_13&quantity=100")
                                       .then()
                                       .statusCode(200)
                                       .extract()
                                       .response()
                                       .as(Boolean.class);
        Assertions.assertTrue(response1);

        boolean response2 = RestAssured.given()
                                      .when()
                                      .get("/api/v1/inventory?skuCode=iphone_13&quantity=101")
                                      .then()
                                      .statusCode(200)
                                      .extract()
                                      .response()
                                      .as(Boolean.class);
        Assertions.assertFalse(response2);
    }

}
