package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.test.bootstrap;
import views.html.test.dataTest;
import views.html.test.google_visualization;
import views.html.test.affect_button;

/**
 * myPAL
 * Purpose: handles all the controller functions for the dummy actions and pages
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
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
