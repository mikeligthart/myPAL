package models;

import play.db.ebean.Model;
import play.i18n.Messages;

import javax.persistence.Entity;

/**
 * Created by Mike on 21-7-2015.
 */
public enum UserType {

    CHILD, PARENT, PROFESSIONAL, ADMIN;

    public String toString(){

        switch(this){
            case PARENT: return Messages.get("model.user.userType.parent");
            case PROFESSIONAL: return Messages.get("model.user.userType.professional");
            case ADMIN: return Messages.get("model.user.userType.admin");
            case CHILD:
            default: return Messages.get("model.user.userType.child");
        }
    }

}