
package subedi.flatfile.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import subedi.flatfile.persistence.UploadedFileRecord;
import subedi.flatfile.persistence.enumerated.FileStatusEnum;
import subedi.flatfile.persistence.enumerated.FileTypeEnum;


/**
 * The Class UploadedFileDaoImpl is a Dao class for Uploaded Files pojo.  retrieves all the files that satisfy the criteria.
 *
 * @author vivek.subedi
 */

@Repository
@Transactional
public class UploadedFileRecordDaoImpl extends HibernateDaoSupport implements UploadedFileRecordDao {

	private static final Logger logger = LoggerFactory.getLogger(UploadedFileRecordDaoImpl.class);
	
	private StatelessSession session;

	@Autowired
	public UploadedFileRecordDaoImpl(@Qualifier("tdsSessionFactory") final SessionFactory sessionFactory) {
		this.setHibernateTemplate(new HibernateTemplate(sessionFactory));
		session = sessionFactory.openStatelessSession();
	}

	/*
	 * This getUploadedFiles method retrieves all the files that satisfy the provided criteria and returns the list of those files
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UploadedFileRecord> getUploadedFiles(final FileStatusEnum fileStat, final FileTypeEnum fileType, final String fileCat) {
		final Criteria criteria = session.createCriteria(UploadedFileRecord.class);
		criteria.add(Restrictions.eq("fileCat", fileCat));

		final Criteria typeAndStatusCriteria = criteria.createCriteria("uploadedFileRecordWork");
		typeAndStatusCriteria.add(Restrictions.eq("fileWorkType", fileType));
		typeAndStatusCriteria.add(Restrictions.eq("processingStatus", fileStat));

		final List<UploadedFileRecord> uploadedFileList = criteria.list();
		Assert.notNull(uploadedFileList, "Query on UPLOADED_FILE failed");

		return uploadedFileList;
	}

	/*
	 * Updates the uploaded file status to the provided status using its file ID
	 */
	@Override
	public void updateUploadedFileRecord(final UploadedFileRecord fileRecord) {
		this.getHibernateTemplate().update(fileRecord.getUploadedFileRecordWork());
		this.getHibernateTemplate().flush();
		this.getHibernateTemplate().clear();
		logger.info("File named: ["+fileRecord.getUploadedFileName()+"] with FILE_UPLOADED_ID: ["+fileRecord.getUploadedFileId()+"] "
				+ "has been updated to ["+ fileRecord.getUploadedFileRecordWork().getProcessingStatus() +"]");
	}

	/*
	 * Just returns a uploaded file object that has provided file ID
	 */
	@Override
	public UploadedFileRecord getUploadedFileFromTable(final Long fileId) {
		final Criteria criteria = session.createCriteria(UploadedFileRecord.class);
		criteria.add(Restrictions.eq("uploadedFileId", fileId));
		return (UploadedFileRecord) criteria.uniqueResult();
	}
}
