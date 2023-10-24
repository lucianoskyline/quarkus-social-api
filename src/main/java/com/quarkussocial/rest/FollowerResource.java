package com.quarkussocial.rest;

import com.quarkussocial.domain.model.Follower;
import com.quarkussocial.domain.repository.FollowerRepository;
import com.quarkussocial.domain.repository.UserRepository;
import com.quarkussocial.rest.dto.FollowerRequest;
import com.quarkussocial.rest.dto.FollowerResponse;
import com.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    @Inject
    private FollowerRepository followerRepository;

    @Inject
    private UserRepository userRepository;

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (user.getId() == request.getFollowerId()) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow your self").build();
        }

        var follower = userRepository.findById(request.getFollowerId());
        if (follower == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (followerRepository.followers(follower, user)) {
            Follower f = new Follower();
            f.setFollower(follower);
            f.setUser(user);
            followerRepository.persist(f);
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var followers = followerRepository.findByUserId(userId);
        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setFollowersCount(followers.size());
        response.setContent(followers.stream().map(FollowerResponse::new).collect(Collectors.toList()));
        return Response.ok().entity(response).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        var user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerANdUser(followerId, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
