package subedi.flatfile.testJob;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class to do integration test of the job
 * 
 * @author vivek.subedi
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:SUBEDIFILE_J01-context.xml", "classpath:test-context.xml"})
public class TestJob {
	
	@Value("${batch.schema}")
	private String schema;
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils = null;
	
	private final String DELIMITER_CHARACTER = "|";
	private final long COMMIT_INTERVAL = 100l;
	private final String FILE_ENCODING = "UTF-8";
	private final String JOB_NAME = "SUBEDIFILE_J01";
	private final String DATABASE_CODE = "ICM";
	private final String FILE_CATAGORY = "TELEPHONY_ANALYTICS";
	private final String FILE_STATUS = "NEW";
	private final String FILE_TYPE = "ICM";
	private final String MAPPING_NAME = "ICM_MAPPING";
	private final String PROCESS_FAILED = "true";
	private final long MAX_RUN_COUNT = 10L;

	private JobParameters jobParameters = null;
	
	@Before
	public void setUp() {
		// prepare job parameters
		Map<String, JobParameter> jobParametersContent = new HashMap<String, JobParameter>();

		//job parameters
		jobParametersContent.put("DELIMITER.CHARACTER", new JobParameter(DELIMITER_CHARACTER));
		jobParametersContent.put("COMMIT.INTERVAL", new JobParameter(COMMIT_INTERVAL));
		jobParametersContent.put("FILE.ENCODING", new JobParameter(FILE_ENCODING));
		jobParametersContent.put("JOB.NAME", new JobParameter(JOB_NAME));
		jobParametersContent.put("DATABASE.CODE", new JobParameter(DATABASE_CODE));
		jobParametersContent.put("FILE.CATAGORY", new JobParameter(FILE_CATAGORY));
		jobParametersContent.put("FILE.STATUS", new JobParameter(FILE_STATUS));
		jobParametersContent.put("FILE.TYPE", new JobParameter(FILE_TYPE));
		jobParametersContent.put("MAPPING.NAME", new JobParameter(MAPPING_NAME));
		jobParametersContent.put("PROCESS.FAILED", new JobParameter(PROCESS_FAILED));
		jobParametersContent.put("MAX.RUN.COUNT", new JobParameter(MAX_RUN_COUNT));
		jobParametersContent.put("RUN", new JobParameter(new Date().getTime()));
		
		jobParameters = new JobParameters(jobParametersContent);
	}
	
	@After
	public void tearDown() throws IOException {
		jobParameters = null;
		jobLauncherTestUtils = null;
	}
	
	@Test
	public void launchJob() throws Exception {
		//testing a job
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}
