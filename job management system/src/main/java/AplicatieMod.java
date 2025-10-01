import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AplicatieMod extends JFrame {
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

    public AplicatieMod(){
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

        a = new Button("Modifica");
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

        JobManager listaJoburi = new JobManager();
        try{
            File fis = new File("src/main/resources/joburi.txt");
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()){

                String linie = sc.nextLine();
                Scanner sc2 = new Scanner(linie);

                sc2.useDelimiter("; ");

                String id = sc2.next();
                String titlu = sc2.next();
                String descriere = sc2.next();
                String oras = sc2.next();
                String companie = sc2.next();
                Job j = new Job(id, titlu, oras, companie, descriere);

                listaJoburi.addJob(j);

                sc2.close();
            }
            sc.close();

        }catch (IOException er){
            JOptionPane.showMessageDialog(null, "Eroare la citirea din fișier: " + er.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);

        }

        /**
         * Preia valorile de intrare din campurile de text.
         * Creeaza un nou obiect Job cu informatiile actualizate.
         * Actualizeaza JobManager-ul cu Job-ul modificat.
         * Scrie informatiile actualizate în fisierul "joburi.txt".
         */
        ActionListener mod = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = fieldId.getText();
                String titlu = fieldTitlu.getText();
                String descriere = fieldDescriere.getText();
                String oras = fieldOras.getText();
                String companie = fieldCompanie.getText();
                Job j = new Job(id, titlu, oras, companie, descriere);

                listaJoburi.updateJob(j.getId(),j);

                try{
                    creareFisierJoburi();
                    scriereFisier(listaJoburi);
                } catch (IOException er) {
                    System.out.println("A apărut o eroare în timpul suprascrierii fișierului: " + er.getMessage());
                }
            }

        };
        a.addActionListener(mod);
    }

    /**
     * Metoda pentru suprascrierea conținutului fișierului "joburi.txt" cu informațiile din JobManager.
     * @param j Obiectul JobManager care conține lista de joburi de scris în fisier.
     * @throws IOException Aruncă o excepție in cazul în care apare o eroare la scrierea în fisier.
     */
    private static void scriereFisier(JobManager j) throws IOException{

            FileWriter fileWriter = new FileWriter("src/main/resources/joburi.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for(Job jo : j.getJoburi()){
                bufferedWriter.write(jo.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            fileWriter.close();

            System.out.println("Conținutul a fost suprascris dupa modificare în fișierul joburi.txt.");
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