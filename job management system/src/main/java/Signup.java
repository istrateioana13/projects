import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Signup extends JFrame {

    JLabel etichetaMail;
    JLabel etichetaParola;
    JLabel etichetaCreare;
    JLabel etichetaNume;
    JLabel etichetaPrenume;
    JPasswordField fieldParola;
    JTextField fieldMail;
    JTextField fieldNume;
    JTextField fieldPrenume;
    JButton buton;

    /**
     * Constructor pentru clasa Singup.
     * Inițializează și configurează componentele ferestrei de înregistrare.
     */
    public Signup() {
        super("Sistem de gestionare a joburilor");

        this.setSize(430, 250);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        etichetaParola = new JLabel("Parola:");
        etichetaMail = new JLabel("Mail:");
        etichetaCreare = new JLabel("Creare cont");
        fieldParola = new JPasswordField(25);
        fieldMail = new JTextField(25);
        buton = new JButton("Creeaza");
        etichetaNume = new JLabel("Nume:");
        fieldNume = new JTextField(25);
        etichetaPrenume = new JLabel("Prenume:");
        fieldPrenume = new JTextField(25);

        etichetaCreare.setPreferredSize(new Dimension(400, 12));
        etichetaCreare.setHorizontalAlignment(SwingUtilities.CENTER);

        etichetaParola.setPreferredSize(new Dimension(60, 12));
        etichetaParola.setHorizontalAlignment(SwingUtilities.RIGHT);

        etichetaMail.setPreferredSize(new Dimension(60, 12));
        etichetaMail.setHorizontalAlignment(SwingUtilities.RIGHT);

        etichetaNume.setPreferredSize(new Dimension(60, 12));
        etichetaNume.setHorizontalAlignment(SwingUtilities.RIGHT);

        etichetaPrenume.setPreferredSize(new Dimension(60, 12));
        etichetaPrenume.setHorizontalAlignment(SwingUtilities.RIGHT);

        buton.setPreferredSize(new Dimension(100,50));

        JPanel panelButon = new JPanel();
        panelButon.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelButon.add(buton, gbc);

        this.add(etichetaCreare);
        this.add(etichetaNume);
        this.add(fieldNume);
        this.add(etichetaPrenume);
        this.add(fieldPrenume);
        this.add(etichetaMail);
        this.add(fieldMail);
        this.add(etichetaParola);
        this.add(fieldParola);
        this.add(panelButon);

        /**
         * Obiect ActionListener pentru gestionarea evenimentului de creare a unui utilizator.
         *
         * Este utilizat pentru a asculta evenimentul asociat butonului sau acțiunii
         * care indică crearea unui utilizator.
         */
        ActionListener creare = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String mail = fieldMail.getText();
                char[] passwordChars = fieldParola.getPassword();
                String parola = new String(passwordChars);
                String nume = fieldNume.getText();
                String prenume = fieldPrenume.getText();
                User u1 = new User(nume, prenume, mail);
                
                if(User.isValidEmail(mail)) {
                    try {
                        creareFisierUser();
                        creareFisierCont();
                        dispose();
                        scriereFisierUser(u1);
                        scriereFisierCont(mail,parola);
                        AplicatieUser a1 = new AplicatieUser();
                        a1.setVisible(true);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Eroare la scrierea în fișier: " + ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Mailul nu este corect.", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        buton.addActionListener(creare);
    }

    /**
     * Adaugă un nou utilizator în fisierul de utilizatori.
     *
     * @param u Utilizatorul care trebuie adaugat în fisier.
     * @throws IOException Excepție aruncata in cazul unor erori la operarea cu fisierul.
     */
    private static void scriereFisierUser(User u) throws IOException{

        FileWriter fileWriter = new FileWriter("src/main/resources/user.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(u.toString());
        bufferedWriter.newLine();

        bufferedWriter.close();
        System.out.println("Textul a fost adăugat în fișierul " + "user.txt");
    }

    /**
     * Adaugă o nouă înregistrare în fișierul de conturi.
     *
     * @param mail Adresa de email asociată contului.
     * @param parola Parola asociată contului.
     * @throws IOException Excepție aruncată în cazul unor erori la operarea cu fișierul.
     */
    private static void scriereFisierCont(String mail, String parola) throws IOException {

        FileWriter fileWriter = new FileWriter("src/main/resources/cont.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(mail + ";" + parola);
        bufferedWriter.newLine();

        bufferedWriter.close();
        System.out.println("Textul a fost adăugat în fișierul " + "cont.txt");
    }

    /**
     * Aceasta metoda verifica dacă fișierul "user.txt" exista deja. Daca nu există, il creeaza.
     * Daca fișierul exista deja, aceasta metoda nu face nimic.
     *
     * @throws IOException daca apare o problema la crearea fisierului
     */
    private static void creareFisierUser() throws IOException{
        String pathname = "src/main/resources/user.txt";
        File file = new File(pathname);

        if(!file.exists()){
            file.createNewFile();
        }
    }

    /**
     * Aceasta metoda verifica dacă fișierul "cont.txt" exista deja. Daca nu există, il creeaza.
     * Daca fișierul exista deja, aceasta metoda nu face nimic.
     *
     * @throws IOException daca apare o problema la crearea fisierului
     */
    private static void creareFisierCont() throws IOException{
        String pathname = "src/main/resources/cont.txt";
        File file = new File(pathname);

        if(!file.exists()){
            file.createNewFile();
        }
    }

}



