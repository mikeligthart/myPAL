package controllers;

import models.UserMyPAL;
import models.UserType;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import util.AppException;
import util.RegistrationToDisk;
import views.html.registration.*;

import static play.data.Form.form;

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

    public static Result register(){
        //Retrieve data from registrationForm
        DynamicForm requestData = form().bindFromRequest();
        String firstName = requestData.get("firstName");
        int age = Integer.valueOf(requestData.get("age"));
        String opinion1a = requestData.get("opinion1-0");
        String opinion1b = requestData.get("opinion1-1");
        String opinion2 = requestData.get("opinion2");
        String emailName = firstName +  UserMyPAL.find.all().size();

        //Save data to file
        RegistrationToDisk.writeToDisk(emailName, opinion1a, opinion1b, opinion2);

        //Create new user with this data
        UserMyPAL user = new UserMyPAL();
        user.setEmail(emailName + "@pal4u.eu");
        user.setFirstName(firstName);
        user.setLastName("Kamp");
        user.setBirthdate("20/10/" + (2015 - age));
        try {
            user.setPassword("secret");
        } catch (AppException e) {
            Logger.error("[Registration > register] AppException " + e.getMessage());
        }
        user.setUserType(UserType.CHILD);
        user.save();

        //Add user to session
        session().clear();
        session("email", user.getEmail());

        return redirect(routes.Diary.diary());
    }
}
