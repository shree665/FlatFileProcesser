package subedi.flatfile.testJob;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test Case for the CCMICMETLED_J01 job
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CCMICMETLED_J01_Test {

	static {
		System.setProperty("test.database.hsqldb.environment", "mem");
	}

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private ExitStatus stat;

	private final String DELIMITER_CHARACTER = "|";
	private final long COMMIT_INTERVAL = 1L;
	private final String FILE_ENCODING = "UTF-8";
	private final String JOB_NAME = "CCMICMETLED_J01";
	private final String DATABASE_CODE = "ICM";
	private final String FILE_CATAGORY = "TELEPHONY_ANALYTICS";
	private final String FILE_STATUS = "NEW";
	private final String FILE_TYPE = "ICM";
	private final String MAPPING_NAME = "CCM_ICM_MAPPING";
	private final String PROCESS_FAILED = "true";
	private final long MAX_RUN_COUNT = 10L;

	/**
	 * Sets up the testing step environment.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		// prepare job parameters
		final Map<String, JobParameter> jobParametersContent = new HashMap<String, JobParameter>();

		// CCMICMETLED_J01 job parameters

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

		final JobParameters jobParameters = new JobParameters(jobParametersContent);

		// launch

		JobExecution jobExecution = null;
		jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		stat = jobExecution.getExitStatus();

	}

	/**
	 * Assures the expected amount of DB entries exist after the step is complete
	 *
	 * @throws IOException
	 */
	@Test
	public void testDbEntries() throws IOException {

		// Assert completed
		assertEquals(ExitStatus.COMPLETED, stat);

	}

	/**
	 * Tear down method for each test case cleans database and working directory
	 *
	 * @throws IOException if database cannot be reached
	 */
	@After
	public void tearDown() throws IOException {
		jobLauncherTestUtils = null;
	}

}
