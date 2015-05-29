package controllers;

import dialogue.Dialogue;
import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    private static Dialogue dialogue = Dialogue.getInstance();

    public static Result index() {
        return ok(greeting.render(dialogue.getGreeting("Jelte")));
    }
    public static Result showBootstrap() { return ok(bootstrap.render("Hello World!"));}
}
