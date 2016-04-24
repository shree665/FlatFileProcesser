package subedi.flatfile.service;

import java.util.List;

import subedi.flatfile.persistence.JobControl;
/**
 * 
 * @author vivek.subedi
 *
 */
public interface JobCtrlService {
	/**
	 * Method to retrieve the list of JobControl objects based on jobname, 
	 * database code and behaviors of the table
	 * @param jobName - job name
	 * @param databaseCode - database code
	 * @param behaviors - behaviors of a table
	 * @return list of JobControl objects
	 */
	List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode, String behaviors);
	
	/**
	 * Method to return a specific JobControl object from cache using job control id
	 * 
	 * @param jobControlId
	 * @return
	 */
	JobControl getJobControl(Integer jobControlId);
	
	/**
	 * Method to retrieve a JobControl object from a cache list using tablename
	 * 
	 * @param jobName
	 * @param databaseCode
	 * @param tableName
	 * @return
	 */
	JobControl getJobControlForTable(String jobName, String databaseCode, String tableName);
}
