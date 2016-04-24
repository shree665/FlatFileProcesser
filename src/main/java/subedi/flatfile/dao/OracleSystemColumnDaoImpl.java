package subedi.flatfile.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import subedi.flatfile.persistence.OracleSystemColumn;
/**
 * 
 * @author vivek.subedi
 *
 */

@Repository
@Transactional
public class OracleSystemColumnDaoImpl extends HibernateAbstractDao<Object> implements OracleSystemColumnDao {
	
	@Autowired
	public OracleSystemColumnDaoImpl(@Qualifier("sessionFactory") final SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OracleSystemColumn> getOracleColumnsForTable(final String tableSchema, final String tableName) {

		Criteria criteria = this.getSession().createCriteria(OracleSystemColumn.class);
		criteria.add(Restrictions.eq("id.tableSchema", tableSchema));
		criteria.add(Restrictions.eq("id.tableName", tableName));

		List<OracleSystemColumn> systemColumnList = criteria.list();

		Assert.notNull(systemColumnList, "Retrieving information from ALL_TAB_COLUMNS failed");
		Assert.state(!systemColumnList.isEmpty(), "No rows in [ALL_TAB_COLUMNS] found for [" + tableSchema + "." + tableName + "]");

		return systemColumnList;
	}

}
