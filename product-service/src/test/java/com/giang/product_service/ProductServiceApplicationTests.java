package com.giang.product_service;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

// Khởi tạo một Spring Boot Context và tiêm DI phục vụ bài các bài Tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
    // Tự động cấu hình kết nối đến địa chỉ MongoDB container, thay vì sử dụng
    // application.properties như bình thường
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    // Đặt là static vì phải được khởi tạo trước các phương thức kiểm thử được thực
    // thi
    static {
        mongoDBContainer.start();
    }

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Kiểm thử endpoints")
    void shouldCreateProduct() throws Exception {
        String requestBody = """
                    {
                    "name": "MacBook Air 2017",
                    "description": "Apple",
                    "price": 20
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/product")
                .then()
                .log()
                .all()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("MacBook Air 2017"))
                .body("description", Matchers.equalTo("Apple"));

        // .body("price", Matchers.is(productRequest.price().intValueExact()))
    }

}
