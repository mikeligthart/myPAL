package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.test.bootstrap;
import views.html.test.dataTest;
import views.html.test.google_visualization;
import views.html.test.affect_button;

/**
 * Created by Mike on 29-7-2015.
 */
public class Test extends Controller {

    public static Result dataTest(){
        return ok(dataTest.render());
    }

    public static Result showBootstrap() {
        return ok(bootstrap.render("Hello World!"));
    }

    public static Result googleTest() {
        return ok(google_visualization.render());
    }

    public static Result affectButton() {
        return ok(affect_button.render());
    }

}
