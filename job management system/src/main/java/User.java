import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    String nume;
    String prenume;
    String mail;

    /**
     * Constructor pentru obiectul User.
     *
     * @param nume     Numele utilizatorului.
     * @param prenume  Prenumele utilizatorului.
     * @param mail     Adresa de email a utilizatorului.
     */
    public User(String nume, String prenume, String mail) {
        this.nume = nume;
        this.prenume = prenume;
        this.mail = mail;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String toString() {
        return this.nume + ";" + this.prenume + ";" + this.mail;
    }

    /**
     * Verifica dacă o adresa de email este valida conform unui format simplu.
     *
     * @param mail Adresa de email de verificat.
     * @return true dacă adresa de email este validă, false în caz contrar.
     */
    public static boolean isValidEmail(String mail){
        String regex = "^(.+)@(.+)$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mail);

        return matcher.matches();
    }

}
