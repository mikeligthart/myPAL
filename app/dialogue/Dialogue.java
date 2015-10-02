package dialogue;

import play.i18n.Messages;

import java.util.Random;

/**
 * myPAL
 * Purpose: loads and parses dialogue items from file for the avatar
 *
 * Developped for TNO.
 * Kampweg 5
 * 3769 DE Soesterberg
 * General telephone number: +31(0)88 866 15 00
 *
 * @author Mike Ligthart - mike.ligthart@gmail.com
 * @version 1.0 21-8-2015
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
