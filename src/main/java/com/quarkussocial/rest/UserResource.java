package com.quarkussocial.rest;

import com.quarkussocial.domain.model.User;
import com.quarkussocial.domain.repository.UserRepository;
import com.quarkussocial.rest.dto.CreateUserRequest;
import com.quarkussocial.rest.dto.ResponseError;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserRepository userRepository;

    @Inject
    private Validator validator;


    @POST
    @Transactional
    public Response createUser(CreateUserRequest createUserRequest) {
        var validations = validator.validate(createUserRequest);
        if (!validations.isEmpty()) {
            ResponseError responseError = ResponseError.createFromValidation(validations);
            return Response.status(Response.Status.BAD_REQUEST).entity(responseError).build();
        }

        User user = new User();
        user.setName(createUserRequest.getName());
        user.setAge(createUserRequest.getAge());
        userRepository.persist(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    public Response listAllUsers() {
        var users = userRepository.findAll();
        return Response.ok(users.list()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.delete(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.setAge(userData.getAge());
            user.setName(userData.getName());
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
