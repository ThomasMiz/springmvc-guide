package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Random;


@Controller
public class HelloWorldController {

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

    // By default, unhandled exceptions will produce an HTTP 500 Internal Server Error. However, if the exception is,
    // for example, that you tried to access a non-existing user's profile, it'd be better if we returned an HTTP 404
    // Not Found, and show a custom page. This is how that's done:
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView noSuchUser() {
        return new ModelAndView("404");
    }
    // It is definitely a good idea to move these handlers to a separate controller, an ExceptionController.

    // El RequestMapping se puede configurar para solo funcionar si el request tiene tal header, o es tal método HTTP,
    // o qué produce, etc.
    // @RequestMapping(value = "/", headers = "...", consumes = "...", method = "...", produces = "...")

    // También es posible especificar varias ubicaciones:
    // @RequestMapping(value = {"/", "/index.html"})

    // Por default, cualquier pedido sin importar headers o método HTTP es aceptado por el mapping:
    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("helloworld/index");
        return mav;
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
            return registerForm(userForm);
        }

        final User user = us.create(userForm.getEmail(), userForm.getPassword());

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

    // @ModelAttribute can also be given to a method. This makes it so for every ModelAndView in this controller, the
    // given object will be added as param.
    // @ModelAttribute // By default, the param will use the function's name, so addObject("randomNumber", number).
    @ModelAttribute("randnum") // However we can also specify the param name manually like this.
    public int randomNumber() {
        return new Random().nextInt();
    }
    // A typical use for this is for an object with the currently logged user's information.
}
