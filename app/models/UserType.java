package models;

import play.i18n.Messages;

/**
 * myPAL
 * Purpose: enum that contains the different types a Usermypal can have
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
 */
public enum UserType {

    CHILD, PARENT, PROFESSIONAL, ADMIN;

    public String toString(){

        switch(this){
            case PARENT: return Messages.get("model.user.userType.PARENT");
            case PROFESSIONAL: return Messages.get("model.user.userType.PROFESSIONAL");
            case ADMIN: return Messages.get("model.user.userType.ADMIN");
            case CHILD:
            default: return Messages.get("model.user.userType.CHILD");
        }
    }
}
