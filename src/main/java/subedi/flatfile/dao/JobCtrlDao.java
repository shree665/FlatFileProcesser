package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.JobControl;

/**
 * Interface to get all the tables from job ctrl table
 * 
 * @author vivek.subedi
 *
 */
public interface JobCtrlDao {
	/**
	 * Method to retrieve all the tables that need to process from JOB_CTRL table
	 * for a specific job using hibernate
	 * 
	 * @param jobName - Job name that tables belong to
	 * @param databaseCode - database code
	 * @return List of Job control objects 
	 */
	List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode);
}
