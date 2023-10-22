package com.quarkussocial.rest;

import com.quarkussocial.domain.modal.User;
import com.quarkussocial.rest.dto.CreateUserRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @POST
    @Transactional
    public Response createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setAge(createUserRequest.getAge());
        user.persist();

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        var users = User.findAll();
        return Response.ok(users.list()).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = User.findById(id);
        if (user != null) {
            user.delete();
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();

    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user=User.findById(id);
        if(user!=null){
            user.setAge(userData.getAge());
            user.setName(user.getName());

            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
