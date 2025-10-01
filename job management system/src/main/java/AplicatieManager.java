import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

public class AplicatieManager extends JFrame {

    Button adaug;
    Button sterg;
    Button modific;
    JList <String> lista;
    DefaultListModel<String> model;

    public AplicatieManager(){
        super("Sistem de gestionare a joburilor");
        this.setSize(new Dimension(440,240));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        adaug = new Button("Adaugare");
        sterg = new Button("Stergere");
        modific = new Button("Modificare");

        adaug.setPreferredSize(new Dimension(100,50));
        sterg.setPreferredSize(new Dimension(100,50));
        modific.setPreferredSize(new Dimension(100,50));

        JPanel panelButon = new JPanel();
        panelButon.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelButon.add(adaug, gbc);
        panelButon.add(sterg, gbc);
        panelButon.add(modific, gbc);

        model = new DefaultListModel<>();
        lista = new JList<>(model);

        JScrollPane panou = new JScrollPane(lista);
        panou.setPreferredSize(new Dimension(440,140));
        lista.setFixedCellWidth(250);
        lista.setFixedCellHeight(20);
        lista.setVisibleRowCount(5);
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JobManager listaJoburi = new JobManager();
        try{
            File fis = new File("src/main/resources/joburi.txt");
            Scanner sc = new Scanner(fis);
            while(sc.hasNextLine()){

                String linie = sc.nextLine();
                Scanner sc2 = new Scanner(linie);

                sc2.useDelimiter(";");

                String id = sc2.next();
                String titlu = sc2.next();
                String descriere = sc2.next();
                String oras = sc2.next();
                String companie = sc2.next();
                Job j = new Job(id, titlu, oras, companie, descriere);

                listaJoburi.addJob(j);
                model.addElement(j.afisareFisier());

                sc2.close();
            }
            sc.close();

        }catch (IOException er){
            JOptionPane.showMessageDialog(null, "Eroare la citirea din fișier: " + er.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);

        }

        this.add(panou);
        this.add(panelButon);

        /**
         * Creeaza o noua fereastra AplicatieAdauga si o inchide pe cea curenta.
         */
        ActionListener ada = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AplicatieAdauga i = new AplicatieAdauga();
                i.setVisible(true);
            }
        };
        adaug.addActionListener(ada);

        /**
         * Sterge liniile selectate din JPanel, iar in paralel le sterge si lista de joburi listaJoburi.
         */
        ActionListener str = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indici = lista.getSelectedIndices();
                for(int i = indici.length-1; i>= 0; i--)
                {
                    model.remove(indici[i]);
                    listaJoburi.deleteJob(indici[i]);
                }
                try{
                    creareFisierJoburi();
                    scriereFisier(listaJoburi);
                }catch (IOException er) {
                    System.out.println("A apărut o eroare în timpul suprascrierii fișierului: " + er.getMessage());
                }

            }
        };
       sterg.addActionListener(str);

        /**
         * Creeaza o noua fereastra AplicatieMod si o inchide pe cea curenta.
         */
        ActionListener mod = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AplicatieMod m = new AplicatieMod();
                m.setVisible(true);
                dispose();
            }
        };
       modific.addActionListener(mod);

    }

    /**
     * Metoda pentru suprascrierea conținutului fișierului "joburi.txt" cu informațiile din JobManager.
     * @param j Obiectul JobManager care conține lista de joburi de scris în fisier.
     * @throws IOException Aruncă o excepție in cazul în care apare o eroare la scrierea în fisier.
     */
    private static void scriereFisier(JobManager j) throws IOException {

        FileWriter fileWriter = new FileWriter("src/main/resources/joburi.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for(Job jo : j.getJoburi()) {
            bufferedWriter.write(jo.toString());
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
        fileWriter.close();
        System.out.println("Textul a fost suprascris dupa stergere în fișierul joburi.txt");
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