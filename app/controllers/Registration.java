package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.registration.*;
/**
 * myPAL
 * Purpose: [ENTER PURPOSE]
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 19-10-2015
 */
public class Registration extends Controller {

    public static Result registrationPage(){

        return ok(views.html.registration.registration.render());
    }
}
