import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JobManagerTest {

    private JobManager jobManager;

    @Before
    public void setup() {
        jobManager = new JobManager();
    }
    @Test
    public void testAddJob() {
        Job job = new Job("1", "Programator", "Cluj", "CompanyX", "Descriere job");
        jobManager.addJob(job);

        List<Job> joburi = jobManager.getJoburi();
        assertEquals(1, joburi.size());
        assertEquals(job, joburi.get(0));
    }

    @Test
    public void testUpdateJob() {
        Job job1 = new Job("1", "Programator", "Cluj", "CompanyX", "Descriere job");
        jobManager.addJob(job1);

        Job job2 = new Job("1", "Inginer", "Bucuresti", "CompanyY", "Descriere job noua");
        jobManager.updateJob("1", job2);

        List<Job> joburi = jobManager.getJoburi();
        assertEquals(1, joburi.size());
        assertEquals(job2, joburi.get(0));
    }

    @Test
    public void testDeleteJob() {
        Job job = new Job("1", "Programator", "Cluj", "CompanyX", "Descriere job");
        jobManager.addJob(job);

        jobManager.deleteJob(0);

        List<Job> joburi = jobManager.getJoburi();
        assertEquals(0, joburi.size());
    }

    @Test
    public void testJobsInCity() {
        Job job1 = new Job("1", "Programator", "Cluj", "CompanyX", "Descriere job");
        Job job2 = new Job("2", "Tester", "Bucuresti", "CompanyY", "Descriere job noua");

        jobManager.addJob(job1);
        jobManager.addJob(job2);

        List<Job> jobsInCluj = jobManager.jobsInCity("Cluj");
        assertEquals(1, jobsInCluj.size());
        assertEquals(job1, jobsInCluj.get(0));
    }

    @Test
    public void testJobsCompany() {
        Job job1 = new Job("1", "Programator", "Cluj", "CompanyX", "Descriere job");
        Job job2 = new Job("2", "Tester", "Bucuresti", "CompanyY", "Descriere job noua");

        jobManager.addJob(job1);
        jobManager.addJob(job2);

        List<Job> jobsCompanyX = jobManager.jobsCompany("CompanyX");
        assertEquals(1, jobsCompanyX.size());
        assertEquals(job1, jobsCompanyX.get(0));
    }
}
