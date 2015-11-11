package models.avatar.behaviorDefinition;

import models.avatar.behaviorDefinition.viewHolders.*;
import play.twirl.api.Html;

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
 * @version 1.0 31-10-2015
 */
public class AvatarHtml {

    private AvatarHtmlView view;
    private boolean isActiveHtml;

    public AvatarHtml(AvatarHtmlView view){
        this.view = view;
        if(view != null){
            isActiveHtml = true;
        } else {
            isActiveHtml = false;
        }
    }

    public Html render(int index) {
        if(view == null){
            return null;
        } else {
            return view.renderHtml(index);
        }
    }

    public AvatarHtmlView getView() {
        return view;
    }

    public void setView(AvatarHtmlView view) {
        this.view = view;
    }

    public boolean isActiveHtml(){
        return isActiveHtml;
    }

    public static AvatarHtml getAvatarHtml(AvatarHtmlType type){
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
