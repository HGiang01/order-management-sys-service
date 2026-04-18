package com.hoanggiang.order_service;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;

import com.hoanggiang.order_service.stubs.InventoryClientStub;

import io.restassured.RestAssured;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.3.0");

    @LocalServerPort
    private int port;

    static {
        mysql.start();
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void shouldSubmitOrder() {
        String submitOrderJson = """
                {
                    "skuCode": "iphone_13",
                    "quantity": 2,
                    "price": 20
                }
                """;

        InventoryClientStub.stubInventoryCall("iphone_13", 2);

        String responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("/api/v1/order")
                .then()
                .statusCode(201)
                .extract()
                .asString();

        assertEquals(responseBodyString, "Placed order successfully");
    }

}
