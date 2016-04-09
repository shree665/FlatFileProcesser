package subedi.flatfile.persistence;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author vivek.subedi
 *
 */

public class DataContainer implements Serializable {

	private static final long serialVersionUID = -8375536692854225974L;
	
	private JobControl jobControl;
	private Map<String, String> ccmData;
	
	
	public JobControl getJobControl() {
		return jobControl;
	}
	
	public void setJobControl(JobControl jobControl) {
		this.jobControl = jobControl;
	}
	
	public Map<String, String> getCcmData() {
		return ccmData;
	}
	
	public void setCcmData(Map<String, String> ccmData) {
		this.ccmData = ccmData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ccmData == null) ? 0 : ccmData.hashCode());
		result = prime * result + ((jobControl == null) ? 0 : jobControl.hashCode());
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
		DataContainer other = (DataContainer) obj;
		if (ccmData == null) {
			if (other.ccmData != null)
				return false;
		} else if (!ccmData.equals(other.ccmData))
			return false;
		if (jobControl == null) {
			if (other.jobControl != null)
				return false;
		} else if (!jobControl.equals(other.jobControl))
			return false;
		return true;
	}
	
	public String toString() {
		return ccmData.toString();
	}
}
