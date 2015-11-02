package models.avatar.behaviorDefenition;

import models.avatar.behaviorDefenition.viewHolders.AvatarHtmlView;
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

    public Html getHtml(int index) {
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
}
