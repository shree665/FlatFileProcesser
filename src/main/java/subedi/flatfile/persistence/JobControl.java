package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import subedi.flatfile.persistence.enumerated.BehaviorEnum;
import subedi.flatfile.persistence.enumerated.FrequencyEnum;

/**
 * 
 * @author vivek.subedi
 *
 */
@Entity
@Table(name = "JOB_CTRL")
public class JobControl implements Serializable {

private static final long serialVersionUID = -3949770379154937990L;
	
	@Id
	@Column(name = "JOB_CTRL_ID")
	private Integer jobControlId;
	
	@Column(name = "JOB_NAME")
	private String jobName;
	
	@Column(name = "DB_CD")
	private String databaseCode;
	
	@Column(name = "TBL_NAME")
	private String tableName;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "FREQ_CD")
	private FrequencyEnum frequencyCode;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "BEHAV_CD")
	private BehaviorEnum behaviorCode;
	
	@Column(name = "ID_COL_NAME")
	private String idColumnName;
	
	@Column(name = "UPDT_COL_NAME")
	private String updatedColumnName;
	
	@Column(name = "CRTD_COL_NAME")
	private String createdColumnName;
	
	public Integer getJobControlId() {
		return jobControlId;
	}
	
	public void setJobControlId(Integer jobControlId) {
		this.jobControlId = jobControlId;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getDatabaseCode() {
		return databaseCode;
	}
	
	public void setDatabaseCode(String databaseCode) {
		this.databaseCode = databaseCode;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public FrequencyEnum getFrequencyCode() {
		return frequencyCode;
	}
	
	public void setFrequencyCode(FrequencyEnum frequencyCode) {
		this.frequencyCode = frequencyCode;
	}
	
	public BehaviorEnum getBehaviorCode() {
		return behaviorCode;
	}
	
	public void setBehaviorCode(BehaviorEnum behaviorCode) {
		this.behaviorCode = behaviorCode;
	}
	
	public String getIdColumnName() {
		return idColumnName;
	}
	
	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}
	
	public String getUpdatedColumnName() {
		return updatedColumnName;
	}
	
	public void setUpdatedColumnName(String updatedColumnName) {
		this.updatedColumnName = updatedColumnName;
	}
	
	public String getCreatedColumnName() {
		return createdColumnName;
	}
	
	public void setCreatedColumnName(String createdColumnName) {
		this.createdColumnName = createdColumnName;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((behaviorCode == null) ? 0 : behaviorCode.hashCode());
		result = prime * result + ((createdColumnName == null) ? 0 : createdColumnName.hashCode());
		result = prime * result + ((databaseCode == null) ? 0 : databaseCode.hashCode());
		result = prime * result + ((frequencyCode == null) ? 0 : frequencyCode.hashCode());
		result = prime * result + ((idColumnName == null) ? 0 : idColumnName.hashCode());
		result = prime * result + ((jobControlId == null) ? 0 : jobControlId.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((updatedColumnName == null) ? 0 : updatedColumnName.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobControl other = (JobControl) obj;
		if (behaviorCode != other.behaviorCode)
			return false;
		if (createdColumnName == null) {
			if (other.createdColumnName != null)
				return false;
		} else if (!createdColumnName.equals(other.createdColumnName))
			return false;
		if (databaseCode == null) {
			if (other.databaseCode != null)
				return false;
		} else if (!databaseCode.equals(other.databaseCode))
			return false;
		if (frequencyCode != other.frequencyCode)
			return false;
		if (idColumnName == null) {
			if (other.idColumnName != null)
				return false;
		} else if (!idColumnName.equals(other.idColumnName))
			return false;
		if (jobControlId == null) {
			if (other.jobControlId != null)
				return false;
		} else if (!jobControlId.equals(other.jobControlId))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (updatedColumnName == null) {
			if (other.updatedColumnName != null)
				return false;
		} else if (!updatedColumnName.equals(other.updatedColumnName))
			return false;
		return true;
	}
}
