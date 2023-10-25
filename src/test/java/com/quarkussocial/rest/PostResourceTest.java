package com.quarkussocial.rest;

import com.quarkussocial.domain.model.Follower;
import com.quarkussocial.domain.model.Post;
import com.quarkussocial.domain.model.User;
import com.quarkussocial.domain.repository.FollowerRepository;
import com.quarkussocial.domain.repository.PostRepository;
import com.quarkussocial.domain.repository.UserRepository;
import com.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    private UserRepository userRepository;

    private Long userId;

    private Long userNotfollowerId;

    private Long userFollowerId;

    @Inject
    private FollowerRepository followerRepository;

    @Inject
    private PostRepository postRepository;


    @BeforeEach
    @Transactional
    public void setup() {
        var user = new User();
        user.setAge(30);
        user.setName("Usuario");
        userRepository.persist(user);
        userId = user.getId();

        var userNotfollower = new User();
        userNotfollower.setAge(30);
        userNotfollower.setName("Não seguidor");
        userRepository.persist(userNotfollower);
        userNotfollowerId = userNotfollower.getId();

        var userFollower = new User();
        userFollower.setAge(30);
        userFollower.setName("Seguidor");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

        Post post=new Post();
        post.setDatetime(LocalDateTime.now());
        post.setPostText("Texto da postagem");
        post.setUser(user);
        postRepository.persist(post);
    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given().contentType(MediaType.APPLICATION_JSON).body(postRequest)
                .pathParams("userId", userId)
                .when().post().then().statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given().contentType(MediaType.APPLICATION_JSON).body(postRequest)
                .pathParams("userId", 999)
                .when().post().then().statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    public void listPostForInexistentUserTest() {
        var inexistentUserId = 999;
        given().pathParams("userId", inexistentUserId)
                .when().get().then().statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest() {
        given().pathParams("userId", userId)
                .when().get().then().statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doen't exist")
    public void listPostFollowerNotFoundTest() {
        given().pathParams("userId", userId).header("followerId", 999)
                .when().get().then().statusCode(404);
    }

    @Test
    @DisplayName("Should return 403 when followerId isn't a follower")
    public void listPostNotAFollowerTest() {
        given().pathParams("userId", userId).header("followerId", userNotfollowerId)
                .when().get().then().statusCode(403);
    }

    @Test
    @DisplayName("Should return posts")
    public void listPostsTest() {
        given().pathParams("userId", userId).header("followerId", userFollowerId)
                .when().get().then().statusCode(200).body("size()", Matchers.is(1));
    }

}