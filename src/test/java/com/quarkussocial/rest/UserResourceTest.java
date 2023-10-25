package com.quarkussocial.rest;

import com.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    public URL apiUrl;

    @Test
    @DisplayName("Should create an user successfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Luciano");
        user.setAge(30);
        var response = given().contentType(MediaType.APPLICATION_JSON)
                .body(user).when().post(apiUrl).then()
                .extract().response();
        assertEquals(201, response.getStatusCode());
        assertNotNull(response.jsonPath().getInt("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given().contentType(MediaType.APPLICATION_JSON)
                .body(user).when().post(apiUrl).then()
                .extract().response();
        assertEquals(400, response.getStatusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given().contentType(MediaType.APPLICATION_JSON).
                when().get(apiUrl).then().statusCode(200).body("size()", Matchers.is(1));
    }

}