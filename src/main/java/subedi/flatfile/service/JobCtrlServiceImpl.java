package subedi.flatfile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import subedi.flatfile.dao.JobCtrlDao;
import subedi.flatfile.persistence.JobControl;
import subedi.flatfile.persistence.enumerated.BehaviorEnum;

/**
 * 
 * @author vivek.subedi
 *
 */
@Service
public class JobCtrlServiceImpl implements JobCtrlService {
	
	@Autowired
	private JobCtrlDao jobCtrlDao;

	private HashMap<Integer, JobControl> cachedJobControls;
	
	@Override
	public List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode, String behavior) {
		
		List<JobControl> jobControlsForDatabase = this.getJobControlsForDatabase(jobName, databaseCode);
		
		BehaviorEnum beahviorE = BehaviorEnum.valueOf(behavior);
		List<JobControl> jobControlList = new ArrayList<JobControl>();
		
		for (JobControl jc : jobControlsForDatabase) {
			
			if (jc.getDatabaseCode().equalsIgnoreCase(databaseCode)
				&& jc.getBehaviorCode() == beahviorE) {
				jobControlList.add(jc);
			}
		}
		
		return jobControlList;
	}
	
	public List<JobControl> getJobControlsForDatabase(String jobName, String databaseCode) {
		
		if (cachedJobControls == null) {
			
			cachedJobControls = new HashMap<Integer, JobControl>();
			
			List<JobControl> jobControls = jobCtrlDao.getJobControlsForDatabase(jobName, databaseCode);
			for (JobControl control : jobControls) {
				cachedJobControls.put(control.getJobControlId(), control);
			}
		}
		
		List<JobControl> newList = new ArrayList<JobControl>(cachedJobControls.values());
		//return (List<JobControl>) cachedJobControls.values();
		return newList;
	}
	
	@Override
	public JobControl getJobControl(Integer jobControlId) {
		if (cachedJobControls == null) {
			throw new RuntimeException("Cannot call JobCotrolService.getJobControl() before JobCotrolService.getJobControlsForDatabase()");
		}
		return cachedJobControls.get(jobControlId);
	}

	@Override
	public JobControl getJobControlForTable(String jobName, String databaseCode, String tableName) {
		
		List<JobControl> jobControlsForDatabase = this.getJobControlsForDatabase(jobName, databaseCode);
		
		for(JobControl jobControl : jobControlsForDatabase) {
			if(jobControl.getTableName().equalsIgnoreCase(tableName)) {
				return jobControl;
			}
		}
		
		throw new IllegalArgumentException("No JobControl found for table ["+tableName+"] with databaseCode ["+databaseCode+"] for job ["+jobName+"]");
	}

}
