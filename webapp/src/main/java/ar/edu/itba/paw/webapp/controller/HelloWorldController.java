package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;


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
        User user = us.createUser("paw@itba.edu.ar", "mysecret");
        mav.addObject("user", user);

        return mav;
    }

    @RequestMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("helloworld/register");
    }

}
