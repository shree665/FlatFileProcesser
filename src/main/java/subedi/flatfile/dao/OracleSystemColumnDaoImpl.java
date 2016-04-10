package subedi.flatfile.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import subedi.flatfile.persistence.OracleSystemColumn;


@Repository
@Transactional
public class OracleSystemColumnDaoImpl implements OracleSystemColumnDao {

	private StatelessSession session;
	
	@Autowired
	public OracleSystemColumnDaoImpl(@Qualifier("rdsSessionFactory") final SessionFactory totalAccessSessionFactory) {
		session = totalAccessSessionFactory.openStatelessSession();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OracleSystemColumn> getOracleColumnsForTable(final String tableSchema, final String tableName) {

		// Stateless session for ORACLE metadata
		Criteria criteria = session.createCriteria(OracleSystemColumn.class);
		criteria.add(Restrictions.eq("id.tableSchema", tableSchema));
		criteria.add(Restrictions.eq("id.tableName", tableName));

		List<OracleSystemColumn> systemColumnList = criteria.list();

		// Close the sessions
		session.close();

		Assert.notNull(systemColumnList, "Retrieving information from ALL_TAB_COLUMNS failed");
		Assert.state(!systemColumnList.isEmpty(), "No rows in [ALL_TAB_COLUMNS] found for [" + tableSchema + "." + tableName + "]");

		return systemColumnList;
	}

}
