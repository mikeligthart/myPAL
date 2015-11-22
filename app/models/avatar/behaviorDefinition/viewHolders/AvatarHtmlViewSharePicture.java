package models.avatar.behaviorDefinition.viewHolders;

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
 * @version 1.0 22-11-2015
 */
public class AvatarHtmlViewSharePicture implements AvatarHtmlView {

    private views.html.avatar.avatar_share_picture view;
    private String pictureSource;

    @Override
    public Html renderHtml(int index) {
        return view.render(index, pictureSource);
    }

    public String getPictureSource() {
        return pictureSource;
    }

    public void setPictureSource(String pictureSource) {
        this.pictureSource = pictureSource;
    }
}
