package subedi.flatfile.service;

import java.util.List;

import subedi.flatfile.persistence.JobControl;

public interface JobCtrlService {
	
	List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode, String behaviors);
	
	JobControl getJobControl(Integer jobControlId);
	
	JobControl getJobControlForTable(String jobName, String databaseCode, String tableName);
}
