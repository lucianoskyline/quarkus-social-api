package com.quarkussocial.rest;

import com.quarkussocial.domain.model.Follower;
import com.quarkussocial.domain.model.User;
import com.quarkussocial.domain.repository.FollowerRepository;
import com.quarkussocial.domain.repository.UserRepository;
import com.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowerRepository followerRepository;

    private Long userId;

    private Long followerId;


    @BeforeEach
    @Transactional
    public void setup() {
        var user = new User();
        user.setAge(30);
        user.setName("Usuario");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(30);
        follower.setName("Seguidor");
        userRepository.persist(follower);
        followerId = follower.getId();

        Follower f = new Follower();
        f.setFollower(follower);
        f.setUser(user);
        followerRepository.persist(f);
    }

    @Test
    @DisplayName("Should return 409 when follower is equal to User Id")
    @Order(1)
    public void sameUserIsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(MediaType.APPLICATION_JSON).body(body).pathParam("userId", userId).when().put().then()
                .statusCode(Response.Status.CONFLICT.getStatusCode()).body(Matchers.is("You can't follow your self"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when user id doens't exist")
    @Order(2)
    public void userNotFoundWhenTryingToFollowTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(MediaType.APPLICATION_JSON).body(body).pathParam("userId", 999).when().put().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    @Order(3)
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given().contentType(MediaType.APPLICATION_JSON).body(body).pathParam("userId", userId).when().put().then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and user id doens't exist")
    @Order(4)
    public void userNotFoundWhenListingFollowersTest() {
        given().contentType(MediaType.APPLICATION_JSON).pathParam("userId", 999).when().get().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    @Order(5)
    public void listingFollowersTest() {
        var response = given().contentType(MediaType.APPLICATION_JSON).pathParam("userId", userId).when().get().then()
                .extract().response();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1, response.jsonPath().getInt("followers_count"));
        assertEquals(1, response.jsonPath().getList("content").size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow a user when user id doens't exist")
    @Order(6)
    public void userNotFoundWhenUnfollowingUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(MediaType.APPLICATION_JSON).queryParam("followerId", followerId)
                .body(body).pathParam("userId", 999).when().delete().then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should unfollow a user")
    @Order(7)
    public void unfollowUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given().contentType(MediaType.APPLICATION_JSON).queryParam("followerId", followerId)
                .body(body).pathParam("userId", userId).when().delete().then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}