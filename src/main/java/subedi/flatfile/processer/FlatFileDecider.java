package subedi.flatfile.processer;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import subedi.flatfile.util.FlatFileUtil;

public class FlatFileDecider implements JobExecutionDecider {
	
	private static Logger logger = LoggerFactory.getLogger(FlatFileDecider.class);

	/**
	 * Strategy for branching an execution based on the state of an ongoing
	 * job. The return value will be used as a status to determine the next step in the job.
	 *
	 * @param jobExecution - the job execution
	 * @param stepExecution - the latest step execution (may be null)
	 * @return the exit status code i.e. MORE_FILES_TO_PROCESS OR NO_MORE_FILES_TO_PROCESS
	 */
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution arg1) {
		String stagingFolderPath = FlatFileUtil.STAGING_PATH;
		
		String databaseCode = jobExecution.getJobParameters().getString(FlatFileUtil.DATABASECODE_KEY);

		@SuppressWarnings("unchecked")
		Collection<File> filesLeft = FileUtils.listFiles(new File(stagingFolderPath), null, false);
		Collection<File> filteredFiles = FlatFileUtil.matchPatterFilePrefix(filesLeft, databaseCode);

		Integer maxRunCount = Integer.parseInt(jobExecution.getJobParameters().getString(FlatFileUtil.MAX_RUN_COUNT_KEY));
		Integer runCount = null;

		if(!jobExecution.getExecutionContext().containsKey(FlatFileUtil.RUN_COUNT_KEY)) {
			runCount = new Integer(1);
			logger.info("First run, instantiating run count to [1]");
			jobExecution.getExecutionContext().putInt(FlatFileUtil.RUN_COUNT_KEY, runCount);
		} else {
			runCount = jobExecution.getExecutionContext().getInt(FlatFileUtil.RUN_COUNT_KEY);
			runCount++;
			jobExecution.getExecutionContext().putInt(FlatFileUtil.RUN_COUNT_KEY, runCount);
			logger.info("Incrementing run count to ["+runCount+"]");
		}

		//if the decider run more that provided max runs, job will stop
		if(runCount > maxRunCount) {
			logger.warn("Hit the maximum run count of ["+maxRunCount+"], please check if there is an issue with the files. Terminating job.");
			return new FlowExecutionStatus("MAX_RUNS_HIT");
		}

		if(!filteredFiles.isEmpty()) {
			return new FlowExecutionStatus("MORE_FILES_TO_PROCESS");
		} else {
			return new FlowExecutionStatus("NO_MORE_FILES_TO_PROCESS");
		}
	}

}
