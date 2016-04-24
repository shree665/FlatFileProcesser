package subedi.flatfile.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

/**
 * Abstract DAO which all others should extend. All extending classes should be annotated with
 * 
 * {@link org.springframework.stereotype.Repository}
 *
 * @author vivek.subedi
 * @param <T>
 *
 */
public abstract class HibernateAbstractDao<T> extends HibernateDaoSupport {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The default schema for the DAO to use
	 */
	protected final String schema;

	/**
	 * The sessionFactory for the DAO to use
	 */
	protected final SessionFactory sessionFactory;

	/**
	 * Constructor that must be used implemented all extending classes. It should be annotated with
	 * {@link org.springframework.beans.factory.annotation.Autowired} and use {@link org.springframework.beans.factory.annotation.Qualifier}
	 * to specify the session factory bean name
	 *
	 * @param sessionFactory The {@link org.hibernate.SessionFactory} for the DAO to use
	 */
	@SuppressWarnings("deprecation")
	public HibernateAbstractDao(final SessionFactory sessionFactory) {

		logger.debug("");
		this.setHibernateTemplate(new HibernateTemplate(sessionFactory));
		this.sessionFactory = sessionFactory;
		this.schema = ((SessionFactoryImplementor) sessionFactory).getSettings().getDefaultSchemaName();
	}

	/**
	 * Returns the current Session, and opens a new one if the session is not open
	 *
	 * @return an open {@link Session}.
	 */
	public Session getSession() {
		// TODO: check if this is at all valid
		Session session = null;

		try {
			session = this.getSessionFactory().getCurrentSession();
		} catch (final HibernateException ex) {
			logger.debug("Could not retrieve pre-bound Hibernate session", ex);
		}
		if (session == null) {
			session = this.getSessionFactory().openSession();
		}
		return session;
	}

	public void delete(final T entity) {
		this.getHibernateTemplate().delete(entity);
	}

	public void delete(final List<? extends T> entities) {
		for (final T entity : entities) {
			this.delete(entity);
		}
	}

	public void deleteAll(final List<? extends T> entities) {
		this.getHibernateTemplate().deleteAll(entities);
	}

	public void update(final T entity) {
		this.getHibernateTemplate().update(entity);
	}

	public void update(final List<? extends T> entities) {
		for (final T entity : entities) {
			this.update(entity);
		}
	}

	public T save(final T entity) {
		this.getHibernateTemplate().save(entity);
		return entity;
	}

	public void save(final List<? extends T> entities) {
		for (final T entity : entities) {
			this.save(entity);
		}
	}

	public void saveOrUpdate(final T entity) {
		this.getHibernateTemplate().saveOrUpdate(entity);
	}

	public void saveOrUpdate(final List<? extends T> entities) {
		for (final T entity : entities) {
			this.getHibernateTemplate().saveOrUpdate(entity);
		}
	}

	public void persist(final List<? extends T> entities) {
		for (final T entity : entities) {
			this.persist(entity);
		}
	}

	public T merge(final T entity) {
		return this.getHibernateTemplate().merge(entity);
	}

	public void persist(final T entity) {
		this.getHibernateTemplate().persist(entity);
	}

	public void evict(final T entity) {
		this.getHibernateTemplate().evict(entity);
	}

	@SuppressWarnings("unchecked")
	final protected List<? extends T> findByCriteria(final DetachedCriteria criteria) {
		return (List<? extends T>) this.getHibernateTemplate().findByCriteria(criteria);
	}

	final protected Criteria createCriteria(final Class<T> clazz) {
		return this.getSession().createCriteria(clazz);
	}
}
