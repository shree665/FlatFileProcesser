package subedi.flatfile.testJob;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import subedi.flatfile.test.support.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = { TestEnvironmentInitializer.class },
		locations = { "classpath:subedi/flatfile/SUBEDIFILE_J01_Test-context.xml" })
@DirtiesContext
public class SUBEDIFILE_J01_Test {
	
	//private final String JOB_LAUNCH_NAME = "SUBEDIFILE_J01";

	private static final String INPUT_FILE = "src/test/input/inst1_awdb.dbo.Agent.20160413.041022.3510025";

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	private JobExecution jobExecution = null;
	private JobParameters jobParameters = null;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private TemporaryFolderBean tempFolderBean;

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

		jobParameters = new JobParameters(jobParametersContent);

	}

	/**
	 * Assures the expected amount of DB entries exist after the step is complete
	 * @throws Exception 
	 */
	@Test
	public void testJob() throws Exception {
		File tempStaging = tempFolderBean.getStagingDirectory();
		FileUtils.copyFileToDirectory(new File(INPUT_FILE), tempStaging);
		String rootPath = tempFolderBean.getCanonicalPath();
		System.out.println("You can find the temp files here: " + rootPath);
		// launch
		jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		stat = jobExecution.getExitStatus();
		
		DataQueryUtils.printSQLQuery(jdbcTemplate, "SELECT * FROM CCM_ICM_AGNT");
		
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
		jobExecution = null;
		jobParameters = null;
	}

}
