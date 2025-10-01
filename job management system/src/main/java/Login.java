import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class Login extends JFrame {

    JLabel conec;
    JLabel etichetaMail;
    JLabel etichetaParola;
    JPasswordField fieldParola;
    JTextField fieldMail;
    JButton buton;
    JButton logare;

    /**
     * Constructor pentru clasa Login.
     * Inițializează și configurează componentele ferestrei de logare.
     */
    public Login() {
        super("Sistem de gestionare a joburilor");

        this.setSize(430, 200);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        conec = new JLabel("Conectare");
        etichetaParola = new JLabel("Parola:");
        etichetaMail = new JLabel("Mail:");
        fieldParola = new JPasswordField(25);
        fieldMail = new JTextField(25);
        buton = new JButton("Creeaza cont");
        logare = new JButton("Logare");

        conec.setPreferredSize(new Dimension(400, 12));
        conec.setHorizontalAlignment(SwingUtilities.CENTER);

        etichetaParola.setPreferredSize(new Dimension(60, 12));
        etichetaParola.setHorizontalAlignment(SwingUtilities.RIGHT);

        etichetaMail.setPreferredSize(new Dimension(60, 12));
        etichetaMail.setHorizontalAlignment(SwingUtilities.RIGHT);

        buton.setPreferredSize(new Dimension(100,50));
        logare.setPreferredSize(new Dimension(100,50));

        JPanel panelButon = new JPanel();
        panelButon.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelButon.add(logare,gbc);
        panelButon.add(buton, gbc);

        this.add(conec);
        this.add(etichetaMail);
        this.add(fieldMail);
        this.add(etichetaParola);
        this.add(fieldParola);
        this.add(panelButon);

    /**
     * ActionListener pentru gestionarea evenimentului de autentificare (login).
     * Acest ActionListener este utilizat pentru a asculta evenimentul asociat butonului logare.
     * Verifică credențialele introduse (mail și parolă)
     * cu informațiile stocate în fișierul "cont.txt".
     */
        ActionListener log = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File fis = new File("src/main/resources/cont.txt");
                    Scanner sc = new Scanner(fis);
                    String m = fieldMail.getText();
                    char[] passwordChars = fieldParola.getPassword();
                    String p = new String(passwordChars);

                    if (m.equals("ioana@gmail.com") && p.equals("12345")) {
                        AplicatieManager a = new AplicatieManager();
                        a.setVisible(true);
                        dispose();

                    } else {
                        int c = 0;
                        while (sc.hasNextLine()) {

                            String linie = sc.nextLine();
                            Scanner sc2 = new Scanner(linie);

                            sc2.useDelimiter(";");

                            String mail = sc2.next();
                            String parola = sc2.next();

                            if (m.equals(mail) && p.equals(parola))
                                c = 1;

                            sc2.close();
                        }
                        sc.close();
                        if (c == 1) {
                            AplicatieUser u = new AplicatieUser();
                            u.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Mailul sau parola nu sunt corecte", "Eroare", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (IOException er) {
                    JOptionPane.showMessageDialog(null, "Eroare la citirea din fișier: " + er.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        logare.addActionListener(log);

        /**
         * Obiect ActionListener pentru gestionarea evenimentului de deschidere a ferestrei de Signup.
         * Închide fereastra curentă și deschide o nouă fereastră Signup.
         * Metoda este apelată atunci când are loc evenimentul de acțiune, adică atunci când
         * butonul creare este apăsat.
         */
        ActionListener creare = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Signup s = new Signup();
                s.setVisible(true);
            }
        };
        buton.addActionListener(creare);

    }
}


