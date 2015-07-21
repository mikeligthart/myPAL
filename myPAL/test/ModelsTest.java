/**
 * Created by ligthartmeu on 1-6-2015.
 */

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {

    @Test
    public void createAndRetrieveUser() {
        new User("bob@gmail.com", "Bob", "Test", "secret", UserType.CHILD).save();
        User bob = User.find.where().eq("email", "bob@gmail.com").findUnique();
        assertNotNull(bob);
        assertEquals("Bob", bob.getFirstName());
    }
}
