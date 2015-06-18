package dialogue;

import play.i18n.Messages;

import java.util.Random;

/**
 * Created by ligthartmeu on 29-5-2015.
 */
public class Dialogue {

    private static Dialogue instance;

    private Dialogue(){}

    public static Dialogue getInstance(){
        if (instance == null)
            instance = new Dialogue();
        return instance;
    }

    public String getGreeting(String name){
        int greetingPart1Count =  Integer.parseInt(Messages.get("dialogue.greeting.part1.count"));
        int greetingPart2Count =  Integer.parseInt(Messages.get("dialogue.greeting.part2.count"));
        Random rand = new Random();

        return Messages.get("dialogue.greeting.part1." + Integer.toString(rand.nextInt(greetingPart1Count)), name) + " "
                +  Messages.get("dialogue.greeting.part2." + Integer.toString(rand.nextInt(greetingPart2Count)));
    }
}
