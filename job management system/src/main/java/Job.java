public class Job{
    String id;
    String titlu;
    String oras;
    String companie;
    String descriere;

    /**
     * Constructor pentru clasa Job.
     */
    public Job(){
        id = null;
        titlu = null;
        oras = null;
        companie = null;
        descriere = null;

    }

    /**
     * Constructor pentru clasa Job.
     */
    public Job(String i, String t,String o, String c, String d ){
        this.id = i;
        this.titlu = t;
        this.oras = o;
        this.companie = c;
        this.descriere = d;
    }

    /**
     * Returnează orașul asociat acestui job.
     *
     * @return Un string care reprezintă orașul asociat acestui job.
     */
    public String getOras(){
        return this.oras;
    }

    /**
     * Returnează compania asociata acestui job.
     *
     * @return Un string care reprezintă compania acestui job.
     */
    public String getCompanie(){
        return this.companie;
    }

    /**
     * Generează un șir de caractere care reprezintă informațiile jobului pentru afișare în fișier.
     *
     * @return Un șir de caractere care reprezintă informațiile jobului pentru afișare în fișier.
     */
    public String afisareFisier(){
        String rez = this.id + " " + this.titlu + " " + this.descriere + " " + this.oras + " " + this.companie;
        return rez;
    }

    public String getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public void setOras(String oras) {
        this.oras = oras;
    }

    public void setCompanie(String companie) {
        this.companie = companie;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    @Override
    public String toString(){
        String rez = this.id + "; " + this.titlu + "; " + this.descriere + "; " + this.oras + "; " + this.companie;
        return rez;
    }

}
