package models.avatar.behaviorDefenition.viewHolders;

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
public class AvatarHtmlViewYesNoDontknow implements AvatarHtmlView {

    private views.html.avatar.avatar_yes_no_dontknow view;

    @Override
    public Html renderHtml(int index) {
        return view.render(index);
    }

}
