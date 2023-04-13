package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


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
    @RequestMapping(value = "/register", method = { RequestMethod.GET }) // ( Podes especificar varios métodos http)
    public ModelAndView registerForm(@ModelAttribute("form") final UserForm userForm) {
        // En vez de hacer mav = new ModelAndView(...); y mav.addObject("form", userForm), puedo simplemente
        // agregar al parámetro userForm el @ModelAttribute() con el nombre de atributo a usar, y cuando retorne va a
        // automáticamente agregar al ModelAndView retornado ese objeto como atributo con nombre "form".
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
    public ModelAndView register(@Valid @ModelAttribute("form") final UserForm userForm, final BindingResult errors) {
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
}
