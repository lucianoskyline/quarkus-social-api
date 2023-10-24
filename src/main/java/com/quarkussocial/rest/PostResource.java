package com.quarkussocial.rest;

import com.quarkussocial.domain.model.Post;
import com.quarkussocial.domain.model.User;
import com.quarkussocial.domain.repository.PostRepository;
import com.quarkussocial.domain.repository.UserRepository;
import com.quarkussocial.rest.dto.CreatePostRequest;
import com.quarkussocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    private UserRepository userRepository;

    @Inject
    private PostRepository postRepository;


    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setPostText(request.getText());
        post.setUser(user);
        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var postsQuery = postRepository.find("user",
                Sort.by("datetime", Sort.Direction.Descending), user);
        var posts = postsQuery.list().stream().map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(posts).build();
    }

}
