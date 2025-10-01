import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AplicatieAdauga extends JFrame {

    JLabel etichetaId;
    JLabel etichetaTitlu;
    JLabel etichetaOras;
    JLabel etichetaCompanie;
    JLabel etichetaDescriere;
    JTextField fieldId;
    JTextField fieldOras;
    JTextField fieldCompanie;
    JTextField fieldDescriere;
    JTextField fieldTitlu;
    Button a;
    JMenuBar meniu;

    public AplicatieAdauga() {
        super("Sistem de gestionare a joburilor");
        this.setSize(new Dimension(440,270));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        etichetaCompanie = new JLabel("Companie:");
        etichetaDescriere = new JLabel("Descriere:");
        etichetaId = new JLabel("ID:");
        etichetaOras = new JLabel("Oras:");
        etichetaTitlu = new JLabel("Titlu:");

        fieldDescriere = new JTextField(25);
        fieldCompanie = new JTextField(25);
        fieldId = new JTextField(25);
        fieldOras = new JTextField(25);
        fieldTitlu = new JTextField(25);

        etichetaTitlu.setPreferredSize(new Dimension(70, 12));
        etichetaTitlu.setHorizontalAlignment(SwingUtilities.RIGHT);
        etichetaOras.setPreferredSize(new Dimension(70, 12));
        etichetaOras.setHorizontalAlignment(SwingUtilities.RIGHT);
        etichetaCompanie.setPreferredSize(new Dimension(70, 12));
        etichetaCompanie.setHorizontalAlignment(SwingUtilities.RIGHT);
        etichetaDescriere.setPreferredSize(new Dimension(70, 12));
        etichetaDescriere.setHorizontalAlignment(SwingUtilities.RIGHT);
        etichetaId.setPreferredSize(new Dimension(70, 12));
        etichetaId.setHorizontalAlignment(SwingUtilities.RIGHT);

        a = new Button("Adauga");
        a.setPreferredSize(new Dimension(100,50));

        JPanel panelButon = new JPanel();
        panelButon.setPreferredSize(new Dimension(410, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelButon.add(a, gbc);

        meniu = new JMenuBar();
        JMenu inap = new JMenu("File");
        JMenuItem inapoi = new JMenuItem("Inapoi");

        this.add(etichetaId);
        this.add(fieldId);
        this.add(etichetaTitlu);
        this.add(fieldTitlu);
        this.add(etichetaDescriere);
        this.add(fieldDescriere);
        this.add(etichetaOras);
        this.add(fieldOras);
        this.add(etichetaCompanie);
        this.add(fieldCompanie);
        this.add(panelButon);
        this.setJMenuBar(meniu);
        inap.add(inapoi);
        meniu.add(inap);

        /**
         * Inchide fereastra curenta si deschide fereastra AplicatieManager.
         */
        ActionListener ina = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AplicatieManager a = new AplicatieManager();
                a.setVisible(true);
                dispose();
            }
        };
        inapoi.addActionListener(ina);

        /**
         * Preia valorile de intrare din campurile de text.
         * Creeaza un nou obiect Job cu informatiile actualizate.
         * Scrie informatiile actualizate în fisierul "joburi.txt".
         */
        ActionListener ad = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = fieldId.getText();
                    String titlu = fieldTitlu.getText();
                    String descriere = fieldDescriere.getText();
                    String oras = fieldOras.getText();
                    String companie = fieldCompanie.getText();
                    Job j = new Job(id, titlu, oras, companie, descriere);

                    creareFisierJoburi();
                    appendFisier(j);

                }catch(IOException er){
                    JOptionPane.showMessageDialog(null, "Eroare la scrierea în fișier: " + er.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        a.addActionListener(ad);

    }

    /**
     * Metoda pentru adaugarea unui job la sfarsitul fisierului "joburi.txt".
     * @param j Job-ul de adaugat în fisier.
     * @throws IOException Arunca o exceptie in cazul in care apare o eroare la scrierea în fisier.
     */
    private static void appendFisier(Job j) throws IOException {

            FileWriter fileWriter = new FileWriter("src/main/resources/joburi.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(j.toString());
            bufferedWriter.newLine();

            bufferedWriter.close();
            System.out.println("Textul a fost adăugat în fișierul joburi.txt");

    }

    /**
     * Aceasta metoda verifica dacă fișierul "joburi.txt" exista deja. Daca nu există, il creeaza.
     * Daca fișierul exista deja, aceasta metoda nu face nimic.
     *
     * @throws IOException daca apare o problema la crearea fisierului
     */
    private static void creareFisierJoburi() throws IOException{
        String pathname = "src/main/resources/joburi.txt";
        File file = new File(pathname);

        if(!file.exists()){
            file.createNewFile();
        }
    }
}
