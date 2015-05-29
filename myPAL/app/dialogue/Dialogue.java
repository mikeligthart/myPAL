package dialogue;

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
        return "Hoi " + name + " leuk je weer te zien.";
    }
}
