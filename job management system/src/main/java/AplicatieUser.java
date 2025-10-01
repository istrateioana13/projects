import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

public class AplicatieUser extends JFrame {

    Button aplic;
    JList <String> lista;
    DefaultListModel<String> model;
    JMenuBar nav;
    JTextField fieldOras;
    JTextField fieldCompanie;
    JLabel etichetaOras;
    JLabel etichetaCompanie;
    JButton searchOras;
    JButton searchCompanie;

    public AplicatieUser(){
        super("Sistem de gestionare a joburilor");
        this.setSize(new Dimension(440,280));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        aplic= new Button("Aplica");
        aplic.setPreferredSize(new Dimension(100,50));

        etichetaCompanie = new JLabel("Companie:");
        etichetaOras = new JLabel("Oras:");
        fieldCompanie = new JTextField(18);
        fieldOras = new JTextField(20);
        searchOras = new JButton("Cauta");
        searchCompanie = new JButton("Cauta");

        etichetaOras.setPreferredSize(new Dimension(60, 12));
        etichetaOras.setHorizontalAlignment(SwingUtilities.RIGHT);

        etichetaCompanie.setPreferredSize(new Dimension(80, 12));
        etichetaCompanie.setHorizontalAlignment(SwingUtilities.RIGHT);

        searchOras.setPreferredSize(new Dimension(80,50));
        searchCompanie.setPreferredSize(new Dimension(80,50));

        JPanel panelButon = new JPanel();
        panelButon.setPreferredSize(new Dimension(400, 200));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelButon.add(aplic, gbc);

        nav = new JMenuBar();
        JMenu filtru = new JMenu("Filtre");
        JMenuItem o = new JMenuItem("Oras");
        JMenuItem  comp = new JMenuItem("Comp");
        JMenuItem tot = new JMenuItem("Toate");
        filtru.add(o);
        filtru.add(comp);
        filtru.add(tot);

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

                sc2.useDelimiter("; ");

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
        this.add(etichetaOras);
        this.add(fieldOras);
        this.add(searchOras);
        this.add(etichetaCompanie);
        this.add(fieldCompanie);
        this.add(searchCompanie);
        this.add(panelButon);
        nav.add(filtru);
        this.setJMenuBar(nav);

        etichetaOras.setVisible(false);
        fieldOras.setVisible(false);
        searchOras.setVisible(false);
        etichetaCompanie.setVisible(false);
        fieldCompanie.setVisible(false);
        searchCompanie.setVisible(false);

        /**
         * Implementare ActionListener pentru gestionarea evenimentului de cautare a joburilor în funcție de companie.
         * Creeaza o lista rezultat pentru joburile din compania specificata de utilizator.
         * Adauga în model reprezentarile sub forma de siruri ale joburilor gasite în compania specificata.
         */
        ActionListener butonCompanie = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Job> rez = new ArrayList<>();
                rez = listaJoburi.jobsCompany(fieldCompanie.getText());
                for(Job  i : rez){
                    model.addElement(i.afisareFisier());
                }
            }
        };

        searchCompanie.addActionListener(butonCompanie);

        /**
         * Implementare ActionListener pentru gestionarea evenimentului de cautare a joburilor în funcție de oraș.
         * Creeaza o lista rezultat pentru joburile din orașul specificat de utilizator.
         * Adauga în model reprezentarile sub forma de siruri ale joburilor gasite în orasul specificat.
         */
        ActionListener butonOras = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               List<Job> rez = new ArrayList<>();
               rez = listaJoburi.jobsInCity(fieldOras.getText());
               for(Job  i : rez){
                   model.addElement(i.afisareFisier());
               }
            }
        };
        searchOras.addActionListener(butonOras);

        /**
         * Implementarea ActionListerner pentru a aplica la un job.
         * Sterge liniile selectate din JPanel.
         */
        ActionListener apl = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] indici = lista.getSelectedIndices();
                for(int i = indici.length-1; i>= 0; i--)
                {
                    model.remove(indici[i]);
                }

            }
        };
        aplic.addActionListener(apl);

        /**
         * Implementarea ActionListerner pentru a cauta dupa oras.
         * Elimina toate elementele din model.
         * Populeaza modelul cu reprezentarea sub forma de sir a fiecarui job din lista.
         * Ajusteaza dimensiunea preferata a panoului.
         */
        ActionListener cautareOras = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                model.removeAllElements();
                fieldOras.setText("");

                etichetaCompanie.setVisible(false);
                fieldCompanie.setVisible(false);
                searchCompanie.setVisible(false);

                etichetaOras.setVisible(true);
                fieldOras.setVisible(true);
                searchOras.setVisible(true);
                panou.setPreferredSize(new Dimension(440,100));

            }
        };
        o.addActionListener(cautareOras);

        /**
         * Implementarea ActionListerner pentru a cauta dupa companie.
         * Elimina toate elementele din model.
         * Populeaza modelul cu reprezentarea sub forma de sir a fiecarui job din lista.
         * Ajusteaza dimensiunea preferata a panoului.
         */

        ActionListener cautareComp = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                model.removeAllElements();
                fieldCompanie.setText("");

                etichetaOras.setVisible(false);
                fieldOras.setVisible(false);
                searchOras.setVisible(false);

                etichetaCompanie.setVisible(true);
                fieldCompanie.setVisible(true);
                searchCompanie.setVisible(true);
                panou.setPreferredSize(new Dimension(440,100));
            }
        };
        comp.addActionListener(cautareComp);

        /**
         * Implementare ActionListener pentru gestionarea evenimentului afișare a tuturor joburilor.
         * Elimina toate elementele din model.
         * Populeaza modelul cu reprezentarea sub forma de sir a fiecarui job din lista.
         * Ajusteaza dimensiunea preferata a panoului.
         */
        ActionListener cautareToate = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeAllElements();
                for(Job i : listaJoburi.getJoburi()){
                    model.addElement(i.afisareFisier());
                }
                etichetaOras.setVisible(false);
                fieldOras.setVisible(false);
                searchOras.setVisible(false);

                etichetaCompanie.setVisible(false);
                fieldCompanie.setVisible(false);
                searchCompanie.setVisible(false);

                panou.setPreferredSize(new Dimension(440,140));
            }
        };
        tot.addActionListener(cautareToate);
    }
}
