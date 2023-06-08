package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Vamos a hacer un controller para endpoints del API que tengan que ver con usuarios.

@Path("users") // Maneja endpoints con paths /users/**
@Component // No usamos @Controller! Usamos @Component.
public class UserController {

    private final UserService us;

    // Este objeto nos va a dejar construir URIs. Lo anotamos con con @Autowired, pero con @Context. Nos lo va a
    // inyectar no Spring, pero Jersey, y contiene información específica del request actual que se está procesando.
    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(final UserService us) {
        this.us = us;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers(@QueryParam("page") @DefaultValue("1") final int page) {
        if (page < 1)
            return Response.status(Response.Status.BAD_REQUEST).build();

        final List<User> userList = us.getAll(page, 20);

        // Si la lista está vacía, retornamos un HTTP 204 No Content
        if (userList.isEmpty())
            return Response.noContent().build();

        // Sino, HTTP 200 OK con la lista de usuarios, y agregamos headers de paginación
        /*return Response.ok(new GenericEntity<List<User>>(userList) {})
                .link("ya vamos", "next")
                .link("a rellenar", "prev")
                .link("estos campos", "first")
                .link("con uris", "last")
                .build();*/

        // Tenemos que wrappear el userList en una subclase anónima de GenericEntity para que lo pueda reconocer bien
        // y nos arme un json array. El tema es que por default esto solo trae los campos de User que tienen getter y
        // setter, o sea que el único campo que trae es la contraseña. Tenemos que cambiar esto!
        // Una buena práctica es separar la entidad de dominio (User) con la representación que la API expone. Entonces
        // creamos un paquete nuevo en webapp, llamado dto, o "Data Transfer Object". Creamos una clase UserDto con la
        // representación de cómo se ve un usuario en la API, le agregamos métodos static para crearlos desde User/s,
        // y entonces en vez de usar User para la lista de respueta, usamos UserDto:

        List<UserDto> userDtoList = UserDto.fromUserList(uriInfo, userList);
        return Response.ok(new GenericEntity<List<UserDto>>(userDtoList) {})
                .link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page + 1).build(), "next")
                .link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page - 1).build(), "prev")
                // TODO: Agregar verificación en los next y prev, si no existe no incluirlo!
                // Tip: Mover la adición de estos headers a una helper function
                // No tenemos como implementar el first y last, habría que buscar el total count de usuarios, pero
                // es buena práctica incluirlos.
                //.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page + 1).build(), "first")
                //.link(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page + 1).build(), "last")
                .build();
    }

    // Con @Produces podemos especificar el tipo de retorno del endpoint, y con @Consumes podemos especificar qué pide.
    // Si nuestro api tiene varias versiones, está MAL hacer api.mysite.com/v1/users y api.mysite.com/v2/users! La
    // forma en la que distinguis versiones es en el endpoint un @Consumes("application/vnd.createuserform.v1+json").
    // @Produces define el header Content-Type en la respuesta.

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") final long id) {
        final User user = us.findById(id).orElseThrow(UserNotFoundException::new);

        // OJO: Acá también usamos el UserDto y no el User en la respuesta!!
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid final UserForm userForm) {
        // NOTA: No tengo que hacer la verificación de errores del form! Con el @Valid, si el form no es válido, el
        // error se maneja desde otro lado.

        final User user = us.create(userForm.getEmail(), userForm.getPassword());
        return Response.created(uriInfo.getBaseUriBuilder().path("/users").path(String.valueOf(user.getUserId())).build()).build();
    }
}
