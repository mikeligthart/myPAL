package models.avatar.behaviorDefinition.viewHolders;

import play.twirl.api.Html;
import views.html.avatar.avatar_share_picture;

/**
 * myPAL
 * Purpose: placeholder to render avatar_share_picture view
 * <p>
 * Developed for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 22-11-2015
 */
public class AvatarHtmlViewSharePicture implements AvatarHtmlView {

    private String pictureSource;

    @Override
    public Html renderHtml(int index) {
        return avatar_share_picture.render(index, pictureSource);
    }

    public String getPictureSource() {
        return pictureSource;
    }

    public void setPictureSource(String pictureSource) {
        this.pictureSource = pictureSource;
    }
}
