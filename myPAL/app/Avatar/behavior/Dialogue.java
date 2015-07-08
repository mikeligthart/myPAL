package avatar.behavior;

import play.i18n.Messages;

import java.util.List;

/**
 * Created by ligthartmeu on 7-7-2015.
 */
public class Dialogue implements BehaviorItem {

    private String id;
    private Boolean isBuild;

    private List<Text> texts;
    private List<Gesture> gestures;

    private Text text;
    private Gesture gesture;

    public Dialogue(String id){
        this.id = id;
        isBuild = false;
    }

    @Override
    public void build(){
        if (!isBuild){
            int textCount =  Integer.parseInt(Messages.get("behaviorItem.dialogue." + id + ".count"));
            isBuild = true;
        }
    }

    @Override
    public void set() {

    }
}
