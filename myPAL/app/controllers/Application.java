package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(greeting.render("Jelte"));
    }

    public static Result showBootstrap() { return ok(bootstrap.render("Hello World!"));}

}
