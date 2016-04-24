/**
 *
 */
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

import subedi.flatfile.persistence.RefData;

/**
 * @author vivek.subedi
 *
 */

@Repository
@Transactional
public class RefDataDaoImpl extends HibernateAbstractDao<Object> implements RefDataDao {

	@Autowired
	public RefDataDaoImpl(@Qualifier("sessionFactory") final SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RefData> getRefDataforDatabase(final String mappingName) {
		final Criteria criteria = this.getSession().createCriteria(RefData.class);
		criteria.add(Restrictions.eq("id.ccmMappingName", mappingName));
		final List<RefData> refDataList = criteria.list();
		Assert.notNull(refDataList, "Query on Ref data failed");
		Assert.state(!refDataList.isEmpty(), "No rows in CCM_REF found for mapping name = [" + mappingName + "]");
		return refDataList;
	}
}
