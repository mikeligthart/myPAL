package models.avatar;

import models.UserMyPAL;
import play.twirl.api.Html;
import views.html.avatar.*;

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
 * @version 1.0 30-10-2015
 */
public class AvatarHtmlFactory {

    private UserMyPAL user;

    public AvatarHtmlFactory(UserMyPAL user){
        this.user = user;
    }

    public Html getHtml(AvatarHtmlType type){
        switch(type){
            case YES_NO:
                return avatar_yes_no.render();
            case YES_NO_DONTKNOW:
                return avatar_yes_no_dontknow.render();
            case TEXT:
                return avatar_text.render();
            case TEXTFIELD:
                return avatar_textfield.render();
        }
        return null;
    }
}
