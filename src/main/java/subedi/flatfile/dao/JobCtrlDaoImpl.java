package subedi.flatfile.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import subedi.flatfile.persistence.JobControl;

@Repository
@Transactional
public class JobCtrlDaoImpl implements JobCtrlDao {
	
	private Session session;
	
	@Autowired
	public JobCtrlDaoImpl(@Qualifier("sessionFactory") final SessionFactory codSessionFactory) {
		session = codSessionFactory.openSession();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<JobControl> getJobControlsForDatabase(final String jobName, final String databaseCode) {

		final Criteria criteria = session.createCriteria(JobControl.class);
		criteria.add(Restrictions.eq("databaseCode", databaseCode));
		criteria.add(Restrictions.eq("jobName", jobName));

		final List<JobControl> jobControlList = criteria.list();

		Assert.notNull(jobControlList, "Query on JOB_CTRL failed");
		Assert.state(!jobControlList.isEmpty(), "No rows in JOB_CTRL found for databaseCode = [" + databaseCode + "] for jobName = ["+jobName+"]");

		return jobControlList;
	}
}
