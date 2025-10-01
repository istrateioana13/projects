import static org.junit.Assert.*;
import org.junit.Test;


public class UserTest {
    @Test
    public void testConstructor1(){
        String nume1 = "Ioana";
        String prenume1 = "Istrate";
        String mail1 = "ioana@gmail.com";
        User u1 = new User(nume1, prenume1, mail1);

        assertEquals(nume1, u1.getNume());
        assertEquals(prenume1, u1.getPrenume());
        assertEquals(mail1, u1.getMail());

        String nume2 = "Gina";
        String prenume2 = "Pistol";
        String mail2 = "gina@gmail.com";
        User u2 = new User(nume2, prenume2, mail2);

        assertEquals(nume2, u2.getNume());
        assertEquals(prenume2, u2.getPrenume());
        assertEquals(mail2, u2.getMail());

    }

    @Test
    public void testValidMail(){
        assertTrue(User.isValidEmail("test@example.com"));
        assertTrue(User.isValidEmail("user123@gmail.com"));
    }

}
