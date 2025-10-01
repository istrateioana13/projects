import static org.junit.Assert.*;
import org.junit.Test;

public class JobTest {

    @Test
    public void testConstructor3() {
        Job job = new Job("2", "Analist", "Bucuresti", "CompanyY", "Descriere job");
        assertEquals("2", job.getId());
        assertEquals("Analist", job.getTitlu());
        assertEquals("Bucuresti", job.getOras());
        assertEquals("CompanyY", job.getCompanie());
        assertEquals("Descriere job", job.getDescriere());
    }

    @Test
    public void testConstructor4() {
        Job altJob = new Job("3", "Inginer", "Iasi", "CompanyZ", "Descriere alt job");
        assertEquals("3", altJob.getId());
        assertEquals("Inginer", altJob.getTitlu());
        assertEquals("Iasi", altJob.getOras());
        assertEquals("CompanyZ", altJob.getCompanie());
        assertEquals("Descriere alt job", altJob.getDescriere());
    }

    @Test
    public void testAfisareFisier() {
        Job job = new Job("3", "Designer", "Timisoara", "CompanyZ", "Descriere job");
        assertEquals("3 Designer Descriere job Timisoara CompanyZ", job.afisareFisier());

    }
}
