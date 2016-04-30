/**
 * 
 */
package subedi.flatfile.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;

import subedi.flatfile.persistence.DataContainer;
import subedi.flatfile.persistence.JobControl;
import subedi.flatfile.service.JobCtrlService;

/**
 * Mapper to map the each record of a file.
 * 
 * @author vivek.subedi
 * @param <T>
 *
 */
public class FlatFileMapper implements FieldSetMapper<Object>, InitializingBean{
	
	private JobControl jobControl;
	private Integer jobControlId;
	
	@Autowired
	private JobCtrlService jobCtrlService;
	
	@Override
	public Object mapFieldSet(FieldSet fieldSet) throws BindException {
		DataContainer ccmContainer = new DataContainer();
		Map<String, String> data = new HashMap<String, String>();
		String[] names = fieldSet.getNames();
		String[] values = fieldSet.getValues();
		
		for (int i = 0; i < names.length; i++) {
			data.put(names[i].trim(), values[i].trim());
		}
 		ccmContainer.setCcmData(data);
 		ccmContainer.setJobControl(jobControl);
		
		return ccmContainer;
	}
	
	public void setJobControlId(Integer jobControlId) {
		this.jobControlId = jobControlId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		jobControl = jobCtrlService.getJobControl(jobControlId);
		
	}

}

