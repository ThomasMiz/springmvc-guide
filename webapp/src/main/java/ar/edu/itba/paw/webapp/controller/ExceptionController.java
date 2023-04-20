package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {
    // Las exceptions no manejadas producen un HTTP 500 Internal Server Error por default, pero podemos agregar
    // handlers para manejarlas y retornar algo distinto, entonces si por ejemplo intentas acceder al perfil de un
    // usuario que no existe, te tira HTTP 404. Esto se hace con @ExceptionHandler:
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ModelAndView noSuchUser() {
        return new ModelAndView("/errors/404");
    }
    // Es una buena idea poner estos en controller distinto para ser organizados, por ejemplo un ExceptionController.
    // Para que funcione esto igual vamos a precisar agregarle @ControllerAdvice en vez de @Controller, o
    // alternativamente hacer una clase base BaseController, poner los handlers ahí, y que todos tus controllers
    // extiendan de BaseController. Vamos a ver mejor cómo trabajar con esto más adelante.
    // Otra alternativa está en el web.xml, donde podemos agregar tags <error-page>.
}
