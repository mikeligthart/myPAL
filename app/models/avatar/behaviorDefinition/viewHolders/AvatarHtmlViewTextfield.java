package models.avatar.behaviorDefinition.viewHolders;

import play.twirl.api.Html;
import views.html.avatar.avatar_textfield;

/**
 * myPAL
 * Purpose: placeholder to render avatar_textfield view
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 31-10-2015
 */
public class AvatarHtmlViewTextfield implements AvatarHtmlView {

    @Override
    public Html renderHtml(int index) {
        return avatar_textfield.render(index);
    }

}
