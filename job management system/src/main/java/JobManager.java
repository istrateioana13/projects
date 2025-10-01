import java.util.ArrayList;
import java.util.List;

public class JobManager extends Job{
    List<Job> joburi;

    /**
     * Constructor pentru clasa JobManager.
     */
    public JobManager(){
        joburi = new ArrayList<>();
    }

    /**
     * Aceasta metoda adauga un obiect de tip Job in lista de joburi.
     * @param j Obiectul de tip Job care urmeaza sa fie adaugat in lista de joburi.
     */
    public void addJob(Job j){
        joburi.add(j);
    }

    /**
     * Această metodă caută un job în lista de joburi utilizând ID-ul specificat și actualizează
     * informațiile acestuia cu valorile furnizate prin obiectul Job j.
     *
     * @param id ID-ul jobului care trebuie actualizat.
     * @param j  Obiectul de tip Job care furnizează noile informații pentru actualizare.
     */
    public void updateJob(String id, Job j){
        for(Job i : this.joburi){
            if(i.getId().equals(id)) {
                i.setOras(j.getOras());
                i.setCompanie(j.getCompanie());
                i.setDescriere(j.getDescriere());
                i.setTitlu(j.getTitlu());
            }
        }
    }

    /**
     * Această metodă șterge jobul de la poziția specificată în listă. Indexul trebuie să fie
     * în intervalul valid pentru lista curentă de joburi.
     *
     * @param index Indexul jobului care trebuie șters.
     */
    public void deleteJob(int index){

        joburi.remove(index);
    }

    /**
     * Această metodă caută toate joburile din lista de joburi gestionată de JobManager
     * care au orașul specificat și le adaugă într-o nouă listă care este returnată.
     *
     * @param city Orașul pentru care se caută joburi.
     * @return O listă de joburi din orașul specificat.
     */
    public List<Job> jobsInCity(String city){
        List<Job> rez = new ArrayList<>();
        for(Job i : this.joburi){
            if(i.getOras().equals(city)) {
               rez.add(i);
            }
        }
        return rez;
    }

    /**
     * Această metodă caută toate joburile din lista de joburi gestionată de JobManager
     * care sunt oferite de compania specificată și le adaugă într-o nouă listă care este returnată.
     *
     * @param company Numele companiei pentru care se caută joburi.
     * @return O listă de joburi oferite de compania specificată.
     */
    public List<Job> jobsCompany(String company){
        List<Job> rez = new ArrayList<>();
        for(Job i : this.joburi){
            if(i.getCompanie().equals(company))
              rez.add(i);
        }
        return rez;
    }

    /**
     * Această metodă furnizează acces la lista de joburi curentă din JobManager.
     *
     * @return Lista de joburi din JobManager.
     */
    public List<Job> getJoburi() {
        return joburi;
    }

}
