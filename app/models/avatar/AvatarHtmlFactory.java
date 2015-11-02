package models.avatar;

import models.UserMyPAL;
import models.avatar.viewHolders.AvatarHtmlViewText;
import models.avatar.viewHolders.AvatarHtmlViewTextfield;
import models.avatar.viewHolders.AvatarHtmlViewYesNo;
import models.avatar.viewHolders.AvatarHtmlViewYesNoDontknow;
import play.Logger;
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

    public AvatarHtml getAvatarHtml(AvatarHtmlType type){
        switch(type){
            case YES_NO:
                return new AvatarHtml(new AvatarHtmlViewYesNo());
            case YES_NO_DONTKNOW:
                return new AvatarHtml(new AvatarHtmlViewYesNoDontknow());
            case TEXT:
                return new AvatarHtml(new AvatarHtmlViewText());
            case TEXTFIELD:
                return new AvatarHtml(new AvatarHtmlViewTextfield());
            case NULL:
            default:
                return new AvatarHtml(null);
        }
    }
}
