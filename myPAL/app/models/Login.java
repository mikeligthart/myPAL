package models;

/**
 * Created by ligthartmeu on 17-6-2015.
 */
public class Login {
    public String email;
    public String password;

    public String validate() {
        if (!User.authenticate(email, password)) {
            return "Invalid user or password";
        }
        return null;
    }
}
