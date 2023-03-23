package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;


@Controller
public class HelloWorldController {

    // This field and the following constructor are an example on how to use the dependency injection engine.
    // The HelloWorldController tells Spring that it wants a ViewResolver, from a method called "viewResolver".
    // Spring gets that from the @Bean method in WebConfig.java and passes it to here.
    private final ViewResolver viewResolver;
    @Autowired
    public HelloWorldController(@Qualifier("viewResolver") final ViewResolver vr) {
        this.viewResolver = vr;
    }

    // El RequestMapping se puede configurar para solo funcionar si el request tiene tal header, o es tal método HTTP,
    // o qué produce, etc.
    // @RequestMapping(value = "/", headers = "...", consumes = "...", method = "...", produces = "...")

    // También es posible especificar varias ubicaciones:
    // @RequestMapping(value = {"/", "/index.html"})

    // Por default, cualquier pedido sin importar headers o método HTTP es aceptado por el mapping:
    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("helloworld/index");
        mav.addObject("username", "PAW");

        return mav;
    }

    @RequestMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("helloworld/register");
    }
}
