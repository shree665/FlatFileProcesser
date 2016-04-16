/**
 * 
 */
package subedi.flatfile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import subedi.flatfile.dao.RefDataDao;
import subedi.flatfile.persistence.RefData;

/**
 * @author vivek.subedi
 *
 */
@Service
public class RefDataServiceImpl implements RefDataService {
	
	@Autowired
	private RefDataDao refDataDao;
	
	private HashMap<String, List<RefData>> cachedRefData;

	@Override
	public List<RefData> getRefDataForDatabase(String mappingName) {
		
		String key = mappingName;
		
		if (cachedRefData == null) {
			cachedRefData = new HashMap<String, List<RefData>>();
		}
		
		if (cachedRefData.containsKey(key)) {
			return cachedRefData.get(key);
		}else {
			
			List<RefData> allRefDatas = refDataDao.getRefDataforDatabase(mappingName);
			cachedRefData.put(key, allRefDatas);
			
			return allRefDatas;
		}
		
	}
	
	@Override
	public List<RefData> getRefDataForTable(String mappingName, String fileTableName) {
		
		List<RefData> allRefData = getRefDataForDatabase(mappingName);
		List<RefData> db2RefData = new ArrayList<RefData>();
		
		for (RefData refData : allRefData) {
			if (refData.getId().getTableNameFromFile().equalsIgnoreCase(fileTableName)) {
				db2RefData.add(refData);
			}
		}
		return db2RefData;
	}
	
	public void setRefDataDao(RefDataDao refDataDao) {
		this.refDataDao = refDataDao;
	}

	

}
