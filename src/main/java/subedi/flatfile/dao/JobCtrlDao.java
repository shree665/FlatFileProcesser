package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.JobControl;

public interface JobCtrlDao {
	List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode);
}
