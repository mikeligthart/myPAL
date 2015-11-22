package models.avatar.behaviorDefinition;

import controllers.Assets;
import controllers.routes;
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

    private static String AVATARPICTURESOURCE = routes.Assets.at("avatar/pictures/").url();
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
        AvatarHtmlViewSharePicture view;
        switch(type){
            case YES_NO:
                return new AvatarHtml(new AvatarHtmlViewYesNo());
            case YES_NO_DONTKNOW:
                return new AvatarHtml(new AvatarHtmlViewYesNoDontknow());
            case TEXT:
                return new AvatarHtml(new AvatarHtmlViewText());
            case TEXTFIELD:
                return new AvatarHtml(new AvatarHtmlViewTextfield());
            case PICTURE1:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "1.jpg");
                return new AvatarHtml(view);
            case PICTURE2:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "2.jpg");
                return new AvatarHtml(view);
            case PICTURE3:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "3.jpg");
                return new AvatarHtml(view);
            case PICTURE4:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "4.jpg");
                return new AvatarHtml(view);
            case PICTURE5:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "5.jpg");
                return new AvatarHtml(view);
            case PICTURE6:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "6.jpg");
                return new AvatarHtml(view);
            case PICTURE7:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "7.jpg");
                return new AvatarHtml(view);
            case PICTURE8:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "8.jpg");
                return new AvatarHtml(view);
            case PICTURE9:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "9.jpg");
                return new AvatarHtml(view);
            case PICTURE10:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "10.jpg");
                return new AvatarHtml(view);
            case PICTURE11:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "11.jpg");
                return new AvatarHtml(view);
            case PICTURE12:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "12.jpg");
                return new AvatarHtml(view);
            case PICTURE13:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "13.jpg");
                return new AvatarHtml(view);
            case PICTURE14:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "14.jpg");
                return new AvatarHtml(view);
            case PICTURE15:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "15.jpg");
                return new AvatarHtml(view);
            case PICTURE16:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "16.jpg");
                return new AvatarHtml(view);
            case PICTURE17:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "17.jpg");
                return new AvatarHtml(view);
            case PICTURE18:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "18.jpg");
                return new AvatarHtml(view);
            case PICTURE19:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "19.jpg");
                return new AvatarHtml(view);
            case PICTURE20:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "20.jpg");
                return new AvatarHtml(view);
            case PICTURE21:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "21.jpg");
                return new AvatarHtml(view);
            case PICTURE22:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "22.jpg");
                return new AvatarHtml(view);
            case PICTURE23:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "23.jpg");
                return new AvatarHtml(view);
            case PICTURE24:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "24.jpg");
                return new AvatarHtml(view);
            case PICTURE25:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "25.jpg");
                return new AvatarHtml(view);
            case PICTURE26:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "26.jpg");
                return new AvatarHtml(view);
            case PICTURE27:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "27.jpg");
                return new AvatarHtml(view);
            case PICTURE28:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "28.jpg");
                return new AvatarHtml(view);
            case PICTURE29:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "29.jpg");
                return new AvatarHtml(view);
            case PICTURE30:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "30.jpg");
                return new AvatarHtml(view);
            case PICTURE31:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "31.jpg");
                return new AvatarHtml(view);
            case PICTURE32:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "32.jpg");
                return new AvatarHtml(view);
            case PICTURE33:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "33.jpg");
                return new AvatarHtml(view);
            case PICTURE34:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "34.jpg");
                return new AvatarHtml(view);
            case PICTURE35:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "35.jpg");
                return new AvatarHtml(view);
            case PICTURE36:
                view = new AvatarHtmlViewSharePicture();
                view.setPictureSource(AVATARPICTURESOURCE + "36.jpg");
                return new AvatarHtml(view);
            case NULL:
            default:
                return new AvatarHtml(null);
        }
    }

    public static String getAvatarHtmlImage(AvatarHtmlType type){
        if(type.name().contains("PICTURE")){
            return AVATARPICTURESOURCE + type.name().replace("PICTURE", "") + ".jpg";
        }
        return routes.Assets.at("images/" + type.name().toLowerCase() + ".png").url();
    }
}
