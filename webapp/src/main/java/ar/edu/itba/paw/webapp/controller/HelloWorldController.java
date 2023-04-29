package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Random;


@Controller
public class HelloWorldController {

    // Asegurate de usar org.slf4j.Logger! Ojo de que la clase pasada por parámetro sea la correcta OJO AL COPYPASTE
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldController.class);
    // Vos ahora podes loggear llamando LOGGER.<level>(mensaje). Por ejemplo:
    // LOGGER.warn("El servicio de usuarios tardó mucho tiempo en crear un nuevo usuario!");
    // Lo que está MAL hacer es esto:
    // LOGGER.info("Se creó el usuario con email " + user.getEmail());
    // Porque esto tiene que generar el string, y el logger talvez después bocha el mensaje por nivel! Solución:
    // LOGGER.info("Se creó el usuario con email {}", user.getEmail());
    // Y ahora el logger internamente concatena los strings. Se pueden especificar tantos {}s como quieras.
    // El tema ahora es que user.getEmail() se llama siempre! Qué pasa si esto es una operación cara? Podemos usar una
    // lambda y solo la va a evaluar si precisa construir el string. ESTO SOLO ESTÁ EN SLF4J 2.0, O SEA NO LO TENGO ACÁ
    // LOGGER.info("Se creó el usuario con email {}", () -> user.getEmail());
    // Para loggear errores, el último parámetro del método debe ser un tipo Throwable, y así se loggea solo con el
    // stack trace y eso:
    // LOGGER.error("A la merda se pudrió tudu 💀", new UserNotFoundException());


    private final UserService us;

    @Autowired
    public HelloWorldController(final UserService us) {
        this.us = us;
    }

    // Spring permite hacer inyección de otras formas, no solo pasando instancia al constructor.
    // Estas otras incluyen:

    // Inyectar directamente a un campo:
    // @Autowired
    // private UserService us;

    // Inyectar a travez de un método setter:
    // @Autowired
    // public void setUserService(UserService us) { this.us = us; }

    // El RequestMapping se puede configurar para solo funcionar si el request tiene tal header, o es tal método HTTP,
    // o qué produce, etc.
    // @RequestMapping(value = "/", headers = "...", consumes = "...", method = "...", produces = "...")

    // También es posible especificar varias ubicaciones:
    // @RequestMapping(value = {"/", "/index.html"})

    // Por default, cualquier pedido sin importar headers o método HTTP es aceptado por el mapping:
    @RequestMapping("/")
    public ModelAndView helloWorld() {
        // Esta es la forma en la que podemos conseguir el usuario actual de spring security:
        final PawAuthUserDetails userDetails = (PawAuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Como cada request es ejecutado por un thread, esto no necesita parámetros, si no que utiliza el ID del
        // thread actual para saber quién es el usuario.
        // Esto se guarda en el SecurityContext, que podemos obtener con getContext(), que por detras esto utiliza un
        // ThreadLocal<SecurityContext> para saber qué contexto tiene el thread actual.

        final Optional<User> user = us.findByEmail(userDetails.getUsername());
        if (user.isPresent()) {
            LOGGER.debug("Hello world page requested by user {}", user.get().getUserId());
            return new ModelAndView("redirect:/" + user.get().getUserId());
        }

        LOGGER.warn("Unknown user requested the hello world page");
        return new ModelAndView("helloworld/index");
    }

    // @RequestMapping(value = "/register", method = RequestMethod.GET)
    @RequestMapping(value = "/register", method = {RequestMethod.GET}) // ( Podes especificar varios métodos http)
    public ModelAndView registerForm(@ModelAttribute("registerForm") final UserForm userForm) {
        // En vez de hacer mav = new ModelAndView(...); y mav.addObject("form", userForm), puedo simplemente
        // agregar al parámetro userForm el @ModelAttribute() con el nombre de atributo a usar, y cuando retorne va a
        // automáticamente agregar al ModelAndView retornado ese objeto como atributo con nombre "form".
        // Si agrego también @Valid va a no solo popular el form, sino que validarlo. Esto no se usa acá en el GET,
        // porque es posible que nos pasen el form con errores para decirle al usuario "che este campo está mal" (y en
        // tal caso populamos el form con los mismos valores de antes), pero si se usa en el register POST.
        LOGGER.info("Register form requested by someone");
        return new ModelAndView("helloworld/register");
    }

    // Una forma de atender requests es tomando un HttpServletRequest, que nos da mucho control sobre el request y
    // cómo respondemos:
    /*@RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(HttpServletRequest req) {
        final User user = us.createUser(req.getParameter("email"), req.getParameter("password"));

        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("user", user);

        return mav;
    }*/

    // Pero en general pedimos parámetros fijos y respondemos algo claro, entonces podemos hacer las cosas más simples
    // usando una sintaxis que nos deja especificar los parámetros del http request como parámetros del método.
    // Acá igual lo hacemos de una forma más simple y potente, usamos un form:
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("registerForm") final UserForm userForm, final BindingResult errors) {
        if (errors.hasErrors()) {
            LOGGER.warn("Register form POST failed with {} errors", errors.getAllErrors().size());
            return registerForm(userForm);
        }

        final User user = us.create(userForm.getEmail(), userForm.getPassword());
        LOGGER.warn("Registered new user: {}, {}", user.getEmail(), user.getPassword());

        // Podemos retornar un view y mostrarlo, pero esto va a ser en el body retornado por el POST /register y por
        // ende si apretas F5 el browser te tira un mensaje de "estas seguro que queres reenviar el formulario?"
        // final ModelAndView mav = new ModelAndView("helloworld/index");
        // mav.addObject("user", user);
        // return mav;

        // Entonces una mejor idea es redirigir a la vista de ver useruario por id:
        return new ModelAndView("redirect:/" + user.getUserId());
    }

    // @RequestMapping("/{id}") // El problema con este es que no pone restricciones al valor de "id"!
    @RequestMapping("/{id:\\d+}") // Antes aceptaba negativos, ahora no!
    // NOTAR: Si pones negativo o texto antes te tiraba 400 bad request, ahora te tira 404 not found.
    public ModelAndView profile(@PathVariable("id") final long userId) {
        final ModelAndView mav = new ModelAndView("helloworld/profile");
        final User user = us.findById(userId).orElseThrow(UserNotFoundException::new);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginForm() {
        return new ModelAndView("helloworld/login");
    }

    // @ModelAttribute can also be given to a method. This makes it so for every ModelAndView in this controller, the
    // given object will be added as param.
    // @ModelAttribute // By default, the param will use the function's name, so addObject("randomNumber", number).
    @ModelAttribute("randnum") // However we can also specify the param name manually like this.
    public int randomNumber() {
        return new Random().nextInt();
    }
    // A typical use for this is for an object with the currently logged user's information.
}
